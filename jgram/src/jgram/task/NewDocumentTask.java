package jgram.task;

import java.io.IOException;
import java.nio.file.Path;

import jgram.assessment.Checkpoint;
import jgram.assessment.Document;
import jgram.exceptions.InvalidCommentException;

public class NewDocumentTask extends Task {
	
	// Instance variable(s)
	private Document document;
	private String message;
	
	// Constructor(s)
	public NewDocumentTask() {
		super();
	}
	
	/**
	 * Intent: A description of the new document task is displayed to the console.
	 */
	@Override
	public void displayHelp() {
		
		String help = "New Document Task Help:"
				+ "\n\n\tThe new document task evaluates Word document "
				+ "\n\tassignments that are ready to be graded and determines if"
				+ "\n\tthe assignment contains any checkpoints, a grade mapping, or"
				+ "\n\tresults table. This task is used to evaluate assignments "
				+ "\n\tthat are ready to be graded and can be run prior to the "
				+ "\n\tinsertion of checkpoints and a grade mapping in preparation"
				+ "\n\tfor the evaluation task.\n";
		
		System.out.println(help);
	}
	
	/**
	 * Intent: Display whether the document is valid and the location of the
	 * document on the file system.
	 * 
	 * @param validity
	 */
	private void displayValidity(String validity) {
		String documentName = document.getAssignmentName().toString();
		System.out.println(message);
		System.out.println("\t" + validity + ": " + documentName);
	}
	
	public String getMessage() {
		return message;
	}
	
	/**
	 * Intent: Validate a new document to see if it contains any grading data.
	 * 
	 * This task is used to determine if an assignment is ready to be graded
	 * by an instructor.
	 * 
	 * Postcondition1 (Document loop): All valid documents are stored in a list
	 * and each document is ready for validation.
	 * Postcondition2 (Previously graded): The document has been tested to see 
	 * if it has been previously graded.
	 * Postcondition3 (Validate document): The document has been parsed for comments
	 * and each comment has been parsed for grading data. The documents validity
	 * has been displayed on the console.
	 * 
	 */
	@Override
	public void performTask() {
		
		try {
			// Post1 Document loop
			createFileList("new");
				
		// Return to main menu if file list was not found
		if (getFileList().isEmpty()) {
			System.out.println("\nNo Word documents were found.");
			System.out.println("\n\tExiting to main menu...");
			return;
		}
		
		for (Path path : getFileList()) {
			
			document = new Document(path);
			
			// Post2 Previously graded
			if (testIfPreviouslyGraded()) {
				message = "\nDocument has already been graded.";
				displayValidity("INVALID");
				break;
			}
			
			try {
				// Post3 Validate document
				validate();
				
			} catch (InvalidCommentException | IOException e) {
				message = "\nDocument contains invalid grading data.";
				displayValidity("INVALID");
			}
			
			
		}
				
		} catch (IOException e) {
			// Directory stream, File input stream
			System.out.println("\nERROR: Could not process files in directory "
					+ "entered.");
		}
		
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
