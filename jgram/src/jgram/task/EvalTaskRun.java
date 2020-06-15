package jgram.task;

import java.io.IOException;
import java.nio.file.Path;

import jgram.assessment.Document;
import jgram.exceptions.InvalidCommentException;
import jgram.security.Secret;
import jgram.storage.RecordManager;

public class EvalTaskRun extends TaskRun {
	
	// Class variable(s)
	private static int recordID = 0;
	
	// Instance variable(s)
	private Document document;

	public EvalTaskRun(Path inputPath, RecordManager inputRM, 
			Secret inputSecret) {
		
		super(inputPath, inputRM, inputSecret);
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
	 * Postconidtion2 (Synchronized): All output operations are synchronized
	 * across executing Threads.
	 * @param e
	 */
	private void displayInvalidCommentException
			(InvalidCommentException e) {
		
		// Post1 Invalid comments display
		String filename = document.getAssignmentName().getFileName().toString();
		
		// Post2 Synchronized
		synchronized(System.out) {
			
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
				
	}
	
	/**
	 * Intent: Display IOException message and current grading status on the 
	 * console.
	 * 
	 * Postconidtion1 (Synchronized): All output operations are synchronized
	 * across executing Threads.
	 * Postcondition2 (Error message): The error message is displayed on the
	 * console.
	 * Postcondition3 (Assignment name): The assignment name is converted
	 * to a string.
	 * Postcondition4 (Update user): A message is displayed on the console
	 * to indicate that the file could not be graded.
	 * @param e
	 */
	private void displayIOException(IOException e) {
		
		// Post1 Synchronized
		synchronized(System.out) {
			// Post2 Error message
			System.out.println("\nERROR: " + e.getMessage());
			
			// Post3 Assignment name
			String filename = document.getAssignmentName().getFileName().toString();
			
			// Post4 Update user
			System.out.println("\n\tCould not grade: " + filename);
		}
		
	}
	
	/**
	 * Intent: Evaluate one assignment document by grading available data
	 * present in the document's comments, encoding the result as a hash
	 * string and writing the hash string to a file.
	 * 
	 * Postcondition1 (Document): A new Document object is created that needs 
	 * to be graded.
	 * Postcondition2 (Parse document): Document is parsed for grading data.
	 * Postcondition3 (Evaluate assignment): Document is evaluated and total
	 * grade is calculated.
	 * Postcondition4 (Create graded assignment): A copy of the assignment is 
	 * created and the results of the graded assignment are appended to the
	 * end of the assignment copy.
	 * Postcondition5 (Hash string): An encoded hash string is created
	 * from the result and stored in a file named 'jgram.dat'.
	 * Postcondition6 (Handle exceptions): Exceptions are reported to the 
	 * console and control returns to caller.
	 */
	@Override
	public void run() {
		
		try {
			
			// Post1 Document
			document = new Document(getPath());
			
			// Post2 Parse document
			parseDocument(document);
			
			// Post3 Evaluate assignment
			document.calculateResult();
						
			// Post4 Create graded assignment
			document.createGradedAssignment();
			
			// Post5 Hash string
			storeHashString(document);
			
		
		// Post6 Handle exceptions
		} catch (IOException e) {
			displayIOException(e);
			
		} catch (InvalidCommentException e) {
			displayInvalidCommentException(e);
					
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
	 * Intent: Create and store a hash string for tamper detection.
	 * 
	 * Postcondition1 (Create hash string): A hash string is created that 
	 * encodes all of the grading result data contained in a document.
	 * Postcondition2 (Record data): All data needed to create a record
	 * is retrieved.
	 * Postconidtion3 (Synchronized): All output operations are synchronized
	 * across executing Threads.
	 * Postcondition4 (Record written): A Record object is created and a record
	 * is written to 'jgram.dat'.
	 * 
	 * @param document
	 * @throws IOException 
	 */
	private void storeHashString(Document document) 
			throws IOException {
		
		// Post1 Create hash string
		document.createHashString(getSecret());
		
		// Post2 Record data
		
		// Assignment Name
		Path assignmentPath = document.getAssignmentName();
		int index = assignmentPath.getNameCount();
		String assignmentName = "GRADED_" + assignmentPath
				.getName(index - 1)
				.toString();
		
		// Post3 Synchronized
		synchronized(getRecordManager().getOutputStream()) {
			
			// Record ID
			recordID++;
			
			// Post4 Record written
			getRecordManager().writeRecord(recordID, assignmentName, 
					document.getHashString());
			
			System.out.println("\nGraded Document: " + getPath().getFileName());
		}
		
	}

}
