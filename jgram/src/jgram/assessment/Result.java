package jgram.assessment;

import java.util.ArrayList;
import java.util.List;

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
	private List<Checkpoint> checkpointList;
	
	// Constructor(s)
	public Result() {
		checkpointList = new ArrayList<>();
	}
	
	public Result(List<Checkpoint> cList) {
		checkpointList = cList;
	}
	
	public Result(List<Checkpoint> cList, float tGrade) {
		checkpointList = cList;
		totalGrade = tGrade;
	}
	
	/**
	 * Intent: Add a Checkpoint object to Result's Checkpoint list
	 * 
	 * @param checkpoint
	 */
	public void addCheckpoint(Checkpoint checkpoint) {
		checkpointList.add(checkpoint);
	}
	
	/**
	 * Test if values in checkpoint map are equal.
	 * @param result Result object to compare to
	 * @return boolean value that indicates if equal
	 */
	private boolean compareCheckpoints(Result result) {
		
		boolean isEqual = true;
		
		// Compare each Checkpoint in the list
		for (int i = 0; i < this.checkpointList.size(); i++) {
			
			Checkpoint thisCheckpoint = this.checkpointList.get(i);
			Checkpoint paramCheckpoint = result.checkpointList.get(i);
			
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
		
		// CheckpointList size
		if(this.checkpointList.size() != result.checkpointList.size()) {
			return false;
		}
		
		// Compare Checkpoints
		if (!compareCheckpoints(result)) {
			return false;
		}
		
		// Result objects are equal
		return true;
		
	}
	
	/**
	 * Returns result's Checkpoint map.
	 * @return HashMap of checkpoints and identifiers
	 */
	public List<Checkpoint> getCheckpointList() {
		return checkpointList;
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
