package config.models;

import java.util.ArrayList;

import commons.communication.ClientDialogue;
import commons.data.CommandToServerMessage;
import commons.data.MessageStatusType;
import commons.metaData.ServerInfo;
import commons.metaData.ServerNodeStatus;

public class ConfigServerToServerLogic{
	private ConfigServer configServer;
	
	public ConfigServerToServerLogic(ConfigServer configServer) {
		this.configServer = configServer;
	}

	public void updateMetaInfo(ServerInfo node) throws Exception {
		ClientDialogue dialogue = new ClientDialogue();
		dialogue.open(node.getServerIp(), node.getPort(), false);
		
		CommandToServerMessage msg = new CommandToServerMessage(MessageStatusType.UPDATE_META_INFO);
		msg.runningServerNodes = configServer.runningServerNodes;
		msg.configServerInfo = configServer.configServerInfo;
		dialogue.asyncSend(msg);
		
		dialogue.close();
	}
	
	public void deleteOldData(ServerInfo node) throws Exception {
		ClientDialogue dialogue = new ClientDialogue();
		dialogue.open(node.getServerIp(), node.getPort(), false);
		
		CommandToServerMessage msg = new CommandToServerMessage(MessageStatusType.DELETE_OLD_DATA);
		dialogue.asyncSend(msg);
		
		dialogue.close();
	}

	public void releaseWriteLock(ServerInfo node) throws Exception {
		node.setStatus(ServerNodeStatus.RUNNING);
		ClientDialogue dialogue = new ClientDialogue();
		dialogue.open(node.getServerIp(), node.getPort(), false);
		
		CommandToServerMessage msg = new CommandToServerMessage(MessageStatusType.RELEASE_WRITE_LOCK);
		dialogue.asyncSend(msg);
		
		dialogue.close();
	}
	
	public void moveData(ServerInfo from, ServerInfo to, MessageStatusType status) throws Exception {
		ClientDialogue dialogue = new ClientDialogue();
		dialogue.open(from.getServerIp(), from.getPort(), false);
		
		CommandToServerMessage msg = new CommandToServerMessage(status);
		msg.destServerInfo = to;
		dialogue.asyncSend(msg);
		
		dialogue.close();
	}

	public void setWriteLock(ServerInfo node) throws Exception {
		node.setStatus(ServerNodeStatus.WRITE_LOCK);
		ClientDialogue dialogue = new ClientDialogue();
		dialogue.open(node.getServerIp(), node.getPort(), false);
		
		CommandToServerMessage msg = new CommandToServerMessage(MessageStatusType.SET_WRITE_LOCK);
		dialogue.asyncSend(msg);
		
		dialogue.close();
	}
	
	public void start(ServerInfo node, ArrayList<ServerInfo> runningServerNodes) throws Exception{
		node.setStatus(ServerNodeStatus.RUNNING);
		ClientDialogue dialogue = new ClientDialogue();
		dialogue.open(node.getServerIp(), node.getPort(), false);
		
		CommandToServerMessage msg = new CommandToServerMessage(MessageStatusType.START);
		msg.runningServerNodes = configServer.runningServerNodes;
		msg.configServerInfo = configServer.configServerInfo;
		msg.serverName = node.getServerName();
		dialogue.asyncSend(msg);
		
		dialogue.close();
	}
	
	public void shutdownServer(ServerInfo node) throws Exception{
		node.setStatus(ServerNodeStatus.IDLE);
		ClientDialogue dialogue = new ClientDialogue();
		dialogue.open(node.getServerIp(), node.getPort(), false);
		
		CommandToServerMessage msg = new CommandToServerMessage(MessageStatusType.SHUTDOWN_SERVER);
		dialogue.asyncSend(msg);
		
		dialogue.close();
	}

	public void stop(ServerInfo node) throws Exception{
		node.setStatus(ServerNodeStatus.STOPPED);
		ClientDialogue dialogue = new ClientDialogue();
		dialogue.open(node.getServerIp(), node.getPort(), false);
		
		CommandToServerMessage msg = new CommandToServerMessage(MessageStatusType.STOP);
		dialogue.asyncSend(msg);
		
		dialogue.close();
	}

}