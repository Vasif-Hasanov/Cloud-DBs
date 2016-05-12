package commons.exceptions;

public class InvalidParameterException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public static String getErrorMessage(InvalidParameterType type, String value) {
		String errorMessage = null;
		errorMessage = getPreString(type) + value;
		return errorMessage;
	}

	private static String getPreString(InvalidParameterType type) {
		String errorMessage = "";
		switch (type) {
		case TEXT_IS_TOO_LARGE:
			errorMessage = "TEXT IS TOO LARGE: ";
			break;
		case KEY_IS_TOO_LARGE:
			errorMessage = "KEY IS TOO LARGE: ";
			break;
		case VALUE_IS_TOO_LARGE:
			errorMessage = "VALUE IS TOO LARGE: ";
			break;
		case PARAMETER_TYPE_INVALID:
			errorMessage = "PARAMETER TYPE INVALID: ";
			break;
		case KEY_IS_NULL:
			errorMessage = "KEY IS NULL";
			break;
		case VALUE_IS_NULL:
			errorMessage = "VALUE IS NULL";
			break;
		default:
			break;
		}
		return errorMessage;
	}

	public InvalidParameterException(String message) {
		super(message);
	}

	public static String getErrorMessage(InvalidParameterType type) {
		return getPreString(type);
	}
}
