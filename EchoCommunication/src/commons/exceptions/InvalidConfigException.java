package commons.exceptions;

public class InvalidConfigException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidConfigException(){
		super("Config file contains invalid information.");
	}
}
