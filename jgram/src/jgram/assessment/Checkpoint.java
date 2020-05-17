package jgram.assessment;

/**
 * Intent: Represent a grading checkpoint present in an assignment Word 
 * document comment.
 * 
 * Precondition1 (Document creation): A Document object is created that contains
 * checkpoints.
 * 
 * Postcondition1 (Checkpoint creation): A Checkpoint is created that represents
 * a portion of a student's grade for the assignment and includes feedback.
 *
 */
public class Checkpoint {
	
	// Instance variable(s)
	private int weight;
	private int grade;
	private String feedback;
	
	// Constructor(s) - Post1
	// TODO Provide validation to checkpoint data
	public Checkpoint(int inputWeight, int inputGrade, String inputFeedback) {
		weight = inputWeight;
		grade = inputGrade;
		feedback = inputFeedback;
	}
	
	// Accessor(s)
	
	/**
	 * Returns the checkpoint weight.
	 * @return Integer weight value
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * Returns the checkpoint grade.
	 * @return Integer grade value
	 */
	public int getGrade() {
		return grade;
	}
	
	/**
	 * Returns the checkpoint feedback.
	 * @return String feedback value
	 */
	public String getFeedback() {
		return feedback;
	}
	
	// Instance method(s)
	
	/**
	 * Create and return a String of Checkpoint's state.
	 * @return String representation of Checkpoint's current state
	 */
	@Override
	public String toString() {
		return String.format("Weight: %d, Grade: %d, Feedback: %s", 
				weight, grade, feedback);
	}
	
	/**
	 * Compares to Checkpoint objects.
	 * @return boolean value that indicates if object's are equal
	 */
	@Override
	public boolean equals(Object obj) {
		
		// Test if object is compared with a reference to itself
		if (obj == this) {
			return true;
		}
		
		// Check if object is an instance of Checkpoint
		if (!(obj instanceof Checkpoint)) {
			return false;
		}
		
		// Downcast object to Checkpoint to compare instance variables
		Checkpoint checkpoint = (Checkpoint) obj;
		if (this.weight != checkpoint.weight) {
			return false;
		}
		if (this.grade != checkpoint.grade) {
			return false;
		}
		if (!(this.feedback.equals(checkpoint.feedback))) {
			return false;
		}
		
		// Checkpoint objects are equal
		return true;
		
	}

}
