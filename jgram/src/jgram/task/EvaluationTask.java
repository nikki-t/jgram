package jgram.task;

import java.io.IOException;
import java.nio.file.Path;

import jgram.assessment.Document;
import jgram.exceptions.InvalidCommentException;
import jgram.security.Secret;

/**
 * Intent: Calculate and output the grade for an assignment.
 * 
 * Precondition1 (Task Selection): The user has selected the evaluation task
 * to grade the assignment document.
 * 
 * Postcondition1 (Document creation): A Document object is created that 
 * contains checkpoints, a grade mapping, and a result.
 * Postcondition2 (Document grade calculation): The Document is graded and
 * a Result object is created.
 * Postcondition3 (Result is appended to Document): The Result object is 
 * is used to create a table that is displayed to the console.
 * 
 */
public class EvaluationTask extends Task {
	
	// Instance variable(s)
	Document document;
	
	// Constructor(s)
	public EvaluationTask() {
		super();
	}
		
	public EvaluationTask(String userSecret) {
		super(userSecret);
	}
	
	/**
	 * Intent: A description of the evaluation task is displayed to the console.
	 */
	@Override
	public void displayHelp() {
		
		String help = "Evaluation Task Help:"
				+ "\n\n\tThe evaluation task accepts a file system directory" 
				+ "\n\tas input and searches the directory for Word document "
				+ "\n\tassignment files. The evaluation task then parses each " 
				+ "\n\tWord document file's for comments that contain either "
				+ "\n\tcheckpoints or a grade mapping. Once validated and " 
				+ "\n\textracted, the evaluation task then calculates the " 
				+ "\n\tassignment's total grade. A table of checkpoints and the " 
				+ "\n\tfinal grade is appended to a copy of the Word document " 
				+ "\n\tassignment which is created and saved in the same " 
				+ "\n\tdirectory as the assignment.\n";
		
		System.out.println(help);	
		
	}
	
	/**
	 * Intent: Display invalid comment or list of invalid comments determined
	 * from exception thrown by Comment class.
	 * 
	 * Precondition1 (Invalid comments): The graded document contains invalid
	 * comments which can include invalid checkpoints and grade mapping.
	 * 
	 * Postcondition1 (Invalid comments display): All invalid comments for
	 * the assignment are displayed on the console.
	 * @param e
	 */
	private void displayInvalidCommentException(InvalidCommentException e) {
		
		// Post1 Invalid comments display
		String filename = document.getAssignmentName().getFileName().toString();
		System.out.println(e.getMessage() + " " + filename);
		
		// Check if exception has a comment id
		if (e.getCommentID() != -1) {
			
			System.out.println("Please check the following: ");
			int id = e.getCommentID() + 1;
			System.out.println("\tComment #" + id);
		}
		
		// Check if exception has a list of invalid comments
		if (e.getInvalidCommentList() != null) {
			
			System.out.println("Please check the following: ");
			// Display invalid comment numbers
			for (int id : e.getInvalidCommentList()) {
				id += 1;
				System.out.println("\tComment #" + id);
			}
		}
	}
	
	/**
	 * Intent: Parse document for comments, checkpoints, and a grade mapping.
	 * 
	 * Postcondition1 (Comments): Comments are extracted from document.
	 * Postcondition2 (Grade mapping): Grade mapping is extracted from document.
	 * Postcondition3 (Checkpoints): Checkpoints are extracted from document.
	 * 
	 * @param document Document object
	 * @throws InvalidCommentException
	 */
	private void parseDocument(Document document) throws IOException, 
			InvalidCommentException {
		
		// Post1 Comments
		document.parseComments();
		// Test for presence of comments
		if (document.getCommentList().isEmpty()) {
			throw new InvalidCommentException("\nERROR: No comments were found "
					+ "in file");
		}
		
		// Post2 Grade mapping
		document.parseGradeMapping();
		// Set default grade mapping if one is not provided in the comments
		if (document.getGradeMapping().getLimits().isEmpty()) {
			document.setDefaultGradeMapping();
		}
		
		// Post3 Checkpoints
		document.parseCheckpoints();
		// Test for presence of checkpoints
		if (document.getCheckpointList().isEmpty()) {
			String message = "\nERROR: No checkpoints detected in file";
			throw new InvalidCommentException(message);
		}
		
	}
	
	/**
	 * Intent: Evaluate, calculate and display assignment grade.
	 * 
	 * Postcondition1 (Document loop): All documents in a directory have been
	 * iterated on and the task operations have been executed if applicable.
	 * Postcondition2 (Document): A new Document object is created that needs 
	 * to be graded.
	 * Postcondition3 (Parse document): Document is parsed for grading data.
	 * Postcondition4 (Evaluate assignment): Document is evaluated and total
	 * grade is calculated.
	 * Postcondition5 (Create hash string): An encoded hash string is created
	 * from the result.
	 * Postcondition6 (Create graded assignment): A copy of the assignment is 
	 * created and the results of the graded assignment are appended to the
	 * end of the assignment copy.
	 * Postcondition7 (Handle exceptions): Exceptions are reported to the 
	 * console and control returns to caller.
	 */
	@Override
	public void performTask() {
		
		// Notice on overwritten graded files
		System.out.println("\nNOTE: Any previously graded assignments "
		+ "will be overwritten.");
		
		try {
		
			// Post1 Document loop
			createFileList("evaluation");
			
			// Return to main menu if file list was not found
			if (getFileList().isEmpty()) {
				System.out.println("\nNo Word documents were found.");
				System.out.println("\n\tExiting to main menu...");
				return;
			}
		
			for (Path path : getFileList()) {
				
				try {
			
					// Post2 Document
					document = new Document(path);
					
					// Post3 Parse document
					parseDocument(document);
					
					// Post4 Evaluate assignment
					document.calculateResult();
				
					// Post5 Create hash string
					Secret secret = getSecret();
					document.createHashString(secret);
					
					// Post6 Create graded assignment
					document.createGradedAssignment();
					
					System.out.println("\nGraded Document: " 
							+ path.getFileName());
				
				// Post7 Handle exceptions
				} catch (InvalidCommentException e) {
					
					displayInvalidCommentException(e);
							
				}
			
			} // End for
			
			System.out.println("\nFINISHED GRADING. Check 'GRADED' directory "
					+ "for graded assignments.");
		
		// Post7 Handle exceptions
		} catch (IOException e) {
			
			// Directory stream, File input stream, Files copy, deleteDirectory
			System.out.println("\nERROR: Could not process files in directory "
					+ "entered.");
		}
			
	}

}
