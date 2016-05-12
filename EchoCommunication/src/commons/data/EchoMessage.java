package commons.data;

import java.io.Serializable;

import commons.exceptions.InvalidParameterException;
import commons.exceptions.InvalidParameterType;


public class EchoMessage extends Message implements Serializable{
	private static final int maxLengthOfMessage = 128000;
		
	public EchoMessage(String text, MessageStatusType status){
		this.text = text;
		this.status = status;
	}

	@Override
	public String toString() {
		return "EchoMessage [text=" + text + "]";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String text;

	@Override
	public void validate() throws InvalidParameterException {
		if (text != null && text.length() > maxLengthOfMessage){
			String errorMessage = InvalidParameterException.getErrorMessage(InvalidParameterType.TEXT_IS_TOO_LARGE, text);
			throw new InvalidParameterException(errorMessage);
		}
	}
	
	
}
