package jgram.task;

public class NewDocumentTask extends Task {
	
	// Constructor(s)
	public NewDocumentTask() {
		super();
	}
	
	// Instance method(s)
	
	/**
	 * Intent: Validate a new document to test if it is ready to be uploaded to
	 * CMS or gready for grading.
	 * 
	 * TO DO: Implement for next iteration.
	 */
	@Override
	public void performTask() {
		System.out.println("New document task.");
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
	
}
