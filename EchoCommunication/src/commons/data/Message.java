package commons.data;

import java.io.Serializable;

import commons.exceptions.InvalidParameterException;


public abstract class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MessageStatusType status;
	
	public boolean isEchoMessage(){
		return status.ordinal() <= MessageStatusType.ECHO_SUCCESS.ordinal();
	}
	
	public boolean isKeyValueMessage(){
		return status.ordinal() >= MessageStatusType.GET.ordinal() 
				&& status.ordinal() <= MessageStatusType.SERVER_NOT_RESPONSIBLE_FOR_PUT.ordinal();
	}
	
	public boolean isOneWayMessage(){
		return status.ordinal() >= MessageStatusType.PARAMETER_ERROR.ordinal() 
				&& status.ordinal() <= MessageStatusType.SERVER_STOPPED.ordinal();
	}
	
	public boolean isCommandToServerMessage(){
		return status.ordinal() >= MessageStatusType.START.ordinal() 
				&& status.ordinal() <= MessageStatusType.SHUTDOWN_SERVER.ordinal();
	}
	
	public abstract void validate() throws InvalidParameterException;
}
