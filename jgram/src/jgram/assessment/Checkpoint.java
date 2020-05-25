package jgram.assessment;

import jgram.exceptions.InvalidCheckpointException;
import jgram.exceptions.InvalidGradeMappingException;

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
	
	// Class constant(s)
	// Minimum and maximum values for a checkpoint weight
	public static final int MIN_WEIGHT = 1;
	public static final int MAX_WEIGHT = 10;
	// Minimum and maximum values for a checkpoint grade
	public static final int MIN_GRADE = 0;
	public static final int MAX_GRADE = 100;
	
	// Instance variable(s)
	private int weight;
	private int grade;
	private String feedback;
	
	// Constructor(s) - Post1
	/**
	 * Intent: Create a valid Checkpoint object.
	 * 
	 * Postcondition1 (Weight): Weight is a valid integer between MIN_WEIGHT
	 * and MAX_WEIGHT
	 * 
	 * Postcondition2 (Grade):  Grade is a valid integer between MIN_GRADE and
	 * MAX_GRADE
	 * 
	 * Postcondition3 (Feedback): User feedback is stored as feedback.
	 * @param inputWeight
	 * @param inputGrade
	 * @param inputFeedback
	 * @throws InvalidGradeMappingException
	 */
	public Checkpoint(int inputWeight, int inputGrade, String inputFeedback) 
			throws InvalidCheckpointException {
			
		// Post1 Weight
		if (inputWeight >= MIN_WEIGHT  && inputWeight <= MAX_WEIGHT) {
			weight = inputWeight;
		} else {
			throw new InvalidCheckpointException("ERROR: Invalid checkpoint "
					+ "data encountered.");
		}
		
		// Post2 Grade
		if (inputGrade >= MIN_GRADE && inputGrade <= MAX_GRADE) {
			grade = inputGrade;
		} else {
			throw new InvalidCheckpointException("ERROR: Invalid checkpoint "
					+ "data encountered.");
		}
		
		// Post3 Feedback
		feedback = inputFeedback;
		
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
	
	/**
	 * Returns the checkpoint feedback.
	 * @return String feedback value
	 */
	public String getFeedback() {
		return feedback;
	}
	
	/**
	 * Returns the checkpoint grade.
	 * @return Integer grade value
	 */
	public int getGrade() {
		return grade;
	}
	
	/**
	 * Returns the checkpoint weight.
	 * @return Integer weight value
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * Create and return a String of Checkpoint's state.
	 * @return String representation of Checkpoint's current state
	 */
	@Override
	public String toString() {
		return String.format("Weight: %d, Grade: %d, Feedback: %s", 
				weight, grade, feedback);
	}

}
