package jgram.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jgram.assessment.Document;
import jgram.assessment.GradeMapping;
import jgram.assessment.Result;
import jgram.exceptions.InvalidCheckpointException;
import jgram.exceptions.InvalidCommentException;
import jgram.exceptions.InvalidGradeMappingException;
import jgram.exceptions.InvalidTableException;
import jgram.security.Secret;

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
	 * Intent: Determine if the path's parent directory contains 'GRADED'
	 * sub-directory.
	 * 
	 * @param path
	 * @return boolean
	 */
	private boolean containsGradedDirectory(Path path) throws IOException {
		
		Path parentDirectory = path.getParent();
		
		DirectoryStream<Path> directoryStream = 
				Files.newDirectoryStream(parentDirectory);
		
		boolean isGradedDirectory = false;
		
		for (Path p : directoryStream) {
			String filename = p.getFileName().toString();
			if (filename.contains("GRADED")) {
				isGradedDirectory = true;
			}
		}
		
		return isGradedDirectory;
		
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
					
		// Determine if assignments have been graded
		Path firstFile = getFileList().get(0);
		if (!(containsGradedDirectory(firstFile))) {
			throw new FileNotFoundException("\nAssignments have not been graded. "
					+ "Please grade assignments first.");
		}
		
		// Create PrintWriter
		reportFilename = getReportFilename(firstFile);
		outStream = new PrintWriter(reportFilename);
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
	 * Postcondition2 (Document loop): All documents in a directory have been
	 * iterated on and the task operations have been executed if applicable. 
	 * Postcondition3 (Handle exceptions): Exceptions are reported to the 
	 * console and control returns to the caller.
	 */
	@Override
	public void performTask() {
		
		try {
		
			Secret secret = getSecret();
			
			// Post1 PrintWriter creation
			createPrintWriter();
						
			// Post2 Document Loop
			for (Path path : getFileList()) {
				
				try {
					
					tamperTestPath(secret, path);
				
				// Post3 Handle exceptions from try to work with specific files
				} catch (FileNotFoundException e) {
					// generated from writeReport
					String error = "\nERROR: Could not locate file to write "
							+ "report for:  ";
					displayException(error);
				
				} catch (InvalidTableException 
						| InvalidCheckpointException 
						| InvalidGradeMappingException 
						| InvalidCommentException e) {
					// getHashStringFromFile (previously graded file)
					String error = e.getMessage() + "\n\tDetected in retrieval "
							+ "of file: ";
					displayException(error);
				
				} catch (MalformedJwtException e) {
					// JWT
					String error = "ERROR: Previous result has been corrupted. "
							+ "\n\tPlease re-grade original assignment: ";
					displayException(error);
				
				}
			}
			
			System.out.println("\nReport written to: " + reportFilename);
		
		// Post3 Handle exceptions generated from try to work with directory
		} catch (FileNotFoundException e) {
			// exceptions from createPrintWriter
			System.out.println(e.getMessage());
			
		} catch (IOException e) {
			// Directory stream, File input stream, Files copy, getHashStringFromFile
			String error = "ERROR: Could not process files in directory "
					+ "entered.";
			System.out.println(error);
		
		} catch (SignatureException | IllegalArgumentException e) {
			// JWT
			System.out.println("ERROR: INVALID secret entered.");
			
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
		currentDocument.parseComments();
		currentDocument.parseGradeMapping();
		currentDocument.parseCheckpoints();
		currentDocument.calculateResult();
		currentDocument.createHashString(secret.getID(), secret.getIssuer(), 
			secret.getSubject(), secret.getSecret());
	}
	
	/**
	 * Intent: Retrieve previous document grading result data.
	 * 
	 * @param secret
	 * @param path
	 * @return
	 * @throws InvalidCheckpointException
	 * @throws InvalidGradeMappingException 
	 */
	private void retrievePreviousDocument(Secret secret, Path path) 
			throws IOException, InvalidTableException, 
			InvalidCheckpointException, InvalidGradeMappingException {
		
		previousDocument = new Document(path);
		previousDocument.retrieveResult(secret.getSecret());

		
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
	 * 
	 * @param secret
	 * @param path
	 * @throws IOException
	 * @throws InvalidTableException
	 * @throws InvalidCheckpointException
	 * @throws InvalidGradeMappingException
	 * @throws InvalidCommentException
	 */
	private void tamperTestPath(Secret secret, Path path) throws IOException, 
			InvalidTableException, InvalidCheckpointException, 
			InvalidGradeMappingException, InvalidCommentException {
		
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
			status += "\n\tFAILED results comparison";
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
