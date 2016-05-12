package commons.data;

import java.io.Serializable;
import java.util.ArrayList;

import commons.exceptions.InvalidParameterException;
import commons.exceptions.InvalidParameterType;
import commons.metaData.ServerInfo;


public class KVMessage extends Message implements Serializable{
	private static final int maxLengthOfValue = 128000;
	private static final int maxLengthOfKey = 20;
	public String key;
	public String value;
	public ArrayList<ServerInfo> runningServerNodes;
	private static final long serialVersionUID = 1L;
	
	public KVMessage(String key, String value, MessageStatusType status){	
		this.key = key;
		this.value = value;
		this.status = status;
	}
	@Override
	public String toString() {
		return "KeyValueMessage [key=" + key + ", value=" + value + "]";
	}
	/**
	 * 
	 */
	
	@Override
	public void validate() throws InvalidParameterException {
		if (key == null){
			String errorMessage = InvalidParameterException.getErrorMessage(InvalidParameterType.KEY_IS_NULL);
			throw new InvalidParameterException(errorMessage);
		}
		if (value == null && status == MessageStatusType.PUT){
			String errorMessage = InvalidParameterException.getErrorMessage(InvalidParameterType.VALUE_IS_NULL);
			throw new InvalidParameterException(errorMessage);
		}
		if (key.length() > maxLengthOfKey){
			String errorMessage = InvalidParameterException.getErrorMessage(InvalidParameterType.KEY_IS_TOO_LARGE, key);
			throw new InvalidParameterException(errorMessage);
		}
		if (value != null && value.length() > maxLengthOfValue){
			String errorMessage = InvalidParameterException.getErrorMessage(InvalidParameterType.VALUE_IS_TOO_LARGE, value);
			throw new InvalidParameterException(errorMessage);
		}
	}
}
