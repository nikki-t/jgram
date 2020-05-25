package jgram.exceptions;

/**
 * Intent: Represent an exception that occurs when a previously graded
 * assignment table is invalid.
 *
 */
public class InvalidTableException extends Exception {
	
	// Instance variable(s)
	private static final long serialVersionUID = 1L;
	
	// Passes default message to Exception superclass.
	public InvalidTableException() {
		super("ERROR: Invalid table data encountered.");
	}
	
	// Passes client code's message to Exception superclass.
	public InvalidTableException(String message) {
		super(message);
	}
	
	// Passes client code message and Throwable to Exception superclass.
	public InvalidTableException(String message, Throwable cause) {
		super(message, cause);
	}
	
	// Passes Throwable to Exception superclass for exception chaining.
	public InvalidTableException(Throwable cause) {
		super(cause);
	}

}
