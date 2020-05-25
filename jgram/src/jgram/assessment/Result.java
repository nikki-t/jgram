package jgram.assessment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Intent: Represent the result of grading an assignment including a
 * map of checkpoints and the calculated overall grade.
 *
 * Precondition1 (Document creation): A Document object is created that contains
 * Checkpoint objects and a GradeMapping object.
 * 
 * Postcondition1 (Result creation): Result is created and includes a map of
 * checkpoints with identifiers and an overall grade.
 *
 */
public class Result {
	
	// Instance variable(s)
	private float totalGrade;
	private int checkpointID = 1;
	private HashMap<Integer, Checkpoint> checkpointMap;
	
	// Constructor(s)
	public Result() {
		checkpointMap = new HashMap<>();
	}
	
	/**
	 * Adds a checkpoint with an ID number to the checkpoint map.
	 * @param checkpoint Checkpoint object
	 */
	public void addCheckpoint(Checkpoint checkpoint) {
		checkpointMap.put(checkpointID, checkpoint);
		checkpointID++;
	}
	
	/**
	 * Test if keys in checkpoint map are equal.
	 * @param result Result object to compare to
	 * @return boolean value that indicates if equal
	 */
	private boolean compareKeys(Result result) {
		
		boolean isEqual = true;
		
		Set<Integer> thisKeys = this.checkpointMap.keySet();
		Set<Integer> parameterKeys = result.checkpointMap.keySet();
		
		if (!thisKeys.equals(parameterKeys)) {
			isEqual = false;
		}
		
		return isEqual;
		
	}
	
	/**
	 * Test if values in checkpoint map are equal.
	 * @param result Result object to compare to
	 * @return boolean value that indicates if equal
	 */
	private boolean compareValues(Result result) {
		
		boolean isEqual = true;
		
		Collection<Checkpoint> thisValues = this.checkpointMap.values();
		ArrayList<Checkpoint> thisCheckpoints = new ArrayList<>(thisValues);
		
		Collection<Checkpoint> paramValues = result.checkpointMap.values();
		ArrayList<Checkpoint> paramCheckpoints = new ArrayList<>(paramValues);
		
		for (int i = 0; i < thisCheckpoints.size(); i++) {
			
			Checkpoint thisCheckpoint = thisCheckpoints.get(i);
			Checkpoint paramCheckpoint = paramCheckpoints.get(i);
			
			if (!(thisCheckpoint.equals(paramCheckpoint))) {
				isEqual = false;
				break;
			}
			
		}
		
		return isEqual;
	}
	
	// Instance method(s)
	
	/**
	 * Compares Result objects.
	 * @return boolean value that indicates if objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		
		// Test if object is compared with a reference to itself
		if (obj == this) {
			return true;
		}
		
		// Check if object is an instance of Result
		if (!(obj instanceof Result)) {
			return false;
		}
		
		// Downcast object to Result to compare instance variables
		Result result = (Result) obj;
		
		// Total Grade
		if(Float.compare(this.totalGrade, result.totalGrade) != 0) {
			return false;
		}
		
		// CheckpointMap size
		if(this.checkpointMap.size() != result.checkpointMap.size()) {
			return false;
		}
		
		// Compare checkpointMap's keys (Checkpoint id)
		if (!compareKeys(result)) {
			return false;
		}
		
		// Compare checkpointMap's values (Checkpoints)
		if (!compareValues(result)) {
			return false;
		}
		
		// Result objects are equal
		return true;
		
	}
	
	/**
	 * Returns result's Checkpoint map.
	 * @return HashMap of checkpoints and identifiers
	 */
	public HashMap<Integer,Checkpoint> getCheckpointMap() {
		return checkpointMap;
	}
	
	/**
	 * Returns result's total grade.
	 * @return float value of total grade
	 */
	public float getTotalGrade() {
		return totalGrade;
	}
	
	/**
	 * Sets the total grade value to parameter value.
	 * @param grade float value to set total grade to
	 */
	public void setTotalGrade(float grade) {
		totalGrade = grade;
	}

}
