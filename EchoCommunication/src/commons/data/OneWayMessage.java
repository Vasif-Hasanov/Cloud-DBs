package commons.data;

import commons.exceptions.InvalidParameterException;
import commons.exceptions.InvalidParameterType;

public class OneWayMessage extends Message {
	private static final int maxLengthOfMessage = 128000;
	private static final long serialVersionUID = 1L;
	public String description;
	public OneWayMessage(String description, MessageStatusType status){
		this.description = description;
		this.status = status;
	}
	@Override
	public void validate() throws InvalidParameterException {
		if (description != null && description.length() > maxLengthOfMessage){
			String errorMessage = InvalidParameterException.getErrorMessage(InvalidParameterType.TEXT_IS_TOO_LARGE, description);
			throw new InvalidParameterException(errorMessage);
		}
	}
	

}
