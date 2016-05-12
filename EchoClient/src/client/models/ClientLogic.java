package client.models;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import commons.communication.ClientDialogue;
import commons.communication.SocketListener;
import commons.data.EchoMessage;
import commons.data.KVMessage;
import commons.data.MessageStatusType;
import commons.exceptions.AnException;
import commons.exceptions.InvalidParameterException;
import commons.metaData.ServerInfo;
import commons.utils.UtilMethods;


public class ClientLogic implements KeyValueCommInterface{
	protected final Logger LOGGER = Logger.getLogger(ClientLogic.class);	
	private ClientDialogue clientDialogue = new ClientDialogue();
	private MetaInfoHolder metaInfoHolder;
	
	public ClientLogic(MetaInfoHolder metaInfoHolder) {
		this.metaInfoHolder = metaInfoHolder;
		clientDialogue.isTolerant = true;
	}

	public void addListener(SocketListener listener) {
		clientDialogue.addListener(listener);
	}
	
	@Override
	public void connect(String host, int port, boolean isAsync) throws Exception {
		connect(new ServerInfo("unknown", host, port), isAsync);
	}
	
	public void connect(ServerInfo serverInfo, boolean isAsync) throws Exception {
		clientDialogue.open(serverInfo.getServerIp(), serverInfo.getPort(), isAsync);
		metaInfoHolder.setConnectedServer(serverInfo);
	}

	@Override
	public void disconnect() {
		try {
			clientDialogue.close();
		} catch (IOException e) {
			LOGGER.error("Connection closing error", e);
		}
	}

	@Override
	public KVMessage put(String key, String value) throws Exception {
		fixConnection(key);
		KVMessage msg = new KVMessage(key, value, MessageStatusType.PUT);
		msg.validate();
		KVMessage responce = (KVMessage) clientDialogue.send(msg);
		if (responce.status == MessageStatusType.SERVER_NOT_RESPONSIBLE_FOR_PUT){
			metaInfoHolder.setRunningServerNodes(responce.runningServerNodes);
			return put(key, value);
		}
		return responce;
	}

	@Override
	public KVMessage get(String key) throws Exception {
		fixConnection(key);
		KVMessage msg = new KVMessage(key, null, MessageStatusType.GET);
		msg.validate();
		KVMessage responce = (KVMessage) clientDialogue.send(msg);
		if (responce.status == MessageStatusType.SERVER_NOT_RESPONSIBLE_FOR_GET){
			metaInfoHolder.setRunningServerNodes(responce.runningServerNodes);
			return get(key);
		}
		return responce;
	}
	
	public void asyncGet(String key) throws Exception {
		fixConnection(key);
		KVMessage msg = new KVMessage(key, null, MessageStatusType.GET);
		msg.validate();
		clientDialogue.asyncSend(msg);
	}
	
	public void asyncPut(String key, String value) throws Exception {
		fixConnection(key);
		KVMessage msg = new KVMessage(key, value, MessageStatusType.PUT);
		msg.validate();
		clientDialogue.asyncSend(msg);
	}
	
	public void asyncSend(String messageBody) throws InvalidParameterException, IOException {
		EchoMessage msg = new EchoMessage(messageBody, MessageStatusType.ECHO);
		msg.validate();
		clientDialogue.asyncSend(msg);
	}

	public void setRunningServerNodes(ArrayList<ServerInfo> runningServerNodes){
		metaInfoHolder.setRunningServerNodes(runningServerNodes);
	}
	
	private void fixConnection(String key) throws IOException, AnException,Exception {
		ServerInfo correctServerInfo = UtilMethods.getServerInfoByKey(key, metaInfoHolder.getRunningServerNodes());
		boolean connected = (metaInfoHolder.getRunningServerNodes() == null  
				|| (metaInfoHolder.getConnectedServer() != null && metaInfoHolder.getConnectedServer().equals(correctServerInfo)))&& clientDialogue.isOpen();
		if (!connected){
			clientDialogue.closeStream();
			if (correctServerInfo == null){
				throw new AnException("Correct server not found.");
			}
			connect(correctServerInfo, false);
		}
	}
}
