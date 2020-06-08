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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;

import jgram.exceptions.InvalidCommentException;
import jgram.security.JWT;
import jgram.security.Secret;

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
 * 
 * References:
 * 
 * Aided in the use of Apache POI API:
 * Apache POI - Component Overview. (n.d.). Retrieved from Apache POI: http://poi.apache.org/components/
 * Apache POI - the Java API for Microsoft Documents. (n.d.). Retrieved from Apache POI: http://poi.apache.org/index.html
 * Apache POI JavaDocs. (n.d.). Retrieved from Apache POI: http://poi.apache.org/apidocs/4.1/
 * Is there a way to set the width of a column in XWPFTableCell? . (2012, 11 16). Retrieved from Apche POI > POI - User: http://apache-poi.1045710.n5.nabble.com/Is-there-a-way-to-set-the-width-of-a-column-in-XWPFTableCell-td5711491.html
 * examples. (n.d.). Retrieved from Apache POI: http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/xwpf/usermodel/examples/
 * 
 */
public class Document {
	
	// Class constant(s)
	// Checkpoint and grade mapping grammar
	private static final String CHECKPOINT = "CHECKPOINT";
	private static final String GRADE_MAPPING = "GRADEMAPPING";
	private static final String TABLE_TITLE = "JGRAM RESULTS";
	
	// Instance variable(s)
	private Path assignmentPath;
	private List<Comment> commentList;
	private List<Checkpoint> checkpointList;
	private GradeMapping gradeMapping;
	private Result result;
	private String hashString;
	
	private XWPFTable table;;
	
	// Constructor(s)
	public Document() {
		commentList = new ArrayList<>();
		checkpointList = new ArrayList<>();
		gradeMapping = new GradeMapping();
	}
	
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
	public void createHashString(Secret secret) {
		
		// Create JWT object
		JWT jwt = new JWT(secret);
		
		// Encode Document's Result and GradeMapping objects
		jwt.encode(this);
		
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
	 * Postcondition4 (Set each cell's width): Each cell width is adjusted to
	 * allow for the best display of cell content.
	 * 
	 * @param documentContent
	 */
	private void createTable(XWPFDocument documentContent) {
		
		// Create XWPF table object and set table width
		table = documentContent.createTable();
		CTTblLayoutType type = table.getCTTbl().getTblPr().addNewTblLayout();
		type.setType(STTblLayoutType.FIXED);
		
		// Post1 Column headers
		setTableColumnHeader();
		
		// Post2 Checkpoint rows
		setTableCheckpointRows();
		
		// Post3 Total grade and hash string row
		setResultRow();
		
		// Post4 Set each cell's width	
		setCellWidth();
		
	}
	
	public Path getAssignmentName() {
		return assignmentPath;
	}
	
	public List<Checkpoint> getCheckpointList() {
		return checkpointList;
	}
	
	public List<Comment> getCommentList() {
		return commentList;
	}
	
	public GradeMapping getGradeMapping() {
		return gradeMapping;
	}
	
	public String getHashString() {
		return hashString;
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
		for (Checkpoint checkpoint : checkpointList) {
			int weight = checkpoint.getWeight();
			int grade = checkpoint.getGrade();
			String feedback = checkpoint.getFeedback();
			int id = checkpoint.getID();
			
			resultTable.append(String.format("\n%4d %8d %7d   %s", id,
					weight, grade, feedback));
			
		}

		// Display total grade
		resultTable.append(String.format("\n%4s %8s %7.2f\n", "", "Σ",
			result.getTotalGrade()));


		// Return result table string
		return resultTable.toString();

	}
	
	/**
	 * Intent: (Post3) Extract checkpoints from comment list and store 
	 * checkpoints in a list. 
	 * 
	 * Postcondition1 (Track invalid checkpoints): A list of invalid comments is
	 * created that contain invalid checkpoint data.
	 * Postcondition2 (Predicate interface object): A Predicate interface object
	 * is created that tests for checkpoint data present in Comment objects.
	 * Postcondition3 (Function interface with result): A Function interface 
	 * object is created with one Comment argument that returns a Checkpoint 
	 * result.
	 * Postcondition4 (Extract and store checkpoints): All checkpoint data
	 * is extracted from Comment objects and stored as Checkpoint objects in
	 * a list.
	 * Postcondition5 (Test for invalid comments): Invalid comments have been 
	 * stored in a list if present and are passed as exception data to the 
	 * calling method.
	 */
	public void parseCheckpoints() throws InvalidCommentException {
		
		// Post1 Track invalid checkpoints
		ArrayList<Integer> invalidCommentList = new ArrayList<>();
		
		// Post2 Predicate interface object
		Predicate<Comment> isCheckpoint = (comment -> comment
														.getText()
														.contains(CHECKPOINT));
		
		// Post3 Function interface object with result
		Function<Comment, Checkpoint> checkpointCreator = comment -> {
			try {
				return comment.extractCheckpoint(gradeMapping);
			
			} catch (InvalidCommentException e) {
				int id = e.getCommentID();
				invalidCommentList.add(id);
				return null;
			}
		};
		
		
		// Post4 Extract and store checkpoints
		checkpointList =  commentList
				.stream()
				.filter(isCheckpoint)
				.map(checkpointCreator)
				.collect(Collectors.toList());
		
		// Post5 Test for invalid comments
		if (!(invalidCommentList.isEmpty()) ) {
			String message = "\nERROR: Invalid checkpoint data detected in "
					+ "file";
			throw new InvalidCommentException(message, invalidCommentList);
		}
		
	}
	
	/**
	 * Intent: (Post1) Extract the comments from the assignment document and
	 * store them in a list.
	 * 
	 * Postcondition1 (XWPF document creation): The assignment path has been 
	 * converted to a File and passed to the XWPFDocument constructor to create
	 * an XWPFDocument object that represents a Word document.
	 * Postcondition2 (List of Document comments ): A list of XWPFComment
	 * objects is created from the assignment document.
	 * Postcondition3 (Comment list creation): A list of Comment objects is 
	 * produced from a Stream of XWPFComment objects.
	 * 
	 * @throws IOException
	 * @throws InvalidCommentException
	 */
	public void parseComments() throws IOException, InvalidCommentException {
		
		// Post1 XWPF document creation
		XWPFDocument documentContent = retrieveXWPFDocument(assignmentPath);
		
		// Post2 List of Document comments 
		XWPFComment[] docCommentList = documentContent.getComments();
		
		// Post3 Comment list creation
		Stream<XWPFComment> xwpfStream = Stream.of(docCommentList);
		commentList = xwpfStream
				.map(dc -> new Comment(dc.getId(), dc.getAuthor(), dc.getText()))
				.collect(Collectors.toList());
		
		// Close open resources
		documentContent.close();
		
		
	}
	
	/**
	 * Intent: (Post2) Extract grade mapping from comment list and store
	 * in GradeMapping instance variable.
	 * 
	 * Precondition1 (Comment data): Comment data has been parsed and stored.
	 * 
	 * Postcondition1 (Predicate interface object): A predicate interface 
	 * object is defined as an argument to the filter operation.
	 * Postcondition2 (Extract grade mapping data): A GradeMapping object
	 * is created from the results of filtering the comment stream.
	 */
	public void parseGradeMapping() throws InvalidCommentException {
		
		
		// Post1 Predicate interface object
		Predicate<Comment> isGradeMapping = (comment -> comment
														.getText()
														.contains(GRADE_MAPPING));
		
		// Post2 Extract grade mapping data
		Optional<Comment> gradeMappingData = commentList
				.stream()
				.filter(isGradeMapping)
				.findAny();
		
		// Test if grade mapping data was retrieved
		if (gradeMappingData.isPresent()) {
			gradeMapping = gradeMappingData.get().extractGradeMapping();
		} else {
			gradeMapping = new GradeMapping();
		}
				

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
	
	/**
	 * Intent: Set the cell width for each cell in the table instance variable.
	 */
	private void setCellWidth() {
		
		// Create a list of table widths
		int[] columnWidth = {1000, 1000, 1000, 6000};
		
		// Get a list of the rows
		List<XWPFTableRow> rows = table.getRows();
		
		// Loop through each row
		for (XWPFTableRow row : rows) {
			
			// Get a list of the cells
			List<XWPFTableCell> cells = row.getTableCells();
			
			// Loop through each cell
			int i = 0;
			for (XWPFTableCell cell : cells) {
				
				// Set the XML element that holds each cell's metadata
				cell.getCTTc()
					.addNewTcPr()
					.addNewTcW()
					.setW(BigInteger.valueOf(columnWidth[i]));
				
				i++;
				
			}
		}
		
		
		
	}
	
	public void setCheckpointList(List<Checkpoint> cpList) {
		checkpointList = cpList;
	}
	
	public void setCommentList(List<Comment> cList) {
		commentList = cList;
	}
	
	/**
	 * Intent: Set default grade mapping for Document and print message to 
	 * console to notify user.
	 */
	public void setDefaultGradeMapping() {
		
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
        resultRow.getCell(2).setText(String.format("%.2f", 
        		result.getTotalGrade()));

        // Format result row
        resultRow.getCell(2).setColor("8fbc8f");
		
	}
	
	/**
	 * Intent: Create and set checkpoint rows for table.
	 */
	private void setTableCheckpointRows() {
		
		// Create a row for each Checkpoint
		for (Checkpoint checkpoint : checkpointList) {
			XWPFTableRow checkpointRow = table.createRow();
			checkpointRow.getCell(0).setText("" + checkpoint.getID());
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
