package jgram.storage;

import java.io.Serializable;

/**
 * Intent: Represent a record that contains all of the data from the grading
 * process and can be used to re-create a result object for comparison in a 
 * tamper test.
 * 
 */
public class Record implements Serializable {
	
	// Class variable(s)
	private static final long serialVersionUID = 1001;
	
	// Instance variable(s)
	private int id;
	private String grader = System.getProperty("user.name");
	private String assignmentName;
	private String hashString;
	
	// Constructor(s)
	public Record(int inputID, String inputAssignment, String inputHash) {
		id = inputID;
		assignmentName = inputAssignment;
		hashString = inputHash;
	}
	
	public String getAssignmentName() {
		return assignmentName;
	}
	
	public String getHashString() {
		return hashString;
	}
	
	public String toString() {
		
		return String.format("ID: %d, Grader: %s, Assignment Name: %s, "
				+ "Hash: %s", id, grader, assignmentName, hashString);
		
	}

}
