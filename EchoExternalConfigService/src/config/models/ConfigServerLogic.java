package config.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;

import commons.data.MessageStatusType;
import commons.exceptions.InvalidConfigException;
import commons.metaData.ServerInfo;
import commons.metaData.ServerInfoComparator;
import commons.metaData.ServerNodeStatus;
import commons.utils.UtilMethods;

public class ConfigServerLogic extends Thread {
	private ConfigServerToServerLogic	toServerLogic;
	private RunnerScript toComputerLogic = new RunnerScript();
	private ServerInfoComparator comparator = new ServerInfoComparator();
	private ConfigServer configServer;
	
	public ConfigServerLogic(ConfigServer configServer) {
		this.configServer = configServer;
		toServerLogic = new ConfigServerToServerLogic(configServer);
	}

	public AddingOrRemovingResult addServerNodeBegin() throws Exception{
		ServerInfo node = getServerNodeByStatus(ServerNodeStatus.IDLE);
		if (node != null){
			boolean completed = addServerNodeBegin(node);
			return new AddingOrRemovingResult(node.getServerName(),completed);
		}else{
			return new AddingOrRemovingResult(null, false);
		}
	}
	
	public ServerInfo addServerNodeEnd(String serverName) throws Exception{
		ServerInfo successor = UtilMethods.getServerInfoByName(serverName, configServer.runningServerNodes);
		ServerInfo node = getPre(successor);
		updateMetaInfos(node);
		toServerLogic.releaseWriteLock(successor);
		toServerLogic.deleteOldData(successor);
		return node;
	}

	public void updateMetaInfos(ServerInfo node) throws Exception {
		for(ServerInfo item: configServer.runningServerNodes){
			if (item != node){
				toServerLogic.updateMetaInfo(item);
			}
		}
	}
	
	public void updateMetaInfos(String serverName) throws Exception {
		ServerInfo node = UtilMethods.getServerInfoByName(serverName, configServer.serverNodes);
		updateMetaInfos(node);
	}

	public String removeServerNodeBegin() throws Exception{
		String serverName = null;
		if (configServer.runningServerNodes.size() > 1){			
			ServerInfo node = getServerNodeByStatus(ServerNodeStatus.RUNNING);
			if (node == null){
				node = getServerNodeByStatus(ServerNodeStatus.STOPPED);
				toServerLogic.start(node, configServer.runningServerNodes);
				Thread.sleep(1000);
			}
			serverName = node.getServerName();
			ServerInfo successor = determinePositionAfterRemoving(node);
			toServerLogic.setWriteLock(node);
			toServerLogic.updateMetaInfo(successor);
			toServerLogic.moveData(node, successor, MessageStatusType.REMOVE_MOVE_DATA);
		}
		return serverName;
	}
	
	public void removeServerNodeEnd(String serverName) throws Exception{
		ServerInfo node = UtilMethods.getServerInfoByName(serverName, configServer.serverNodes);
		ServerInfo successor = UtilMethods.getServerInfoByHashFrom(node.getHashFrom(), configServer.runningServerNodes);
		updateMetaInfos(successor);
		toServerLogic.shutdownServer(node);
	}

	//first server in config file is config server
	public void initServers(String filePath) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line = null;
		boolean firstLine=true;
		while ((line = reader.readLine()) != null) {
		    String[] tokens = UtilMethods.getTokens(line);
		    if(firstLine){
		    	firstLine=false;
		    	if (tokens.length != 3){
			    	throw new InvalidConfigException();
			    }
		    	configServer.serverNodes.add(new ServerInfo(tokens[0], tokens[1], Integer.parseInt(tokens[2])));
		    }else{
		    	if (tokens.length != 5){
			    	throw new InvalidConfigException();
			    }
			    configServer.serverNodes.add(new ServerInfo(tokens[0], tokens[1], Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3]),tokens[4]));
		    }
		    

		}
		if (configServer.serverNodes.size() < 2){
			throw new InvalidConfigException();
		}
		configServer.configServerInfo = configServer.serverNodes.get(0);
		configServer.serverNodes.remove(0);
		launchHalfOfNodes();
	}
	public void startAll(){
		for(ServerInfo item:configServer.runningServerNodes){
			try {
				toServerLogic.start(item, configServer.runningServerNodes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void shutdownAll(){
		for(ServerInfo item:configServer.runningServerNodes){
			try {
				toServerLogic.shutdownServer(item);
				item.setStatus(ServerNodeStatus.IDLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		configServer.serverNodes.clear();
		configServer.runningServerNodes.clear();
	}

	public void stopAll(){
		for(ServerInfo item:configServer.runningServerNodes){
			try {
				toServerLogic.stop(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private ServerInfo launchServer(ServerInfo node) throws Exception {
		toComputerLogic.runJarWithSSH(node);
		ServerInfo successor = determinePositionAfterAdding(node);	
		return successor;
	}	
	
	private ServerInfo getSuccessor(ServerInfo node) {
		ServerInfo successor = null;
		for(int i = 0; i < configServer.runningServerNodes.size(); i++){
			if (configServer.runningServerNodes.get(i).equals(node)){
				successor = configServer.runningServerNodes.get((i + 1) % configServer.runningServerNodes.size());
			}
		}
		return successor;
	}
	

	private ServerInfo getPre(ServerInfo node) {
		ServerInfo pre = null;
		int size = configServer.runningServerNodes.size();
		for(int i = 0; i < size; i++){
			if (configServer.runningServerNodes.get(i).equals(node)){
				int index = (i + size - 1) % size;
				pre = configServer.runningServerNodes.get(index);
			}
		}
		return pre;
	}

	private ServerInfo determinePositionAfterRemoving(ServerInfo node) {
		ServerInfo successor = getSuccessor(node);
		successor.setHashFrom(node.getHashFrom());
		node.setStatus(ServerNodeStatus.IDLE);
		configServer.runningServerNodes.remove(node);
		return successor;
	}

	private boolean addServerNodeBegin(ServerInfo node) throws Exception {
		ServerInfo successor = launchServer(node);
		toServerLogic.start(node, configServer.runningServerNodes);
		Thread.sleep(1000);
		if (successor != null && successor.getStatus() == ServerNodeStatus.RUNNING){
			toServerLogic.setWriteLock(successor);
			toServerLogic.moveData(successor, node, MessageStatusType.ADD_MOVE_DATA);
			return false;
		}else{
			return true;
		}
	}

	private ServerInfo determinePositionAfterAdding(ServerInfo node) {
		ServerInfo successor = null;
		node.setHashTo(node.getHash());
		if (configServer.runningServerNodes.size() == 0){
			node.setHashFrom(node.getHashTo());
		}else{
			successor = getServerInfoByKey(node.getDesc());
			node.setHashFrom(successor.getHashFrom());
			successor.setHashFrom(node.getHashTo());
		}
		node.setStatus(ServerNodeStatus.STOPPED);
		configServer.runningServerNodes.add(node);
		Collections.sort(configServer.runningServerNodes, comparator);
		return successor;
	}
	
	private ServerInfo getServerNodeByStatus(ServerNodeStatus status){
		for(ServerInfo item: configServer.serverNodes){
			if (item.getStatus() == status){
				return item;
			}
		}
		return null;
	}

	private ServerInfo getServerInfoByKey(String key){
		return UtilMethods.getServerInfoByKey(key, configServer.runningServerNodes);
	}

	private void launchHalfOfNodes() throws Exception {
		int launchingServersCount = configServer.serverNodes.size() / 2;
		if (launchingServersCount == 0){
			launchingServersCount = 1;
		}
		for(int i = 0; i < launchingServersCount; i++){
			ServerInfo node = configServer.serverNodes.get(i);
			launchServer(node);
		}
	}

	
}
