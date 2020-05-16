package jgram.assessment;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Intent: Represent an assignment Word document grade mapping.
 * 
 * Precondition1 (Document creation): A Document object is created that contains
 * a grade mapping.
 * 
 * Postcondition1 (GradeMapping creation): A GradeMapping is created that 
 * represents the grade mapping used to determine a student's grade on their
 * assignment.
 *
 */
public class GradeMapping {
	
	// Instance variable(s)
	// Map of letter and numeric grade values
	private LinkedHashMap<String, Integer> limits;
	
	// Constructor(s)
	public GradeMapping() {
		limits = new LinkedHashMap<>();
	}
	
	// Accessor(s)
	
	/**
	 * Returns a map of letters mapped to grades.
	 * @return LinkedHashMap of mapped letter and numeric grades
	 */
	public LinkedHashMap<String, Integer> getLimits() {
		return limits;
	}
	
	// Instance method(s)
	
	/**
	 * Adds an element to the limits map.
	 * @param gradeLetter String value of an alphabetic grade
	 * @param gradeNumber int value of a numeric grade
	 * 
	 * Postcondition1 (Grade added): A grade mapping is added to the list of 
	 * limits.
	 */
	public void setGrade(String gradeLetter, int gradeNumber) {
		
		// Test if input parameters are valid
		if (gradeLetter == null || gradeLetter.trim().length() == 0) {
			System.out.println("Grade letter is invalid.");
			return;
		}
		if (gradeNumber > 100 || gradeNumber < 0) {
			System.out.println("Grade number is invalid.");
			return;
		}
		
		// Post1
		limits.put(gradeLetter, gradeNumber);
		
	}
	
	/**
	 * Finds and returns the numeric grade associated with an alphabetic grade.
	 * @param gradeLetter String value of an alphabetic grade
	 * @return int value of a numeric grade
	 */
	public int getGrade(String gradeLetter) {
		
		boolean containsGrade = limits.containsKey(gradeLetter);
		if (!containsGrade ) {
			System.out.printf("Missing grade %s mapping.", gradeLetter);
			return -1;
		}
		
		return limits.get(gradeLetter);
		
	}
	
	/**
	 * Create a default grade mapping for limits LinkedHashMap.
	 * @return LinkedHashMap of mapped letter and number grades
	 */
	public void setDefaultGradeMapping() {
		
        limits.put("A+", 97);
        limits.put("A", 95);
        limits.put("A-", 93);
        limits.put("B+", 87);
        limits.put("B", 85);
        limits.put("B-", 83);
        limits.put("C+", 77);
        limits.put("C", 75);
        limits.put("C-", 73);
        limits.put("F", 67);
        
	}
	
	/**
	 * Create and return a String of GradeMapping's state.
	 * @return String representation of GradeMapping's current state
	 */
	@Override
	public String toString() {
		
		StringBuilder gradeMapping = new StringBuilder("");
		
		for (Map.Entry<String, Integer> entry : limits.entrySet()) {
			String grade = String.format("%-2s = %d", entry.getKey(), 
				entry.getValue());
			gradeMapping.append(grade + "\n");
		}
		
		return gradeMapping.toString();
	}
	
	/**
	 * Compares GradeMapping objects.
	 * @return boolean value that indicates if objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		
		// Test if object is compared with a reference to itself
		if (obj == this) {
			return true;
		}
		
		// Check if object is an instance of Result
		if (!(obj instanceof GradeMapping)) {
			return false;
		}
		
		// Downcast object to Result to compare instance variables
		GradeMapping gradeMapping = (GradeMapping) obj;
		
		// Retrieve parameter GradeMapping's limits
		LinkedHashMap<String, Integer> gradeLimits = gradeMapping.getLimits();
		
		// Compare this GradeMapping's limits with parameter GradeMapping limits
		boolean isEqual = this.limits.equals(gradeLimits);
		
		return isEqual;
		
	}
	
}
