package server.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import client.models.ClientLogic;
import client.models.MetaInfoHolder;

import commons.communication.ClientDialogue;
import commons.data.CommandToServerMessage;
import commons.data.MessageStatusType;
import commons.metaData.ServerInfo;
import commons.utils.UtilMethods;

public class ServerForConfigLogic {
	private ServerForClientLogic serverForClientLogic;
	private Server server;
	public ServerForConfigLogic(Server server, ServerForClientLogic serverForClientLogic){
		this.server = server;
		this.serverForClientLogic = serverForClientLogic;
	}
	
	public void moveData(ServerInfo destNode, MessageStatusType status) throws Exception {
		
		ServerInfo currentNode = UtilMethods.getServerInfoByName(
				server.serverName, server.runningServerNodes);
		Hashtable<String, String> allData = serverForClientLogic.getAllData();
		System.out.println("1");
		moveData(allData, currentNode, destNode);		
		System.out.println("7");
		sendMovedDataAcknowledgementMessage(status);
	}

	public void sendMovedDataAcknowledgementMessage(MessageStatusType status)
			throws Exception, IOException {
		ClientDialogue dialogue = new ClientDialogue();
		dialogue.open(server.configServerInfo.getServerIp(), server.configServerInfo.getPort(), false);
		
		CommandToServerMessage responce = new CommandToServerMessage(status);
		responce.serverName = server.serverName;
		dialogue.asyncSend(responce);
		
		dialogue.close();
	}

	public void releaseWriteLock() {
		server.lockedForWrite = false;
	}

	public void setWriteLock() {
		server.lockedForWrite = true;
	}

	public void deleteOldData() {
		ServerInfo currentNode = UtilMethods.getServerInfoByName(
				server.serverName, server.runningServerNodes);
		serverForClientLogic.deleteOldData(currentNode);
	}

	public void updateMetaInfo(ArrayList<ServerInfo> runningServerNodes, ServerInfo configServerInfo) {
		server.runningServerNodes = runningServerNodes;
		server.configServerInfo = configServerInfo;
	}

	public void start(String serverName,
			ArrayList<ServerInfo> runningServerNodes, ServerInfo configServerInfo) {
		server.runningServerNodes = runningServerNodes;
		server.configServerInfo = configServerInfo;
		server.serverName = serverName;
		server.holding = false;
	}

	public void shutdownServer() {
		server.stopServer();
		System.exit(1);
	}
	
	private void moveData(Hashtable<String, String> allData,
			 ServerInfo currentNode, ServerInfo destNode) throws Exception {
		System.out.println("2");
		ClientLogic client = new ClientLogic(createMetaInfoHolder());
		System.out.println("3");
		client.connect(destNode.getServerIp(), destNode.getPort(), false);
		System.out.println("4");
		for (String key : allData.keySet()) {
			if (destNode.isInsideKeyRange(key)) {
				client.put(key, allData.get(key));
			}
		}
		client.disconnect();
		System.out.println("6");
	}

	private MetaInfoHolder createMetaInfoHolder() {
		MetaInfoHolder holder = new MetaInfoHolder() {			
			@Override
			public void setRunningServerNodes(ArrayList<ServerInfo> runningServerNodes) {
				//empty
			}
			
			@Override
			public void setConnectedServer(ServerInfo serverInfo) {
				//empty
			}
			
			@Override
			public ArrayList<ServerInfo> getRunningServerNodes() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ServerInfo getConnectedServer() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return holder;
	}

	public void stopServer() {
		server.holding = true;
	}

}
