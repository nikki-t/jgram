package jgram.assessment;

import jgram.utilities.Validation;

/**
 * Intent: Represent a comment in a Word document assignment. A comment has a 
 * unique identifier, author, and text. A comment can contain a checkpoint
 * or grade mapping so the Comment class has various operations to extract and
 * validate this type of data.
 *
 */
public class Comment {
	
	// Instance variable(s)
	private String id;
	private String author;
	private String text;
	
	// Constructor(s)
	public Comment (String inputID, String inputAuthor, String inputText) {
		id = inputID;
		author = inputAuthor;
		text = inputText;
	}
	
	// Accessor(s)
	
	/**
	 * Returns comment's unique identifier.
	 * @return String id
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Returns comment's author.
	 * @return String author
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Returns comment's text.
	 * @return String text
	 */
	public String getText() {
		return text;
	}
	
	// Instance method(s)
	
	/**
	 * Create and return a String of Comment's state.
	 * @return String representation of Comment's current state
	 */
	@Override
	public String toString() {
		return String.format("ID: %s, Author: %s, Comment Text: %s \n", id, 
				author, text);
	}
	
	/**
	 * Intent: Extract grade mapping from Comment text value.
	 * 
	 * Precondition1 (Comment text): Comment has a text value that contains a
	 * grade mapping.
	 * 
	 * Postcondition1 (Create GradeMapping): Create a GradeMapping object.
	 * Postcondition2 (Extraction content): Extract grade mapping content
	 * from Comment text.
	 * Postcondition3 (Extraction grade): Extract letter grade and numeric grade.
	 * Postcondition4 (Add grade): Add grade to GradeMapping.
	 * Postcondition5 (Return GradeMapping): Return complete GradeMapping.
	 * 
	 * @return GradeMapping object
	 */
	public GradeMapping extractGradeMapping() {
		
		// Post1
		GradeMapping gradeMapping = new GradeMapping();
		
		// Post2
		int startIndex = text.indexOf("GRADEMAPPING(");
		int endIndex = text.indexOf(")");
		String gradeContent = text.substring(startIndex + 13, endIndex);
		
		// Post3
		String[] contentArray = gradeContent.split(",", 0);
		
		// Loop through each element and extract grade mapping
		for (String content : contentArray) {
			
			content = content.trim();
			
			String[] grade = content.split("=", 0);
			String letter = grade[0];
			
			// TODO throw exception if not numeric
			int number = Integer.parseInt(grade[1]);
			
			// Post4
			gradeMapping.setGrade(letter, number);
		}
		
		
		// Post5
		return gradeMapping;
		
	}
	
	/**
	 * Intent: Extract checkpoint from Comment text value.
	 * 
	 * Precondition1 (Comment text): Comment has a text value that contains a
	 * checkpoint.
	 * 
	 * Postcondition1 (Checkpoint content): Extract checkpoint content.
	 * Postcondition2 (Extraction): Extract each piece of checkpoint 
	 * data (weight, grade, feedback).
	 * Postcondition3 (Checkpoint creation): Create and return Checkpoint object
	 * from extracted data.
	 * @return Checkpoint object
	 */
	public Checkpoint extractCheckpoint(GradeMapping gradeMapping) {
		
		// Post1
		int startIndex = text.indexOf("CHECKPOINT(");
		int endIndex = text.indexOf(")");
		String checkpointContent = text.substring(startIndex + 11, endIndex);
		
		// Post 2
		// Extract feedback
		int feedbackStart = text.indexOf("FEEDBACK=[");
		int feedbackEnd = text.indexOf("]");
		String feedback = text.substring(feedbackStart + 10, feedbackEnd);
		
		// Remove feedback from checkpoint content
		checkpointContent = checkpointContent.substring(0, feedbackStart - 11);
		checkpointContent = checkpointContent.trim();
		
		// Split checkpoint content to extract weight and grade
		String[] contentArray = checkpointContent.split(",", 0);
		String weightContent = contentArray[0].trim();
		String gradeContent = contentArray[1].trim();
		
		// Extract grade and weight
		weightContent = weightContent.split("=",0)[1];
		gradeContent = gradeContent.split("=", 0)[1];
		
		// Convert grade and weight to appropriate type
		int weight = Integer.parseInt(weightContent);
		int grade = Validation.isNumeric(gradeContent) 
				? Integer.parseInt(gradeContent) 
				: gradeMapping.getGrade(gradeContent.toUpperCase());
		
		// Post3
		Checkpoint checkpoint = new Checkpoint(weight, grade, feedback);
		return checkpoint;
		
	}
	
	
	/**
	 * Compares to Comment objects.
	 * @return boolean value that indicates if object's are equal
	 */
	@Override
	public boolean equals(Object obj) {
		
		// Test if object is compared with a reference to itself
		if (obj == this) {
			return true;
		}
		
		// Check if object is an instance of Checkpoint
		if (!(obj instanceof Comment)) {
			return false;
		}
		
		// Downcast object to Checkpoint to compare instance variables
		Comment comment = (Comment) obj;
		if (!(this.id.equals(comment.id))) {
			return false;
		}
		if (!(this.author.equals(comment.author))) {
			return false;
		}
		if (!(this.text.equals(comment.text))) {
			return false;
		}
		
		// Checkpoint objects are equal
		return true;
		
	}
	
}
