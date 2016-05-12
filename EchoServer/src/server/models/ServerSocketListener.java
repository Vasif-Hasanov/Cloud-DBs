package server.models;

import java.io.IOException;

import org.apache.log4j.Logger;

import commons.communication.ServerDialogue;
import commons.communication.SocketListener;
import commons.communication.SocketStatus;
import commons.data.CommandToServerMessage;
import commons.data.EchoMessage;
import commons.data.KVMessage;
import commons.data.Message;
import commons.data.MessageStatusType;
import commons.data.OneWayMessage;
import commons.metaData.ServerInfo;
import commons.utils.UtilMethods;

public class ServerSocketListener implements SocketListener {
	private final Logger LOGGER = Logger.getLogger(this.getClass());
	private ServerDialogue dialogue;
	private ServerForClientLogic serverForClientLogic;
	private ServerForConfigLogic serverForConfigLogic;
	private Server server;
	public ServerSocketListener(Server server, ServerDialogue dialogue) {
		this.server = server;
		this.dialogue = dialogue;
		serverForClientLogic = new ServerForClientLogic(dialogue, server);
		serverForConfigLogic = new ServerForConfigLogic(server, serverForClientLogic);
	}
	
	public ServerSocketListener(Server server, ServerDialogue dialogue, int cacheSize, String cacheType) {
		this.server = server;
		this.dialogue = dialogue;
		serverForClientLogic = new ServerForClientLogic(dialogue, server,cacheSize,cacheType);
		serverForConfigLogic = new ServerForConfigLogic(server, serverForClientLogic);
	}

	@Override
	public void handleNewMessage(Message msg) throws Exception {
		LOGGER.info("Received: " + msg.toString());
		if (msg.isEchoMessage()) {
			if (server.holding) {
				handleRequestsInStoppedMode();
				return;
			}
			handleEchoMessage((EchoMessage) msg);
		}
		if (msg.isKeyValueMessage()) {
			if (server.holding) {
				handleRequestsInStoppedMode();
				return;
			}
			handleKeyValueMessage((KVMessage) msg);
		}
		if (msg.isCommandToServerMessage()) {
			handleCommandToServerMessage((CommandToServerMessage) msg);
		}
	}

	private void handleRequestsInStoppedMode() throws IOException {
		OneWayMessage message = new OneWayMessage("In stopped mode.",
				MessageStatusType.SERVER_STOPPED);
		dialogue.asyncSend(message);
	}

	private void handleCommandToServerMessage(CommandToServerMessage msg) throws Exception {
		switch (msg.status) {
		case START:
			serverForConfigLogic.start(msg.serverName, msg.runningServerNodes, msg.configServerInfo);
			break;
		case UPDATE_META_INFO:
			serverForConfigLogic.updateMetaInfo(msg.runningServerNodes, msg.configServerInfo);
			break;
		case DELETE_OLD_DATA:
			serverForConfigLogic.deleteOldData();
			break;
		case SET_WRITE_LOCK:
			serverForConfigLogic.setWriteLock();
			break;
		case RELEASE_WRITE_LOCK:
			serverForConfigLogic.releaseWriteLock();
			break;
		case ADD_MOVE_DATA:
			serverForConfigLogic.moveData(msg.destServerInfo, MessageStatusType.ADD_DATA_MOVED);	
			break;
		case REMOVE_MOVE_DATA:
			serverForConfigLogic.moveData(msg.destServerInfo, MessageStatusType.REMOVE_DATA_MOVED);
			break;
		case STOP:
			serverForConfigLogic.stopServer();
			break;
		case SHUTDOWN_SERVER:
			serverForConfigLogic.shutdownServer();
			break;
		default:
			break;
		}
	}

	private void handleEchoMessage(EchoMessage msg) throws IOException {
		msg.status = MessageStatusType.ECHO_SUCCESS;
		dialogue.asyncSend(msg);
	}

	private void handleKeyValueMessage(KVMessage msg) throws IOException {
		ServerInfo currentNode = UtilMethods.getServerInfoByName(
				server.serverName, server.runningServerNodes);
		if (!currentNode.isInsideKeyRange(msg.key)) {
			handleDestinationFalseRequests(msg);
			return;
		}
		switch (msg.status) {
		case GET:
			serverForClientLogic.get(msg);
			break;
		case PUT:			
			serverForClientLogic.put(msg);
			break;
		default:
			break;
		}
	}

	private void handleDestinationFalseRequests(KVMessage msg) throws IOException {
		switch (msg.status) {
		case GET:
			msg.status = MessageStatusType.SERVER_NOT_RESPONSIBLE_FOR_GET;
			break;
		case PUT:			
			msg.status = MessageStatusType.SERVER_NOT_RESPONSIBLE_FOR_PUT;
			break;
		default:
			break;
		}
		
		msg.runningServerNodes = server.runningServerNodes;
		dialogue.asyncSend(msg);
	}

	@Override
	public void handleStatus(SocketStatus status) {
		switch (status) {
		case CONNECTED:
			LOGGER.info("CONNECTED");
			break;
		case CONNECTION_LOST:
			LOGGER.info("CONNECTION LOST");
			break;
		case DISCONNECTED:
			LOGGER.info("DISCONNECTED");
			break;
		default:
			break;
		}
	}

}
