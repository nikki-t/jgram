package jgram.task;

/**
 * Intent: The Task class represents one of many tasks the JGRAM program can 
 * complete.
 * Precondition1 (Task Selection): User has selected a task to complete.
 * Postcondition1 (Task Completion): The task is completed and the result is 
 * displayed on the console.
 *
 */
public abstract class Task {

	// Constructor(s)
	public Task() {}
	
	// Instance method(s)
	
	/**
	 * Intent: To generate a list of the document's grade mapping and 
	 * checkpoints.
	 * Postcondition1: Return a list of the document's grade mapping and
	 * checkpoints.
	 * @return An array of Strings
	 */
	public String[][] getDocument() {
		
		// Simulation of Document content comments
		String[] comment1 = {"0", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=85, FEEDBACK=[Throws ArrayIndexOutOfBoundsException; "
				+ "watch out for the Boolean condition that controls the for "
				+ "loop’s execution. This for loop executes one more time than "
				+ "you would want it to because of the greater than or equal to "
				+ "sign.])"};
		String[] comment2 = {"1", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=100, FEEDBACK=[Excellent work.])"};
		String[] comment3 = {"2", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=4, "
				+ "GRADE=90, FEEDBACK=[Per the prompt, the method should "
				+ "return the first element in the array; use the break "
				+ "keyword to exit the for loop once the element is found.])"};
		String[] comment4 = {"3", "Nikki Tebaldi", "GRADEMAPPING(A+=97, "
				+ "A=95, A-=93, B+=87, B=85, B-=83, C=77, F=67)"};
		
		// String representing Document content
		String[][] document = {comment1, comment2, comment3, comment4};
		
		return document;
	}
	
	/**
	 * Intent: Performs task specific to subclass implementation.
	 * Precondition1 (Task Selection)
	 * See EvaluationTask, TamperTask, and NewDocumentTask method documentation
	 * for postconditions.
	 */
	abstract public void performTask();
	
	/**
	 * Intent: A brief description of each task will be displayed to the console.
	 * Precondition1 (Help selection): The user indicated that they would like help.
	 */
	abstract public void displayHelp();
	
}
