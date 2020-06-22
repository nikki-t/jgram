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
	private String hashString;
	private String assignmentName;
	private String studentFirstName;
	private String studentLastName;
	
	// Constructor(s)
	public Result() {
		checkpointList = new ArrayList<>();
	}
	
	public Result(List<Checkpoint> cList) {
		checkpointList = cList;
	}
	
	public Result(String inputHash) {
		hashString = inputHash;
		checkpointList = new ArrayList<>();
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
	 * Intent: Set the student's first and last name for the Result object from
	 * the assignment name.
	 * 
	 * Precondition1 (Assignment name): The assignment name has been set for the 
	 * Result object.
	 * 
	 * Postcondition1 (Split assignment name): The assignment name has been 
	 * split into a String array on the underscore character.
	 * Postcondition2 (Test split): The student's name is set to the assignment
	 * name if a first and last name cannot be determined.
	 * Postcondition3 (Assign first and last name): A first and last name are
	 * assigned to the Result object.
	 */
	public void extractStudentName() {
		
		// Post 1 Split assignment name
		String[] names = assignmentName.split("_");
		
		// Post 2 Test split
		if (names.length < 2) {
			studentFirstName = names[0];
			studentLastName = names[0];
		
		} else {
			// Post 3 Assign first and last name
			studentFirstName = names[1];
			studentLastName = names[0];
		}
	}
	
	/**
	 * Returns result's assignment name.
	 * @return String
	 */
	public String getAssignmentName() {
		return assignmentName;
	}
	
	/**
	 * Returns result's Checkpoint map.
	 * @return HashMap of checkpoints and identifiers
	 */
	public List<Checkpoint> getCheckpointList() {
		return checkpointList;
	}
	
	/**
	 * Returns result's hash string.
	 * @return String
	 */
	public String getHashString() {
		return hashString;
	}
	
	/**
	 * Returns result's student first name.
	 * @return String
	 */
	public String getStudentFirstName() {
		return studentFirstName;
	}
	
	/**
	 * Returns result's student last name.
	 * @return String
	 */
	public String getStudentLastName() {
		return studentLastName;
	}
	
	/**
	 * Returns result's total grade.
	 * @return float value of total grade
	 */
	public float getTotalGrade() {
		return totalGrade;
	}
	
	/**
	 * Sets the assignment name value to parameter value.
	 * @param String
	 */
	public void setAssignmentName(String inputAssignment) {
		assignmentName = inputAssignment;
	}
	
	/**
	 * Sets the hash string value to parameter value.
	 * @param String
	 */
	public void setHashString(String inputHash) {
		hashString = inputHash;
	}
	
	/**
	 * Sets the total grade value to parameter value.
	 * @param grade float value to set total grade to
	 */
	public void setTotalGrade(float grade) {
		totalGrade = grade;
	}

}
