package jgram.exceptions;

/**
 * Intent: Represent an exception that occurs when invalid record data is 
 * encountered.
 */
public class InvalidRecordException extends Exception {
	
	// Instance variable(s)
		private static final long serialVersionUID = 1L;

	public InvalidRecordException() {
		super("ERROR: Invalid record data encountered.");
	}

	public InvalidRecordException(String message) {
		super(message);
	}

	public InvalidRecordException(Throwable cause) {
		super(cause);
	}

	public InvalidRecordException(String message, Throwable cause) {
		super(message, cause);
	}


}
