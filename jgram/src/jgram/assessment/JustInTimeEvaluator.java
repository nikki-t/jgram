package jgram.assessment;

import java.util.ArrayList;

/**
 * Intent: Represents an evaluator that evaluates assignment grades based on
 * a list of checkpoints that provide a numeric grade and weight.
 * 
 * Postcondition1 (Result): Calculates the total grade from a list of 
 * checkpoints.
 *
 */
public class JustInTimeEvaluator implements Evaluator {
	
	// Instance variable(s)
	private final ArrayList<Checkpoint> checkpointList;
	
	// Constructor(s)
	public JustInTimeEvaluator(ArrayList<Checkpoint> cList) {
		checkpointList = cList;
	}
	
	/**
	 * Calculates the total grade based on the checkpoint list that contains
	 * a list of grades and weights. Creates a result object to store the 
	 * checkpoint list and total grade.
	 * 
	 * @return Result object
	 */
	@Override
	public Result evaluate() {
		
		Result result = new Result();
		
		// Check if checkpoint list is empty
		if (checkpointList.isEmpty()) {
			return result;
		}
		
		// Store the total sum of grades * weights and the sum of weights
		float sumGradeWeights = 0;
		int sumWeights = 0;
		
		// Loop through each checkpoint to calculate Result
		for (Checkpoint checkpoint: checkpointList) {
			
			sumGradeWeights += checkpoint.getGrade() * checkpoint.getWeight();
			sumWeights += checkpoint.getWeight();
			
			// Add checkpoint to Result
			result.addCheckpoint(checkpoint);
			
		}
		
		// Determine total grade for the assignment
		float grade = sumGradeWeights / sumWeights;
		result.setTotalGrade(grade);
		
		return result;
		
	}

}
