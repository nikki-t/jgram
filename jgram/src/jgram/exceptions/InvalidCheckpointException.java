package jgram.exceptions;

/**
 * Intent: Represent an exception that occurs when an assignment document's 
 * checkpoint syntax is incorrect.
 *
 */
public class InvalidCheckpointException extends Exception {
	
	// Instance variable(s)
	private static final long serialVersionUID = 1L;
	
	// Passes default message to Exception superclass.
	public InvalidCheckpointException() {
		super("Invalid checkpoint detected.");
	}
	
	// Passes client code's message to Exception superclass.
	public InvalidCheckpointException(String message) {
		super(message);
	}
	
	// Passes client code message and Throwable to Exception superclass.
	public InvalidCheckpointException(String message, Throwable cause) {
		super(message, cause);
	}
	
	// Passes Throwable to Exception superclass for exception chaining.
	public InvalidCheckpointException(Throwable cause) {
		super(cause);
	}


}
