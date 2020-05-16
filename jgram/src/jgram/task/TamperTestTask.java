package jgram.task;

import jgram.assessment.Document;
import jgram.assessment.GradeMapping;
import jgram.assessment.Result;

import io.jsonwebtoken.SignatureException;

public class TamperTestTask extends Task {
	
	// Instance variable(s)
	String secret = "";
	
	// Constructor(s)
	public TamperTestTask() {
		super();
	}
	
	//Accessor(s)
	public String getSecret() {
		return secret;
	}
	
	// Mutator(s)
	public void setSecret(String userSecret) {
		secret = userSecret;
	}
	
	// Instance method(s)
	
	/**
	 * Intent: Detect if any grade mappings, checkpoints, or assignment results
	 * have been modified.
	 * 
	 * Precondition1 (Graded assignment): The assignment that is evaluated has
	 * already been graded by JGRAM.
	 * 
	 * Postcondition1 (Previous result): A new document is created that 
	 * represents previous results and previous hash string has been retrieved 
	 * and stored.
	 * Postcondition2 (Current result): The current document is evaluated for
	 * comments, a grade mapping, checkpoints and a result is determined.
	 * Postcondition3 (Comparison): The previous document and current document
	 * are compared.
	 * Postcondition4 (Result): The result of the comparison is displayed on the
	 * console.
	 * Postcondition5 (Report): A report is displayed on the console showing
	 * previous and current document results.
	 */
	@Override
	public void performTask() {
		
		// Post1 Previous result
		Document previousDocument = new Document();
		try {
			previousDocument.retrieveResult(secret);
		} catch (SignatureException | IllegalArgumentException e) {
			System.out.println("INVALID secret entered.");
			return;
		}
		
		// Post2 Current result
		Document currentDocument = new Document(getDocument());
		currentDocument.parseComments();
		currentDocument.parseGradeMapping();
		currentDocument.parseCheckpoints();
		currentDocument.calculateResult();
		currentDocument.createHashString("1", "BU-MET", "JRAM", secret);
		
		// Post3 Comparison of results
		
		// GradeMapping
		boolean isGradeMapEqual = compareGradeMaps(previousDocument, 
			currentDocument);
		
		// Result
		boolean isResultEqual = compareResults(previousDocument,
			currentDocument);
		
		// Post5 Report
		printReport(previousDocument, currentDocument, isGradeMapEqual, 
			isResultEqual);
		
	}
	
	/**
	 * Intent: Compare two grade mappings from previous and current documents 
	 * and determine if they are equal.
	 * @param previousDocument
	 * @param currentDocument
	 * @return boolean value that indicates grade mappings equality
	 */
	private boolean compareGradeMaps(Document previousDocument, 
		Document currentDocument) {
		
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
	 * @param previousDocument Document object
	 * @param currentDocument Document object
	 * @return boolean value that indicates grade mappings equality
	 */
	private boolean compareResults(Document previousDocument, 
		Document currentDocument) {
		
		Result previousResult = previousDocument.getResult();
		Result currentResult = currentDocument.getResult();
		boolean isResultEqual = true;
		
		if (!(previousResult.equals(currentResult))) {
			isResultEqual = false;
		}
		
		return isResultEqual;
	}
	
	/**
	 * Intent: Print a report of tamper test results.
	 * 
	 * Precondition1 (GradeMapping and Result): Both grade mappings and results
	 * have been compared in the previous and current documents.
	 * 
	 * Postcondition1 (Modifications detected): Any modifications that were 
	 * detected have been printed to the console.
	 * Postcondition2 (Comparison results): Result of comparisons have been
	 * printed to the console.
	 * @param previousDocument Document object
	 * @param currentDocument Document object
	 * @param isGradeMapEqual boolean that indicates if grade mappings are equal
	 * @param isResultEqual boolean that indicates if results are equal
	 */
	private void printReport(Document previousDocument, 
		Document currentDocument, boolean isGradeMapEqual, 
		boolean isResultEqual) {
		
		// Post1 Modifications detected
		System.out.println("\n---------------------------------[ TAMPER REPORT ]-----------------------------\n");

		String status = "Tamper Status: ";
		String gradeMapping = "";
		String resultTable = "";
		
		// Grade mapping
		if (!isGradeMapEqual) {
			status += "\n\tFAILED grade mapping comparison";
			gradeMapping = "\nPrevious Grade Mapping: \n" 
				+ previousDocument.getGradeMapping() 
				+ "\nCurrent Grade Mapping: \n"
				+ currentDocument.getGradeMapping();
		}
		
		// Results - checkpoints and total grade
		if (!isResultEqual) {
			status += "\n\tFAILED results comparison";
			resultTable = "\nPrevious Result Table: \n"
				+ previousDocument.createResultTable()
				+ "\nCurrent Result Table: \n"
				+ currentDocument.createResultTable();
		}
		
		// Passed modification tests
		if (isGradeMapEqual && isResultEqual) {
			status += "\n\tPASSED all comparisons";
			gradeMapping = "\nGrade Mapping: \n" 
					+ currentDocument.getGradeMapping();
			resultTable = "\nResult Table: \n"
					+ currentDocument.createResultTable();
			
		}
		
		// Post2 Comparison results
		System.out.println(status);
		System.out.println(gradeMapping);
		System.out.println(resultTable);
		
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

}
