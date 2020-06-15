package jgram.task;

import java.io.IOException;
import java.nio.file.Path;

import jgram.assessment.Checkpoint;
import jgram.assessment.Document;
import jgram.exceptions.InvalidCommentException;

public class NewDocTaskRun extends TaskRun {
	
	// Instance variable(s)
	private Document document;
	private String message;

	public NewDocTaskRun(Path inputPath) {
		super(inputPath);
	}
	
	/**
	 * Intent: Display whether the document is valid and the location of the
	 * document on the file system.
	 * 
	 * Postconidtion1 (Synchronized): All output operations are synchronized
	 * across executing Threads.
	 * 
	 * @param validity
	 */
	private void displayValidity(String validity) {
		
		// Post1 Synchronized
		synchronized(System.out) {
			String documentName = document.getAssignmentName().toString();
			System.out.println(message);
			System.out.println("\t" + validity + ": " + documentName);
		}
		
	}
	
	public String getMessage() {
		return message;
	}
	
	/**
	 * Intent: Retrieve grade data from document by parsing document comments
	 * and then parsing comments for a grade mapping and checkpoints.
	 * @throws InvalidCommentException
	 * @throws IOException
	 */
	private void retrieveGradeData() throws InvalidCommentException, 
			IOException {
		
		document.parseComments();
		
		document.parseGradeMapping();
		
		document.parseCheckpoints();
		
	}
	
	/**
	 * Intent: Determine if a document is ready for grading by the grader.
	 * 
	 * Postcondition1 (Previously graded): The document has been tested to see 
	 * if it has been previously graded and if it has control returns to the
	 * calling method.
	 * Postcondition2 (Validate document): The document has been parsed for comments
	 * and each comment has been parsed for grading data. The documents validity
	 * has been displayed on the console.
	 */
	@Override
	public void run() {
		
		document = new Document(getPath());
		
		// Post1 Previously graded
		if (testIfPreviouslyGraded()) {
			message = "\nDocument has already been graded.";
			displayValidity("INVALID");
			return;
		}
		
		try {
			// Post2 Validate document
			validate();
			
		} catch (InvalidCommentException | IOException e) {
			message = "\nDocument contains invalid grading data.";
			displayValidity("INVALID");
		}
		
	}
 	
	/**
	 * Intent: Test whether a document has grade data (i.e. Checkpoints or a 
	 * grade mapping) and create a message displaying the status of the grade 
	 * data. Return an empty String if no grade data is found.
	 * 
	 * Postcondition1 (Checkpoint data): All checkpoint data stored in the
	 * document is accounted for.
	 * 
	 * Postcondition2 (GradeMapping data): All grade mapping data stored in the
	 * document is accounted for.
	 * 
	 */
	private void testForGradeData() {
		
		StringBuilder sb = new StringBuilder("");
		
		// Post1 Checkpoint data
		if (!document.getCheckpointList().isEmpty()) {
			
			sb.append("\nFOUND checkpoint data in the following comments: ");
			
			for (Checkpoint checkpoint : document.getCheckpointList()) {
				sb.append("\n\tComment #" + checkpoint.getID());
			}
			
		}
		
		// Post2 GradeMapping data
		if (!document.getGradeMapping().getLimits().isEmpty()) {
			
			sb.append("\nFOUND grade mapping data.");
		}
		
		message = sb.toString();
		
		
	}
	
	/**
	 * Intent: Test is the document has already been graded and return a boolean
	 * value indicating test result.
	 * 
	 * @return boolean prevGrade
	 */
	private boolean testIfPreviouslyGraded() {
		
		boolean prevGrade = false;
		
		// Extract name
		int nameCount = document.getAssignmentName().getNameCount();
		String name = document
				.getAssignmentName()
				.getName(nameCount - 1)
				.toString();
		
		if (name.startsWith("GRADED_")) {
			prevGrade = true;
		}
		
		return prevGrade;
	}
	
	/**
	 * Intent: Validate a document for checkpoint and grade mapping data.
	 * 
	 * Postcondition1 (Retrieve grade data): Any grade data that is contained in the
	 * document has been retrieved.
	 * Postcondition2 (Test grade data presence): The document is tested
	 * for the presence of any grade data.
	 * Postcondition3 (Validity message): A message on the document's 
	 * validity is displayed to the console.
	 * 
	 * @throws InvalidCommentException
	 * @throws IOException
	 */
	private void validate() throws InvalidCommentException, IOException {
		
		// Post1 Retrieve grade data
		retrieveGradeData();
		
		// Post2 Test grade data presence
		testForGradeData();
		
		// Post3 Validity message
		if (message.equals("")) {
			message = "\nNo grading data detected.";
			displayValidity("VALID");
		} else {
			displayValidity("INVALID");
		}
		
	}

}
