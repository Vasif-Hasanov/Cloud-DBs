package commons.exceptions;

public class UnknownCommandException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnknownCommandException(){
		super("Unknown command.");
	}
}
