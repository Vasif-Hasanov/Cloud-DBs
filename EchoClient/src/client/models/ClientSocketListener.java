package client.models;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import commons.communication.SocketListener;
import commons.communication.SocketStatus;
import commons.data.EchoMessage;
import commons.data.KVMessage;
import commons.data.Message;
import commons.data.OneWayMessage;

public class ClientSocketListener  implements SocketListener {
	public static final String PROMT = "EchoClient> ";
	private PrintStream userOut;
	private ClientLogic clientLogic;
	private static final Logger LOGGER = Logger.getLogger(ClientSocketListener.class);
	
	public ClientSocketListener(PrintStream userOut, ClientLogic clientLogic) {
		this.userOut = userOut;
		this.clientLogic = clientLogic;
	}

	@Override
	public void handleNewMessage(Message msg) {
		LOGGER.info("Received: " + msg.toString());
		userOut.print(PROMT);
		if (msg.isEchoMessage()) {
			handleEchoMessage((EchoMessage) msg);
		}
		if (msg.isKeyValueMessage()) {
			handleKeyValueMessage((KVMessage) msg);
		}
		if (msg.isOneWayMessage()) {
			handleOneWayMessage((OneWayMessage) msg);
		}
		userOut.print(PROMT);
	}

	private void handleOneWayMessage(OneWayMessage msg) {
		switch (msg.status) {
		case PARAMETER_ERROR:
			userOut.println("Parameters are not valid.");
			break;
		case SERVER_STOPPED:
			userOut.println("Server is not serving client requests.");
			break;
		default:
			break;
		}
	}

	private void handleKeyValueMessage(KVMessage msg) {
		switch (msg.status) {
		case SERVER_AT_WRITE_LOCK:
			userOut.println("Server is at write lock.");
			break;
		case GET_ERROR:
			userOut.println("There is not such element.");
			break;
		case GET_SUCCESS:
			userOut.println(msg.value + " is retrived for the key: " + msg.key);
			break;
		case PUT_SUCCESS:
			userOut.println(msg.key + " - " + msg.value + " pair is inserted.");
			break;
		case PUT_UPDATE:
			userOut.println(msg.key + " - " + msg.value + " pair is updated.");
			break;
		case SERVER_NOT_RESPONSIBLE_FOR_GET:
			clientLogic.setRunningServerNodes(msg.runningServerNodes);
			try {
				clientLogic.asyncGet(msg.key);
			} catch (Exception e) {
				userOut.println("Unexpected exception occured.");
				LOGGER.error(e);
			}
			break;
		case SERVER_NOT_RESPONSIBLE_FOR_PUT:
			clientLogic.setRunningServerNodes(msg.runningServerNodes);
			try {
				clientLogic.asyncPut(msg.key, msg.value);
			} catch (Exception e) {
				userOut.println("Unexpected exception occured.");
				LOGGER.error(e);
			}
			break;
		default:
			break;
		}
	}

	private void handleEchoMessage(EchoMessage msg) {
		switch (msg.status) {
		case ECHO_SUCCESS:
			userOut.println(msg.text);
			break;
		default:
			break;
		}
	}

	@Override
	public void handleStatus(SocketStatus status) {
		switch (status) {
		case CONNECTED:
			LOGGER.info("CONNECTED");
			break;
		case CONNECTION_LOST:
			userOut.print(PROMT);
			userOut.println("Connection terminated for the unknown reasons.");
			userOut.print(PROMT);
			LOGGER.info("CONNECTION LOST");
			break;
		case DISCONNECTED:
			userOut.print(PROMT);
			userOut.println("Disconnected from server.");
			userOut.print(PROMT);
			LOGGER.info("DISCONNECTED");
			break;
		default:
			break;
		}
	}
}
