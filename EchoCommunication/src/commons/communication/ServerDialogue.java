package commons.communication;

import java.io.IOException;
import java.net.Socket;

import commons.data.Message;
import commons.data.MessageStatusType;
import commons.data.OneWayMessage;
import commons.exceptions.InvalidParameterException;


public class ServerDialogue extends Dialogue {

	/**
	 * Constructs a new CientConnection object for a given TCP socket.
	 * @param clientSocket the Socket object for the client connection.
	 */
	public ServerDialogue(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	/**
	 * Initializes and starts the client connection. 
	 * Loops until the connection is closed or aborted by the client.
	 */
	@Override
	public void run() {
		try {
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			
			while(isOpen()) {
				try {
					Message latestMsg = receiveMessage();
					latestMsg.validate();
					handleMessage(latestMsg);
					
				/* connection either terminated by the client or lost due to 
				 * network problems*/	
				} catch (InvalidParameterException ipe) {
					LOGGER.error("Error! Invalid parameters!", ipe);
					asyncSend(new OneWayMessage(ipe.getMessage(), MessageStatusType.PARAMETER_ERROR));
				} catch (IOException ioe) {
					LOGGER.error("Error! Connection lost!");
					close();
				} catch (ClassNotFoundException e) {
					LOGGER.error("Error! Unknown message!");
					close();
				}				
			}
			
		} catch (IOException ioe) {
			LOGGER.error("Error! Connection could not be established!", ioe);			
		} finally {			
			try {
				close();
			} catch (IOException ioe) {
				LOGGER.error("Error! Unable to tear down connection!", ioe);
			}
		}
	}

}
