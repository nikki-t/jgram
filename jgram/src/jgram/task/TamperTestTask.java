package jgram.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jgram.assessment.Document;
import jgram.assessment.GradeMapping;
import jgram.assessment.Result;
import jgram.exceptions.InvalidCheckpointException;
import jgram.exceptions.InvalidCommentException;
import jgram.exceptions.InvalidGradeMappingException;
import jgram.exceptions.InvalidRecordException;
import jgram.security.JWT;
import jgram.security.Secret;
import jgram.storage.RecordManager;

/**
 * Intent: Determine if a previously graded assignment has been modified.
 * 
 * Precondition1 (Graded assignments): Word document assignments have been
 * previously graded and stored in sub-directory named 'GRADED'.
 * 
 * Postcondition1 (Task performed for all files): All files are evaluated for
 * modifications and a report on each file is output to the sub-directory 
 * 'GRADED' and it titled 'report.txt'.
 *
 */
public class TamperTestTask extends Task {
	
	// Instance variable(s)
	Document previousDocument;
	Document currentDocument;
	String reportFilename;
	PrintWriter outStream;
	RecordManager recordManager;
	
	// Constructor(s)
	public TamperTestTask() {
		super();
	}
	
	public TamperTestTask(String userSecret) {
		super(userSecret);
	}
	
	/**
	 * Intent: Compare two grade mappings from previous and current documents 
	 * and determine if they are equal.
	 * 
	 * @param previousDocument
	 * @param currentDocument
	 * @return boolean value that indicates grade mappings equality
	 */
	private boolean compareGradeMaps() {
		
		GradeMapping previousGradeMap = previousDocument.getGradeMapping();
		GradeMapping currentGradeMap = currentDocument.getGradeMapping();
		boolean isGradeMapEqual = true;
		
		if (!(previousGradeMap.equals(currentGradeMap))) {
			isGradeMapEqual = false;
		}
		
		return isGradeMapEqual;	
		
	}
	
	/**
	 * Intent: Compare two results from previous and current documents 
	 * and determine if they are equal.
	 * 
	 * @param previousDocument Document object
	 * @param currentDocument Document object
	 * @return boolean value that indicates grade mappings equality
	 */
	private boolean compareResults() {
		
		Result previousResult = previousDocument.getResult();
		Result currentResult = currentDocument.getResult();
		boolean isResultEqual = true;
		
		if (!(previousResult.equals(currentResult))) {
			isResultEqual = false;
		}
		
		return isResultEqual;
	}
	
	/**
	 * Intent: PrintWriter object is created for writing or appending to 
	 * report.txt.
	 * 
	 * @throws IOException
	 */
	private void createPrintWriter() throws IOException {
		// Post1 PrintWriter creation
		createFileList("tamper");
		
		// Return to main menu if file list was not found
		if (getFileList().isEmpty()) {
			throw new FileNotFoundException("\nNo Word documents were found.");
		}
		
		// Create PrintWriter
		Path firstFile = getFileList().get(0);
		reportFilename = getReportFilename(firstFile);
		outStream = new PrintWriter(reportFilename);
	}
	
	/**
	 * Intent: Create a list of previously saved records.
	 * 
	 * Postcondition1 (RecordManager): A RecordManager object is created from
	 * the current working directory.
	 * Postcondition2 (Input stream): An ObjectInputStream object is created
	 * in order to read data from the previously saved file.
	 * Postcondition3 (Record list): A list of records is created from the 
	 * previously saved file.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void createRecordManager() throws FileNotFoundException, IOException,
			ClassNotFoundException {
		
		// Post1 RecordManager
		recordManager = new RecordManager(getWorkingDirectory());
		
		// Post2 Input stream
		recordManager.createInputStream();
		
		// Post3 Record list
		recordManager.createRecordListFromFile();
		
		// Close open resources
		recordManager.getInputStream().close();
		
	}
	
	/**
	 * Intent: Display exception with file name where exception interrupted
	 * current processing of.
	 * @param errorMessage
	 */
	private void displayException(String errorMessage) {
		
		// Obtain file name from original document
		String filename = previousDocument
				.getAssignmentName()
				.getFileName()
				.toString();
		errorMessage += filename;
		
		// Print message to console
		System.out.println(errorMessage);
		
	}
	
	/**
	 * Intent: A description of the tamper test task is displayed to the console.
	 */
	@Override
	public void displayHelp() {
		
		String help = "Tamper Test Task Help:"
				+ "\n\n\tThe tamper test task determines if the checkpoints,"
				+ "\n\tgrade mapping, or results have been modified for a "
				+ "\n\tpreviously graded assignment. The task uses a hash string "
				+ "\n\tcreated from the 'secret' to determine if any portion of"
				+ "\n\tthe assignment grading has been changed. The results are"
				+ "\n\tdisplayed to the console.\n";
		
		System.out.println(help);
		
	}
	
	private String getReportFilename(Path path) {
		
		// Locate previously graded file directory
		String prevGradedFile =  path.getParent().toString();
		
		// Return string that includes 'report.txt' as report file name
		return prevGradedFile + "/report.txt";
		
	}
	
	/**
	 * Intent: Post 1 Task performed for all files: 
	 * Detect if any grade mappings, checkpoints, or assignment results
	 * have been modified.
	 * 
	 * Precondition1 (Graded assignment): The assignment that is evaluated has
	 * already been graded by JGRAM.
	 * 
	 * Postcondition1 (PrintWriter creation): PrintWriter object has been 
	 * created for writing or appending to report.txt.
	 * Postcondition2 (RecordManager creation): RecordManager object is 
	 * created and a list of records is retrieved.
	 * Postcondition3 (Document loop): All documents in a directory have been
	 * iterated on and the task operations have been executed if applicable. 
	 * Postcondition4 (Handle exceptions): Exceptions are reported to the 
	 * console and control returns to the caller.
	 */
	@Override
	public void performTask() {
		
		try {
		
			Secret secret = getSecret();
			
			// Post1 PrintWriter creation
			createPrintWriter();
			
			// Post2 RecordManager creation
			createRecordManager();
						
			// Post3 Document Loop
			for (Path path : getFileList()) {
				
					tamperTestPath(secret, path);
			}
			
			System.out.println("\nReport written to: " + reportFilename);
		
		// Post4 Handle exceptions generated from try to work with directory
		} catch (FileNotFoundException e) {
			// exceptions from createPrintWriter and ObjectInputStream
			System.out.println("\nError: " + e.getMessage());
			
		} catch (ClassNotFoundException e) {
			// generated from RecordManager.createInputStream
			System.out.println("\nError: Could not locate record of previously"
					+ "saved results.\n\tPlease re-grade assignment(s).");
			
		} catch (IOException e) {
			// Directory stream, File input stream, Files copy, getHashStringFromFile
			System.out.println("\nERROR: Could not process files in directory "
					+ "entered.");
		
		} catch (SignatureException | IllegalArgumentException e) {
			// JWT
			System.out.println("\nERROR: INVALID secret entered.");
			
		} finally {
			if (outStream != null) {
				outStream.close();
			}
		}
		
	}
	
	/**
	 * Intent: Retrieve current document grading result data.
	 * 
	 * @param secret
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws InvalidCommentException
	 */
	private void retrieveCurrentDocument(Secret secret, Path path) 
			throws IOException, InvalidCommentException {
		
		currentDocument = new Document(path);	
		// Comments
		currentDocument.parseComments();
		// Test for presence of comments
		if (currentDocument.getCommentList().isEmpty()) {
			throw new InvalidCommentException("\nERROR: No comments were found "
					+ "in file");
		}
		
		// Grade mapping
		currentDocument.parseGradeMapping();
		// Set default grade mapping if one is not provided in the comments
		if (currentDocument.getGradeMapping().getLimits().isEmpty()) {
			currentDocument.setDefaultGradeMapping();
		}
		
		// Checkpoints
		currentDocument.parseCheckpoints();
		// Test for presence of checkpoints
		if (currentDocument.getCheckpointList().isEmpty()) {
			String message = "\nERROR: No checkpoints detected in file";
			throw new InvalidCommentException(message);
		}
		
		// Result
		currentDocument.calculateResult();
		currentDocument.createHashString(secret);
	}
	
	/**
	 * Intent: Retrieve previous document grading result data.
	 * 
	 * Postcondition1 (Previous document creation): A Document object is created
	 * to represent the results from a previously grading attempt.
	 * Postcondition2 (Extract file name): The previously graded filename is 
	 * located and the filename is stored as a String.
	 * Postcondition4 (Hash string): The hash string is extracted from the 
	 * record and stored in the previous Document object.
	 * Postcondition5 (Decode hash string): The hash string stored in the 
	 * previous Document object is decoded and used to populate data fields
	 * of Document object.
	 *  
	 * @param secret
	 * @param path
	 *
	 * @throws InvalidCheckpointException
	 * @throws InvalidGradeMappingException 
	 * @throws ClassNotFoundException 
	 * @throws InvalidRecordException 
	 */
	private void retrievePreviousDocument(Secret secret, Path path) 
			throws IOException, InvalidCheckpointException, 
			InvalidGradeMappingException, ClassNotFoundException, 
			InvalidRecordException {
		
		// Post1 Previous document creation
		previousDocument = new Document(path);
		
		// Post2 Extract file name
		int index = path.getNameCount();
		String assignmentName = path
				.getName(index - 1)
				.toString();
		
		// Post3 Retrieve hash string
		String hashString = recordManager
				.retrieveRecord(assignmentName)
				.getHashString();
		if (hashString.equals("")) {
			throw new InvalidRecordException("Could not find record.");
		}
		previousDocument.setHashString(hashString);	
		
		// Post4 Decode hash string
		JWT jwt = new JWT(secret);
		jwt.decode(previousDocument);

		
	}
	
	/**
	 * Intent: Tamper test document found at path parameter and write results
	 * to report.txt.
	 * 
	 * Postcondition1 (Previous result): A new document is created that 
	 * represents previous results and previous hash string has been retrieved 
	 * and stored.
	 * Postcondition2 (Current result): The current document is evaluated for
	 * comments, a grade mapping, checkpoints and a result is determined.
	 * Postcondition3 (Comparison): The previous document and current document
	 * are compared.
	 * Postcondition4 (Report): A report is written to a file in the 'GRADED'
	 * sub-directory called 'report.txt'.
	 * Postcondition5 (Handle exceptions for specific files): Exceptions have 
	 * been handled that are generated by specific files and where processing
	 * of files can continue.
	 * 
	 * @param secret
	 * @param path
	 * 
	 * @throws IOException
	 * @throws InvalidCheckpointException
	 * @throws InvalidGradeMappingException
	 * @throws InvalidCommentException
	 * @throws ClassNotFoundException 
	 * @throws InvalidRecordException 
	 */
	private void tamperTestPath(Secret secret, Path path) throws 
			ClassNotFoundException, IOException {
		
		try {
		
			// Post1 Previous result
			retrievePreviousDocument(secret, path);
			
			// Post2 Current result
			retrieveCurrentDocument(secret, path);
			
			// Post3 Comparison of results
			
			// GradeMapping
			boolean isGradeMapEqual = compareGradeMaps();
			
			// Result
			boolean isResultEqual = compareResults();
			
			// Post4 Report
			writeReport(isGradeMapEqual, isResultEqual, path);
			
			System.out.println("\nTAMPER TESTED: " + path.getFileName());
		
		// Post5 Handle exceptions for specific files
		} catch (FileNotFoundException e) {
			// generated from writeReport
			String error = "\nERROR: Could not locate file to write "
					+ "report for:  ";
			displayException(error);
		
		} catch (InvalidRecordException 
				| InvalidCheckpointException 
				| InvalidGradeMappingException 
				| InvalidCommentException e) {
			// getHashStringFromFile (previously graded file)
			String error = e.getMessage() + "\n\tDetected in retrieval "
					+ "of file: ";
			displayException(error);
			System.out.println("\n\tCould not generate report for file.");
		
		} catch (MalformedJwtException e) {
			// JWT
			String error = "ERROR: Previous result has been corrupted. "
					+ "\n\tPlease re-grade original assignment: ";
			displayException(error);
		
		}
	}
	
	/**
	 * Intent: Print a report of tamper test results.
	 * 
	 * Precondition1 (GradeMapping and Result): Both grade mappings and results
	 * have been compared in the previous and current documents.
	 * 
	 * Postcondition1 (Modifications detected): Any modifications that were 
	 * detected have been written to a file in the 'GRADED' sub-directory called
	 * 'reports.txt'.
	 * Postcondition2 (Comparison results): Result of comparisons have been
	 * written to a file in the 'GRADED' sub-directory called 'reports.txt'.
	 * 
	 * @param isGradeMapEqual boolean that indicates if grade mappings are equal
	 * @param isResultEqual boolean that indicates if results are equal
	 * @param path Path object to determine where report should be written.
	 */
	private void writeReport( boolean isGradeMapEqual, boolean isResultEqual, 
			Path path) throws IOException {
		

		// Post1 Modifications detected
		outStream.println("\n---------------------------------"
				+ "[ TAMPER REPORT ]-----------------------------\n");
		
		outStream.println("Filename: " + path.getFileName());
	    
		String status = "\nTamper Status: ";
		String gradeMapping = "";
		String resultTable = "";
		
		// Grade mapping
		if (!isGradeMapEqual) {
			status += "\n\tFAILED grade mapping comparison";
			gradeMapping = "Previous Grade Mapping: \n" 
				+ previousDocument.getGradeMapping() 
				+ "\nCurrent Grade Mapping: \n"
				+ currentDocument.getGradeMapping();
		}
		
		// Results - checkpoints and total grade
		if (!isResultEqual) {
			status += "\n\tFAILED checkpoint comparison";
			resultTable = "Previous Result Table: \n"
				+ previousDocument.getResultTableString()
				+ "\nCurrent Result Table: \n"
				+ currentDocument.getResultTableString();
		}
		
		// Passed modification tests
		if (isGradeMapEqual && isResultEqual) {
			status += "\n\tPASSED all comparisons";
			gradeMapping = "\nGrade Mapping: \n" 
					+ currentDocument.getGradeMapping();
			resultTable = "\nResult Table: \n"
					+ currentDocument.getResultTableString();
			
		}
		
		// Post2 Comparison results
		outStream.println(status);
		outStream.println(gradeMapping);
		outStream.println(resultTable);
		
	}

}
