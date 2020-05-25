package jgram.assessment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import jgram.exceptions.InvalidCheckpointException;
import jgram.exceptions.InvalidCommentException;
import jgram.exceptions.InvalidGradeMappingException;
import jgram.exceptions.InvalidTableException;
import jgram.security.JWT;

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
 * Postcondition6 (Append table): Create and append a table of the graded 
 * results to a copy of the assignment.
 * Postcondition7 (Write results): Write results of Document grading to a txt
 * file named 'saved_jgrams.txt'.
 */
public class Document {
	
	// Class constant(s)
	// Checkpoint and grade mapping grammar
	private static final String CHECKPOINT = "CHECKPOINT";
	private static final String GRADE_MAPPING = "GRADEMAPPING";
	private static final String TABLE_TITLE = "JGRAM RESULTS";
	
	
	// Instance variable(s)
	private Path assignmentPath;
	private ArrayList<Comment> commentList;
	private ArrayList<Checkpoint> checkpointList;
	private GradeMapping gradeMapping;
	private Result result;
	private String hashString;
	
	private XWPFTable table;
	
	// Constructor(s)
	public Document() {
		commentList = new ArrayList<>();
		checkpointList = new ArrayList<>();
		gradeMapping = new GradeMapping();
	};
	
	public Document(Path path) {
		assignmentPath = path;
		commentList = new ArrayList<>();
		checkpointList = new ArrayList<>();
		gradeMapping = new GradeMapping();
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
	 * Intent: Copy Document's assignmentPath to a new Path that represents
	 * a file on the file system.
	 * 
	 * Precondition1 (Assignment path): The assignment path has been identified
	 * by the user.
	 * 
	 * Postcondition1 (Copied): The file found at the assignment path has been
	 * copied to a new file located in a directory labeled 'GRADED' inside
	 * original file's parent directory. Any previously graded assignments
	 * will be overwritten.
	 * 
	 * @return Path object
	 * @throws IOException
	 */
	private Path copyFile() throws IOException {
		
		// Define and create target directory
		String targetDirectory = assignmentPath.getParent().toString() 
				+ "/GRADED";
		Path pathTargetDirectory = Paths.get(targetDirectory);
		
		// If the directory does not exit create it
		if (!(Files.isDirectory(pathTargetDirectory))) {
			Files.createDirectories(pathTargetDirectory);
		}
		
		// Define and create copy of assignment file
		String targetFile =  pathTargetDirectory.toString() 
				+ "/GRADED_" 
				+ assignmentPath.getFileName().toString();
		
		Path pathTarget = Paths.get(targetFile);
		
		Files.copy(assignmentPath, pathTarget, 
				StandardCopyOption.REPLACE_EXISTING);
		
		// Post1 Copied
		return pathTarget;
		
	}
	
	/**
	 * Intent: (Post6) Create and append a table of graded results to a copy
	 * of the assignment.
	 * 
	 * Precondition1 (Graded) Assignment has been graded.
	 * 
	 * Postcondition1 (XWPF document creation): The assignment path has been 
	 * converted to a File and passed to the XWPFDocument constructor to create
	 * an XWPFDocument object that represents a Word document.
	 * Postcondition2 (Assignment copied): XWPFDocument object is copied to 
	 * a new XWPF document which also creates a new copy of the assignment
	 * file on the file system.
	 * Postcondition2 (Table creation): An XWPFTable object has been created
	 * that represents a Word document table with the graded assignment results.
	 * Postcondition3 (Result written to copy): The XWPFTable has been written to a copy
	 * of the Word document assignment.
	 * 
	 * @throws IOException
	 */
	public void createGradedAssignment() throws IOException {
		
		// Post1 XWPF document creation
		XWPFDocument documentContent = retrieveXWPFDocument(assignmentPath);
		
		// Post2 Assignment copied
		Path pathCopy = copyFile();
		XWPFDocument documentCopy = retrieveXWPFDocument(pathCopy);
		
		// Post3 Table creation
		createParagraph(documentCopy);
		createTable(documentCopy);
		
		// Post4 Results written to copy
		FileOutputStream fileOutputStream = 
				new FileOutputStream(pathCopy.toString());
		documentCopy.write(fileOutputStream);
		
		// Close resources
		documentContent.close();
		documentCopy.close();
		fileOutputStream.close();
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
	 * Intent: Create a paragraph in a XWPFDocument
	 * 
	 * @param documentContent
	 */
	private void createParagraph(XWPFDocument documentContent) {
		
		XWPFParagraph paragraph = documentContent.createParagraph();
		XWPFRun run = paragraph.createRun();
		run.setBold(true);
		run.setFontSize(16);
		run.setText(TABLE_TITLE);
		
	}
	
	/**
	 * Intent: Create an XWPF table object that contains results of assignment
	 * grading.
	 * 
	 * Postcondition1 (Column headers): The column headers have been created and
	 * stored in the table.
	 * Postcondition2 (Checkpoint rows): The checkpoints have been stored in
	 * rows in the table.
	 * Postondition3 (Total grade and hash string row): The total grade and
	 * hash string for the graded assignment have been stored in the last
	 * row in the table.
	 * 
	 * @param documentContent
	 */
	private void createTable(XWPFDocument documentContent) {
		
		// Create XWPF table object
		table = documentContent.createTable();
		
		// Set table width TODO ??
		CTTblWidth tableWidth = table.getCTTbl().addNewTblPr().addNewTblW();
		tableWidth.setType(STTblWidth.DXA);
		tableWidth.setW(BigInteger.valueOf(6000));
		
		// Post1 Column headers
		setTableColumnHeader();
		
		// Post2 Checkpoint rows
		setTableCheckpointRows();
		
		// Post3 Total grade and hash string row
		setResultRow();
		
	}
	
	public Path getAssignmentName() {
		return assignmentPath;
	}
	
	public ArrayList<Checkpoint> getCheckpointList() {
		return checkpointList;
	}
	
	public ArrayList<Comment> getCommentList() {
		return commentList;
	}
	
	public GradeMapping getGradeMapping() {
		return gradeMapping;
	}
	
	public String getHashString() {
		return hashString;
	}
	
	/**
	 * Intent: Return hash string stored in previously graded Word document.
	 * 
	 * Precondition1 (Previously graded file): A previously graded Word document
	 * exists in the sub-directory 'GRADED' and the file name has 'GRADED_' 
	 * prepended to it.
	 * 
	 * Postcondition1 (Locate graded file): The previously graded file is 
	 * located and stored as an XWPFDocument object.
	 * Postcondition2 (Table extraction): The results table is  extracted from
	 * the previously graded file. An error is thrown if the table cannot be 
	 * found.
	 * Postcondition3 (Hash string extraction): The hash string is extracted 
	 * from the last row in the table. An error is thrown if the hash string
	 * cannot be found.
	 */
	private void getHashStringFromFile() throws IOException, 
			InvalidTableException {
		
		// Post1 Locate previously graded file
		String parentDirectory = assignmentPath.getParent().toString();
		String filename = assignmentPath.getFileName().toString();
		String prevGradedFile = parentDirectory + "/" + filename;
		Path prevPath = Paths.get(prevGradedFile);
		
		XWPFDocument prevDocument = retrieveXWPFDocument(prevPath);
		
		// Post2 Table extraction
		List<XWPFTable> tables = prevDocument.getTables();
		if (tables.isEmpty()) {
			throw new InvalidTableException();
		}
		XWPFTable lastTable = tables.get(tables.size() - 1);
	    
		// Post3 Hash string extraction
		List<XWPFTableRow> rows = lastTable.getRows();
		if (!(isValidRows(rows))) {
			throw new InvalidTableException();
		}
	    int lastRowIndex = rows.size() -1;
        hashString = lastTable.getRow(lastRowIndex).getCell(3).getText();
		
	}
	
	public Result getResult() {
		return result;
	}
	
	/**
	 * Intent: Return a String representation of a table of grading results
	 * that includes checkpoints and total assignment grade.
	 * 
	 * @return String
	 */
	public String getResultTableString() {

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
	 * Test for valid table rows in graded Word document assignment.
	 * @param rows
	 * @return boolean
	 */
	private boolean isValidRows(List<XWPFTableRow> rows) {
		
		boolean validRows = (!(rows.isEmpty()) 
				&& !(rows.get(0).getTableCells().isEmpty())
				&& rows.get(0).getCell(0).getText().equals("C#"));
		
		return validRows;
		
	}
	
	/**
	 * Intent: (Post3) Extract checkpoints from comment list and store 
	 * checkpoints in a list. 
	 * 
	 * Postcondition1 (Track valid checkpoints): A list of Checkpoints is 
	 * created from valid checkpoint data.
	 * Postcondition2 (Track invalid checkpoints): A list of comments that
	 * contain invalid checkpoint data is created and stored in an exception.
	 */
	public void parseCheckpoints() throws InvalidCommentException {
		
		// Loop through comments list and extract comments that contain
		// checkpoints
		ArrayList<Integer> invalidCommentList = new ArrayList<>();
		for (Comment comment : commentList) {
			
			if (comment.getText().contains(CHECKPOINT)) {
				
				// Try to create a Checkpoint object
				try {
					
					Checkpoint checkpoint = 
							comment.extractCheckpoint(gradeMapping);
					//Post1 Keep track of any valid checkpoints
					checkpointList.add(checkpoint);
				
				} catch (InvalidCommentException e) {
					
					//Post2 Keep track of any invalid checkpoints
					int id = e.getCommentID();
					invalidCommentList.add(id);
					
				}
			}
		}
		
		// Test for invalid comments
		if (!(invalidCommentList.isEmpty()) ) {
			String message = "\nERROR: Invalid checkpoint data detected in "
					+ "file";
			throw new InvalidCommentException(message, invalidCommentList);
		}
		
		// Test for presence of checkpoints
		if (checkpointList.isEmpty()) {
			String message = "\nERROR: No checkpoints detected in file";
			throw new InvalidCommentException(message);
		}
		
	}
	
	/**
	 * Intent: (Post1) Extract the comments from the assignment document and
	 * store them in a list.
	 * 
	 * Postcondition1 (XWPF document creation): The assignment path has been 
	 * converted to a File and passed to the XWPFDocument constructor to create
	 * an XWPFDocument object that represents a Word document.
	 * Postcondition2 (List of Comments): A list of Comment objects is created
	 * from the XWPFDocument object.
	 * 
	 * @throws IOException
	 * @throws InvalidCommentException
	 */
	public void parseComments() throws IOException, InvalidCommentException {
		
		// Post1 XWPF document creation
		XWPFDocument documentContent = retrieveXWPFDocument(assignmentPath);
		
		// Post2 List of Comments 
		XWPFComment[] docCommentList = documentContent.getComments();
		
		// Loop through list and create Comment objects
		int length = docCommentList.length;
		for (int i = 0; i < length; i++) {
			
			XWPFComment dc = docCommentList[i];
			
			Comment comment = new Comment(dc.getId(), dc.getAuthor(), 
					dc.getText());
			
			commentList.add(comment);
		}
		
		// Close open resources
		documentContent.close();
		
		// Check if any comments were found in the document
		if (commentList.isEmpty()) {
			throw new InvalidCommentException("\nERROR: No comments were found "
					+ "in file");
		}
	}
	
	/**
	 * Intent: (Post2) Extract grade mapping from comment list and store
	 * in GradeMapping instance variable.
	 */
	public void parseGradeMapping() throws InvalidCommentException {
		
		// Loop through comments list and extract grade mapping
		for (Comment comment: commentList) {
			
			if (comment.getText().contains(GRADE_MAPPING)) {
				gradeMapping = comment.extractGradeMapping();
				break;
			}
		}
		
		// Set default grade mapping if one is not provided in the comments
		if (gradeMapping.getLimits().isEmpty()) {
			setDefaultGradeMapping();
		}
		
	}
	
	/**
	 * Intent: Retrieve previous result from txt file and create Result
	 * and GradeMapping objects stored in Document.
	 * 
	 * Precondition1 (Previously graded): Assignment document has been 
	 * previously graded and a txt file for that assignment exits.
	 * 
	 * Postcondition1 (Previous result): Previous result's hash 
	 * string is retrieved from txt file.
	 * Postcondition2 (Create JWT): JWT object is created using user's secret.
	 * Postcondition3 (Decode hash string): Previous result's hash string
	 * is decoded and Result and GradeMapping objects have been created and 
	 * stored in Document object.
	 * @param secret String that represents user secret
	 */
	public void retrieveResult(String secret) throws IOException, 
			InvalidTableException, InvalidCheckpointException,	
			InvalidGradeMappingException {
		
		// Post1 Previous result
		getHashStringFromFile();
		
		// Post2 Create JWT
		JWT jwt = new JWT(secret);
		
		// Post3 Decode hash string
		jwt.decode(this);
	}
	
	/**
	 * Intent: Open and return an XWPFDocument object that represents a Word
	 * document.
	 *  
	 * @param path 
	 * @return XWPFDocument object
	 * @throws IOException
	 */
	private static XWPFDocument retrieveXWPFDocument(Path path) 
			throws IOException {
		
		// Get file path to Word Document
		File file = path.toFile();
		FileInputStream fileInputStream = 
				new FileInputStream(file.getAbsolutePath());
		
		// Create XWPFDocument
		XWPFDocument documentContent = new XWPFDocument(fileInputStream);
		
		// Close input stream
		fileInputStream.close();
		
		return documentContent;
	}
	
	public void setCommentList(ArrayList<Comment> cList) {
		commentList = cList;
	}
	
	/**
	 * Intent: Set default grade mapping for Document and print message to 
	 * console to notify user.
	 */
	private void setDefaultGradeMapping() {
		
		System.out.println("\nDid not find a grade mapping for assignment:"
				+ "\n\t" + assignmentPath.getFileName().toString());
		gradeMapping.setDefaultGradeMapping();
		System.out.println("Default mapping is set to: \n" + gradeMapping);
	}
	
	public void setGradeMapping(GradeMapping gMap) {
		gradeMapping = gMap;
	}
	
	public void setHashString(String hString) {
		hashString = hString;
	}
	
	public void setResult(Result calculatedResult) {
		result = calculatedResult;
	}
	
	/**
	 * Intent: Create and set total grade and hash string row.
	 */
	private void setResultRow() {
		
		// Create result row
		XWPFTableRow resultRow = table.createRow();
		resultRow.getCell(0).setText("");
		resultRow.getCell(1).setText("Σ");
        resultRow.getCell(2).setText(String.format("%.2f", result.getTotalGrade()));
        resultRow.getCell(3).setText(hashString);

        // Format result row
        resultRow.getCell(2).setColor("8fbc8f");
		
	}
	
	/**
	 * Intent: Create and set checkpoint rows for table.
	 */
	private void setTableCheckpointRows() {
		
		// Create a row for each Checkpoint from Result object
		HashMap<Integer, Checkpoint>  checkpointMap = result.getCheckpointMap();
		for (Map.Entry<Integer, Checkpoint> entry : checkpointMap.entrySet()) {
			
			int id = entry.getKey();
			Checkpoint checkpoint = entry.getValue();
			
			XWPFTableRow checkpointRow = table.createRow();
			checkpointRow.getCell(0).setText("" + id);
			checkpointRow.getCell(1).setText("" + checkpoint.getWeight());
			checkpointRow.getCell(2).setText("" + checkpoint.getGrade());
			checkpointRow.getCell(3).setText("" + checkpoint.getFeedback());
			
		}
		
		
	}
	
	/**
	 * Intent: Create and set column header row for table.
	 */
	private void setTableColumnHeader() {
		
		// Create column header row
		XWPFTableRow columnHeadersRow = table.getRow(0);
		columnHeadersRow.getCell(0).setText("C#");
		columnHeadersRow.addNewTableCell().setText("Weight");
		columnHeadersRow.addNewTableCell().setText("Grade");
		columnHeadersRow.addNewTableCell().setText("Feedback");
		
		// Format column header row
		columnHeadersRow.getCell(0).setColor("c0c0c0");
		columnHeadersRow.getCell(1).setColor("c0c0c0");
		columnHeadersRow.getCell(2).setColor("c0c0c0");
		columnHeadersRow.getCell(3).setColor("c0c0c0");
	}
}
