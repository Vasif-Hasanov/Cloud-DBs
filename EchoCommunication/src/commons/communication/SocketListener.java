package commons.communication;

import commons.data.Message;


public interface SocketListener {
	public void handleNewMessage(Message msg) throws Exception;
	
	public void handleStatus(SocketStatus status);
}

