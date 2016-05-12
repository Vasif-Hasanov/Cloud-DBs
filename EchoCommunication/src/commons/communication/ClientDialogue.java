package commons.communication;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import commons.data.EchoMessage;
import commons.data.Message;

public class ClientDialogue extends Dialogue {
	public boolean isTolerant = false;
	private void initThread() {
		thread = new Thread(this);
		thread.start();
	}

	public EchoMessage open(String host, int port, boolean isAsync) throws Exception{
		this.host = host;
		this.port = port;
		EchoMessage responce = null;
		close();
		initSocket(host, port);
		if (isAsync){
			initThread();
		}
		return responce;
	}	

	private void initSocket(String host, int port) throws IOException,
			UnknownHostException {
		clientSocket = new Socket(host, port);
		out = (clientSocket.getOutputStream());
		in = (clientSocket.getInputStream());
	}
	
	@Override
	public void run() {
		int sequencialErrorCount = 0;
		while (isOpen()) {
			try {
				Message latestMsg = null;
				if (isTolerant){
					latestMsg = tolerantReceiveMessage();
				}else{
					latestMsg = receiveMessage();
				}
				if (latestMsg == null){
					sequencialErrorCount++;
					if (sequencialErrorCount > 1){
						close();
					}
					Thread.sleep(1);
					continue;
				}else{
					sequencialErrorCount = 0;
				}
				handleMessage(latestMsg);
			} catch (Exception ioe) {
				LOGGER.error("Connection lost!", ioe);
				try {
					close();
				} catch (IOException e) {
					LOGGER.error("Unable to close connection!", e);
				}
			}
		}
	}

}
