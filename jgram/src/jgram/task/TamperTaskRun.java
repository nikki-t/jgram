package jgram.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

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

public class TamperTaskRun extends TaskRun {
	
	// Class variable(s)
	private static PrintWriter outStream;
	
	// Instance variable(s)
	private Document previousDocument;
	private Document currentDocument;
	

	public TamperTaskRun(PrintWriter inputOutStream, Path inputPath, 
			RecordManager inputRM, Secret inputSecret) {
		
		super(inputPath, inputRM, inputSecret);
		outStream = inputOutStream;		
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
	 * Intent: Display exception with file name where exception interrupted
	 * current processing of.
	 * 
	 * Postcondition1 (Obtain file name from original): The file name from the
	 * originally graded document is obtained and stored.
	 * Postconidtion2 (Synchronized): All output operations are synchronized
	 * across executing Threads.
	 * Postcondition3 (Display error): The exception's error message
	 * and the file where the exception occurred is displayed on the console.
	 * 
	 * @param e
	 */
	private void displayException(Exception e) {
			
		// Post1 Obtain file name from original
		String filename = previousDocument
				.getAssignmentName()
				.getFileName()
				.toString();
		
		// Post2 Synchronized
		synchronized(System.out) {
			// Post3 Display error
			if (e.getMessage().startsWith("\nERROR")) {
				System.out.println(e.getMessage());
			} else {
				System.out.println("\nERROR: " + e.getMessage());
			}
			
			System.out.println("\tCould not write tamper report for: " + filename);
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
			throw new InvalidCommentException("No comments were found "
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
			String message = "No checkpoints detected in file";
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
		String hashString = getRecordManager()
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
	 */
	@Override
	public void run() {
		
		try {
			
			// Store data needed for results
			Secret secret = getSecret();
			Path path = getPath();
			
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
		
		// Post5 Handle exceptions for specific files
		} catch (Exception e) {
			displayException(e);
		}
	}
	
	/**
	 * Intent: Print a report of tamper test results.
	 * 
	 * Precondition1 (GradeMapping and Result): Both grade mappings and results
	 * have been compared in the previous and current documents.
	 * 
	 * Postconidtion1 (Synchronized): All output operations are synchronized
	 * across executing Threads.
	 * Postcondition2 (Modifications detected): Any modifications that were 
	 * detected have been written to a file in the 'GRADED' sub-directory called
	 * 'reports.txt'.
	 * Postcondition3 (Comparison results): Result of comparisons have been
	 * written to a file in the 'GRADED' sub-directory called 'reports.txt'.
	 * 
	 * @param isGradeMapEqual boolean that indicates if grade mappings are equal
	 * @param isResultEqual boolean that indicates if results are equal
	 * @param path Path object to determine where report should be written.
	 */
	private void writeReport( boolean isGradeMapEqual, boolean isResultEqual, 
			Path path) throws IOException {
		
		// Post1 Synchronized
		synchronized(outStream) {
			
			// Post2 Modifications detected
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
			
			// Post3 Comparison results
			outStream.println(status);
			outStream.println(gradeMapping);
			outStream.println(resultTable);
			
			System.out.println("\nTAMPER TESTED: " + path.getFileName());
			
		}
		
	}

}
