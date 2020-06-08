package jgram.assessment;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
	private final List<Checkpoint> checkpointList;
	
	// Constructor(s)
	public JustInTimeEvaluator(List<Checkpoint> cList) {
		checkpointList = cList;
	}
	
	/**
	 * Intent: Calculates the total grade based on the checkpoint list that 
	 * contains a list of grades and weights. Creates a result object to store 
	 * the  checkpoint list and total grade.
	 * 
	 * Postcondition1 (Function interface object): A Function interface object
	 * is created that takes a Checkpoint argument and returns an Integer
	 * result.
	 * Postcondition2 (Sum the product of checkpoint grade and weight):
	 * The sum of the product of all checkpoint's grade and weight instance 
	 * variables is calculated.
	 * Postcondition3 (Sum checkpoint grade weights): The sum of all checkpoint
	 * grade weights is calculated.
	 * Postcondition4 (Determine total grade): The total grade for the 
	 * assignment is calculated.
	 * Postcondition5 (Result): A Result object is created with a list of 
	 * Checkpoint objects and a total grade.
	 * 
	 * @return Result object
	 */
	@Override
	public Result evaluate() {
		
		// Post1 Function interface object
		Function<Checkpoint, Integer> gradeWeightProduct = checkpoint -> 
				checkpoint.getGrade() * checkpoint.getWeight();
		
		// Post2 Sum the product of checkpoint grade and weight
		Optional<Integer> sumGradeWeights = checkpointList
				.stream()
				.map(gradeWeightProduct)
				.reduce((x, y) -> x + y);
		
		int gradeWeights;
		if (sumGradeWeights.isPresent()) {
			gradeWeights = sumGradeWeights.get();
		} else {
			gradeWeights = -1;
		}
		
		// Post3 Sum checkpoint grade weights
		int weights = checkpointList
				.stream()
				.mapToInt(c -> c.getWeight())
				.sum();
		
		// Post4 Determine total grade for the assignment
		float grade = gradeWeights / (float) weights;
		
		// Post5 Result
		Result result = new Result(checkpointList, grade);
		return result;

		
	}

}
