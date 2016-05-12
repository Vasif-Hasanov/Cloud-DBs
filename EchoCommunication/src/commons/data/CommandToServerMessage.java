package commons.data;

import java.util.ArrayList;

import commons.exceptions.InvalidParameterException;
import commons.metaData.ServerInfo;

public class CommandToServerMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<ServerInfo> runningServerNodes;
	public String serverName;
	public ServerInfo configServerInfo;
	public ServerInfo destServerInfo;
	public CommandToServerMessage(MessageStatusType status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "CommandToServerMessage [status=" + status.name() + "]";
	}

	@Override
	public void validate() throws InvalidParameterException {
	}
}
