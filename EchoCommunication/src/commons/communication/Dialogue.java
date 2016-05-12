package commons.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import commons.data.Message;

public abstract class Dialogue implements Runnable {
	protected static Thread thread = null;
	protected List<SocketListener> listeners = new ArrayList<SocketListener>();
	protected volatile Socket clientSocket = null;
	protected volatile OutputStream out = null;
	protected volatile InputStream in = null;
	protected String host;
	protected int port;
	protected final Logger LOGGER = Logger.getLogger(this.getClass());	

	@SuppressWarnings("deprecation")
	private void stopThread() {
		if (thread != null && thread.isAlive()){
			thread.stop();
		}
	}

	public void close() throws IOException {
		if (isOpen()){
			stopThread();
			clientSocket.close();
			handleSocketStatus(SocketStatus.CONNECTION_LOST);
		}
	}
	
	public void closeStream() throws IOException {
		if (isOpen()){
			clientSocket.close();
		}
	}

	public void asyncSend(Message request) throws IOException {
		if (isOpen()){
			LOGGER.info("Sent: " + request.toString());
			ObjectOutputStream objectOutFromServer = new ObjectOutputStream(out);
			objectOutFromServer.writeObject(request);
			out.flush();
		}else{
			throw new IOException("Connection is closed!");
		}
	}

	public Message send(Message request) throws IOException, ClassNotFoundException {
		Message responce = null;
		if (isOpen()){
			LOGGER.info("Sent: " + request.toString());
			ObjectOutputStream objectOutFromServer = new ObjectOutputStream(out);
			objectOutFromServer.writeObject(request);
			out.flush();
			responce = receiveMessage();
		}else{
			throw new IOException("Connection is closed!");
		}
		return responce;
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public boolean isOpen() {
		return clientSocket != null && !clientSocket.isClosed();
	}

	public void addListener(SocketListener listener) {
		listeners.add(listener);
	}

	protected void handleSocketStatus(SocketStatus socketStatus) {
		for (SocketListener listener : listeners) {
			listener.handleStatus(socketStatus);
		}
	}

	protected void handleMessage(Message latestMsg) {
		for (SocketListener listener : listeners) {
			try {
				listener.handleNewMessage(latestMsg);
			} catch (Exception e) {
				try {
					close();
				} catch (IOException ioe) {
					LOGGER.error("Connection failed!", ioe);
				}
			}
		}
	}
	
	protected Message receiveMessage() throws IOException, ClassNotFoundException{
		 
		ObjectInputStream objectInFromServer = new ObjectInputStream(in);
		Message message = (Message) objectInFromServer.readObject();
		return message;
	}
	
	protected Message tolerantReceiveMessage(){
		Message message = null;
		ObjectInputStream objectInFromServer;
		try {
			objectInFromServer = new ObjectInputStream(in);
			message = (Message) objectInFromServer.readObject();
		} catch (Exception e) {
			
			LOGGER.error("Reading error.", e);
		}
		return message;
	}
	@Override
	public abstract void run();
}
