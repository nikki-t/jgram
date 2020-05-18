package jgram.task;

import jgram.assessment.Document;

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
	String secret = "";
	
	// Constructor(s)
	public EvaluationTask() {
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
	 * Intent: Evaluate, calculate and display assignment grade.
	 * Postcondition1 (Document) Create a new Document object that needs to be
	 * graded.
	 * Postcondition2 (Comments): Comments are extracted from document.
	 * Postcondition3 (Grade mapping): Grade mapping is extracted from document.
	 * Postcondition4 (Checkpoints): Checkpoints are extracted from document.
	 * Postcondition5 (Evaluate assignment): Document is evaluated and total
	 * grade is calculated.
	 * Postcondition6 (Create hash string): An encoded hash string is created
	 * from the result.
	 * Postcondition7 (Display result): Assignment grade is displayed to the 
	 * console.
	 * Postcondition8 (Write result): The result hash string is written to a 
	 * file for later retrieval in tamper test.
	 */
	@Override
	public void performTask() {
		
		// Post1 Document
		Document document = new Document(getDocument());
		document.setAssignmentName("assignment_sample.docx");
		
		// Post2 Comments
		document.parseComments();
		
		// Post3 Grade mapping
		document.parseGradeMapping();
		
		// Post4 Checkpoints
		document.parseCheckpoints();
		
		// Post5 Evaluate assignment
		document.calculateResult();
		
		// Post6 Create hash string
		document.createHashString("1", "BU-MET", "JRAM", secret);
		
		// Post7 Display result
		System.out.println("Grading Result Table: ");
		System.out.println(document.createResultTable());
		
		// Post8 Write result
		document.writeResult();
		
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

}
