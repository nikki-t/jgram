package jgram.assessment;

import jgram.security.JWT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Intent: Represent a Word Document assignment with comments that contain
 * checkpoints and a grade mapping.
 * 
 * Precondition1 (Assignment exists): An assignment with comments that contains
 * checkpoints and a grade mapping exists.
 * 
 * Postcondition1 (Comments): The comments are extracted from the Word document
 * and stored in a list.
 * Postcondition2 (Grade mapping): The grade mapping is extracted from the 
 * comments and stored in a Grade Mapping object.
 * Postcondition3 (Checkpoints): The checkpoints are extracted from the comments
 * and stored in a list of Checkpoint objects.
 * Postcondition4 (Result): The total grade and checkpoints are stored in a 
 * Result object.
 * Postcondition5 (Encode result): Encode document grading results and store
 * in hash string instance variable.
 * Postcondition6 (Create table): Create and display a table of the Result 
 * object.
 * Postcondition7 (Write results): Write results of Document grading to a txt
 * file named 'saved_jgrams.txt'.
 */
public class Document {
	
	// Class constant(s)
	// Minimum and maximum values for a checkpoint weight
	public static final int MIN_WEIGHT = 1;
	public static final int MAX_WEIGHT = 10;
	// Minimum and maximum values for a checkpoint grade
	public static final int MIN_GRADE = 0;
	public static final int MAX_GRADE = 100;
	// Checkpoint and grade mapping grammar
	private static final String CHECKPOINT = "CHECKPOINT";
	private static final String GRADE_MAPPING = "GRADEMAPPING";
	
	// Instance variable(s)
	private String[][] filePath;
	private String assignmentName;
	private ArrayList<Comment> commentList;
	private ArrayList<Checkpoint> checkpointList;
	private GradeMapping gradeMapping;
	private Result result;
	private String hashString;
	
	// Constructor(s)
	public Document() {
		commentList = new ArrayList<>();
		checkpointList = new ArrayList<>();
	}
	
	public Document(String[][] path) {
		filePath = path;
		commentList = new ArrayList<>();
		checkpointList = new ArrayList<>();
	};
	
	// Accessor(s)
	public String getAssignmentName() {
		return assignmentName;
	}
	
	public ArrayList<Comment> getCommentList() {
		return commentList;
	}
	
	public ArrayList<Checkpoint> getCheckpointList() {
		return checkpointList;
	}
	
	public Result getResult() {
		return result;
	}
	
	public GradeMapping getGradeMapping() {
		return gradeMapping;
	}
	
	public String getHashString() {
		return hashString;
	}
	
	// Mutator(s)
	public void setAssignmentName(String aName) {
		assignmentName = aName;
	}
	
	public void setGradeMapping(GradeMapping gMap) {
		gradeMapping = gMap;
	}
	
	public void setResult(Result calculatedResult) {
		result = calculatedResult;
	}
	
	public void setHashString(String hString) {
		hashString = hString;
	}
	
	// Instant Method(s)
	
	/**
	 * Intent: (Post1) Extract the comments from the assignment document and
	 * store them in a list.
	 */
	public void parseComments() {
		
		// Simulate getting document comments from document found at file path
		String[][] comments = filePath;
		
		// Post1 Create a new comment object and add it to the comment list
		for (String[] docComment : comments) {
			Comment commentObject = new Comment(docComment[0], docComment[1], 
					docComment[2]);
			commentList.add(commentObject);
		}
		
	}
	
	/**
	 * Intent: (Post2) Extract grade mapping from comment list and store
	 * in GradeMapping instance variable.
	 */
	public void parseGradeMapping() {
		
		// Loop through comments list and extract grade mapping
		for (Comment comment: commentList) {
			if (comment.getText().contains(GRADE_MAPPING)) {
				gradeMapping = comment.extractGradeMapping();
				break;
			}
		}
		
	}
	
	/**
	 * Intent: (Post3) Extract checkpoints from comment list and store 
	 * checkpoints in a list.
	 */
	public void parseCheckpoints() {
		
		// Loop through comments list and extract comments that contain
		// checkpoints
		for (Comment comment : commentList) {
			if (comment.getText().contains(CHECKPOINT)) {
				Checkpoint checkpoint = comment.extractCheckpoint(gradeMapping);
				checkpointList.add(checkpoint);
			}
		}
		
	}
	
	/**
	 * Intent: (Post4) Calculate the total grade for the assignment and store
	 * the grade and checkpoints in a Result object.
	 * 
	 * Precondition1 (Checkpoints and Grade mapping): Checkpoint and GradeMapping
	 * objects have been extracted from Comment list.
	 * 
	 * Postcondition1 (Calculate): Calculate the total grade from checkpoint
	 * values.
	 * Postcondition2 (Hash result): Create a hash string that encodes the
	 * result object.
	 */
	public void calculateResult() {
		
		// Post1 Calculate
		JustInTimeEvaluator justInTimeEval = new JustInTimeEvaluator(checkpointList);
		
		// Post2 Hash result
		result = justInTimeEval.evaluate();
		
		
	}
	
	/**
	 * Intent: (Post5) Encode result and grade mapping data as a hashed string to aid in
	 * the detection of tampering.
	 * 
	 * @param id ID of JWT
	 * @param issuer Name of JWT issuer
	 * @param subject JWT subject
	 */
	public void createHashString(String id, String issuer, String subject,
		String secret) {
		
		// Create JWT object
		JWT jwt = new JWT(secret);
		
		// Encode Document's Result and GradeMapping objects
		jwt.encode(id, issuer, subject, this);
		
	}
	
	/**
	 * Intent: (Post6) Create and return a result table as a string.
	 * @return String that contains result table
	 */
	public String createResultTable() {
		
		// Result table string
		StringBuilder resultTable = new StringBuilder("");
		
		// Display columns
		resultTable.append(String.format("%4s %8s %7s %10s", "C#", "Weight", 
				"Grade", "Feedback"));
		
		// Display checkpoints
		HashMap<Integer, Checkpoint> checkpointMap = result.getCheckpointMap();
		for (Map.Entry<Integer, Checkpoint> entry : checkpointMap.entrySet()) {
			
			int checkpointID = entry.getKey();
			Checkpoint checkpoint = entry.getValue();
			int weight = checkpoint.getWeight();
			int grade = checkpoint.getGrade();
			String feedback = checkpoint.getFeedback();
			
			resultTable.append(String.format("\n%4d %8d %7d   %s", checkpointID,
				weight, grade, feedback));
		}
		
		// Display total grade
		resultTable.append(String.format("\n%4s %8s %7.2f   %10s\n", "", "Σ", 
			result.getTotalGrade(), hashString));
		
		
		// Return result table string
		return resultTable.toString();
			
	}
	
	/**
	 * Intent: (post7) Write results to a file in order to make results 
	 * available for tamper detection test at a later time.
	 * 
	 * Precondition1 (Results determined): GradeMapping and Result objects have
	 * been created and contain the results of Document grading operations.
	 * 
	 * Postcondition1 (Create PrintWriter): PrintWriter object is created and
	 * allows writing to a text filed name 'saved_jgrams.txt'.
	 * Postcondition2 (Hash string written): The name of the assignment and 
	 * Document hashString are written to the file.
	 */
	public void writeResult() {
		
		// Post1 Set output stream
        String outFile = "saved_jgrams.txt";
        PrintWriter outputStream = null;
        try {
        	outputStream = new PrintWriter(outFile);
        } catch(FileNotFoundException e) {
            // Error opening file
            System.out.println("Error opening file: " + outFile
                    + ". Exiting program...");
            System.exit(0);
        }
        
        // Post 2 Hash string written
        outputStream.println(assignmentName + " " + hashString);
        outputStream.close();
		
	}
	
	/**
	 * Intent: Retrieve previous result from txt file and create Result
	 * and GradeMapping objects stored in Document.
	 * 
	 * Precondition1 (Previously graded): Assignment document has been 
	 * previously graded and a txt file for that assignment exits.
	 * 
	 * Postcondition1 (Previous result): Pprevious result's hash 
	 * string is retrieved from txt file.
	 * Postcondition2 (Create JWT): JWT object is created using user's secret.
	 * Postcondition3 (Decode hash string): Previous result's hash string
	 * is decoded and Result and GradeMapping objects have been created and 
	 * stored in Document object.
	 * @param secret String that represents user secret
	 */
	public void retrieveResult(String secret) {
		
		// Post1 Previous result
		getHashStringFromFile();
		
		// Post2 Create JWT
		JWT jwt = new JWT(secret);
		
		// Post3 Decode hash string
		jwt.decode(this);
	}
	
	/**
	 * Intent: Return hash string stored in txt file.
	 * 
	 * Precondition1 (txt file): txt file exists and contains a hash string
	 * from previously graded assignment.
	 * 
	 * Postcondition1 (Open file): File is opened.
	 * Postcondition2 (Hash string): Retrieve hash string from file and store
	 * in Document object.
	 */
	private void getHashStringFromFile() {
		
		// Post1 Open file
        String inFile = "saved_jgrams.txt";
        Scanner inputStream = null;
        try {
        	inputStream = new Scanner (new File(inFile));
        } catch(FileNotFoundException e) {
            // Error opening file
            System.out.println("Error opening file: " + inFile
                    + ". Exiting program...");
            System.exit(0);
        }
        
        // Post2 Hash string
        String[] line;
        String hash = "";
        while (inputStream.hasNext()) {
        	// Get line as an array
        	line = inputStream.nextLine().trim().split("\\s");
        	hash = line[1];
        }
        // Store in Document
        hashString = hash;
		
	}
}
