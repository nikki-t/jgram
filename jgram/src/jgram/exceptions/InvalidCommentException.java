package jgram.exceptions;

import java.util.ArrayList;

/**
 * Intent: Represents an exception that occurs when an assignment document contains an
 * invalid comment.
 * 
 * Precondition1 (Invalid comment): An invalid comment is detected.
 * 
 * Postcondition1 (Comment ID is tracked): The ID of an invalid comment is 
 * stored  in order to notify the user of the location of an invalid comment.
 * 
 * Postcondition2 (All comment IDs tracked) All invalid comment identifiers 
 * are stored in order to notify the user of the location of all invalid 
 * comments.
 *
 */
public class InvalidCommentException extends Exception {
	
	private static final long serialVersionUID = 1L;
	// Instance variable(s)
	private int commentID = -1;   // Default to -1 to detect if no comment IDs are present
	private ArrayList<Integer> invalidCommentList;
	
	// Constructor(s)
	/**
	 * Passes default message to Exception superclass.
	 */
	public InvalidCommentException() {
		super("Invalid comment detected.");
	}
	
	/**
	 * Post2 All comment IDs tracked: Passes a list of Integer identifier values to track comments with
	 * invalid data.
	 * 
	 * @param invalidCList
	 */
	public InvalidCommentException(ArrayList<Integer> invalidCList) {
		super();
		invalidCommentList = invalidCList;
	}
		
	/**
	 * Post1 Comment ID is tracked: Passes comment ID to exception instance variable.
	 * 
	 * @param id
	 */
	public InvalidCommentException(int id) {
		super();
		commentID = id;
	}
	
	/**
	 * Passes client code's message to Exception superclass.
	 * 
	 * @param message
	 */
	public InvalidCommentException(String message) {
		super(message);
	}
	
	/**
	 * Post2 All comment IDs tracked: Passes a message and a list of Integer 
	 * identifier values to track comments with invalid data.
	 * 
	 * @param message
	 * @param invalidCList
	 */
	public InvalidCommentException(String message, ArrayList<Integer> invalidCList) {
		super(message);
		invalidCommentList = invalidCList;
	}
	
	/**
	 * Post1 Comment ID is tracked: Passes default message and comment ID to 
	 * exception.
	 * 
	 * @param message
	 * @param id
	 */
	public InvalidCommentException(String message, int id) {
		super(message);
		commentID = id;
	}
	
	/**
	 * Passes client code message and Throwable to Exception superclass.
	 * 
	 * @param message
	 * @param cause
	 */
	public InvalidCommentException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Post2 All comment IDs tracked: Passes default message, Throwable object, 
	 * and invalid comment list to  exception.
	 * 
	 * @param message
	 * @param cause
	 * @param invalidCList
	 */
	public InvalidCommentException(String message, Throwable cause, 
			ArrayList<Integer> invalidCList) {
		
		super(message, cause);
		invalidCommentList = invalidCList;
	}
	
	/**
	 * Post1 Comment ID is tracked: Passes default message, Throwable object, 
	 * and comment ID to exception.
	 * 
	 * @param message
	 * @param cause
	 * @param id
	 */
	public InvalidCommentException(String message, Throwable cause, int id) {
		super(message, cause);
		commentID = id;
	}
	
	/**
	 * Passes Throwable to Exception superclass for exception chaining.
	 * 
	 * @param cause
	 */
	public InvalidCommentException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Post2 All comment IDs tracked: Passes default message and Throwable to 
	 * Exception and a list of invalid comment IDs to subclass.
	 * 
	 * @param cause
	 * @param invalidCList
	 */
	public InvalidCommentException(Throwable cause, 
			ArrayList<Integer> invalidCList) {
		
		super(cause);
		invalidCommentList = invalidCList;
	}
	
	/**
	 * Post1 Comment ID is tracked: Passes Throwable and comment ID to 
	 * exception.
	 * 
	 * @param cause
	 * @param id
	 */
	public InvalidCommentException(Throwable cause, int id) {
		super(cause);
		commentID = id;
	}
	
	// Accessor(s)
	public int getCommentID() {
		return commentID;
	}
	
	public ArrayList<Integer> getInvalidCommentList()  {
		return invalidCommentList;
	}

}
