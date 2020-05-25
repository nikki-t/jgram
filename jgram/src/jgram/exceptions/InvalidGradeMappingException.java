package jgram.exceptions;

/**
 * Intent: Represent an exception that occurs when an assignment document's 
 * grade mapping syntax is incorrect.
 *
 */
public class InvalidGradeMappingException extends Exception {
	
	// Instance variable(s)
	private static final long serialVersionUID = 1L;
	
	// Passes default message to Exception superclass.
	public InvalidGradeMappingException() {
		super("Invalid grade mapping detected.");
	}

	// Passes client code's message to Exception superclass.
	public InvalidGradeMappingException(String message) {
		super(message);
	}
	
	// Passes client code message and Throwable to Exception superclass.
	public InvalidGradeMappingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	// Passes Throwable to Exception superclass for exception chaining.
	public InvalidGradeMappingException(Throwable cause) {
		super(cause);
	}

}
