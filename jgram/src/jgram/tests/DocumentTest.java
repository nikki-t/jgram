package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;

import jgram.assessment.Checkpoint;
import jgram.assessment.Comment;
import jgram.assessment.Document;
import jgram.assessment.GradeMapping;
import jgram.assessment.JustInTimeEvaluator;
import jgram.assessment.Result;
import jgram.exceptions.InvalidCheckpointException;
import jgram.exceptions.InvalidCommentException;
import jgram.security.Secret;

class DocumentTest {
	
	/**
	 * Intent: Create a Document object.
	 * @param document
	 * @param secret
	 * @throws IOException
	 * @throws InvalidCommentException
	 */
	private static void createDocument(Document document, Secret secret) 
			throws IOException, InvalidCommentException {
		
		document.parseComments();
		document.parseGradeMapping();
		document.parseCheckpoints();
		document.calculateResult();
		document.createHashString(secret);
		document.createGradedAssignment();
	}
	
	/**
	 * Test the output of creating a graded assignment, specifically that
	 * a 'GRADED' sub-directory and 'GRADED<assignment_name>' exist and
	 * the graded assignment contains a result table.
	 */
	@Test
	void testCreateGradedAssignment() {
		
		// Locate test assignment file
		Path resourceDocument = TestUtilities
				.returnAssignmentPath("document-test.docx");
		
		// Create a secret object to use for hash string creation
		Secret secret = new Secret("secret");
					
		// Create a Document object, parse it for checkpoints, and grade it
		Document document = new Document(resourceDocument);
		try {
			createDocument(document, secret);
		} catch (IOException | InvalidCommentException e) {
			fail("Invalid test file.");
		}
		
		// Assert graded directory exists
		String gradedDirectory = resourceDocument.getParent().toString() + "/GRADED";
		Path gradedPath = Paths.get(gradedDirectory);
		
		assertTrue(Files.isDirectory(gradedPath));
		
		// Assert graded assignment exists
		String filename = document.getAssignmentName().getFileName().toString();
		String gradedAssignment = gradedDirectory + "/GRADED_" + filename;
		File gradedFile = new File(gradedAssignment);
		
		assertTrue(gradedFile.exists());
		
		
		// Assert graded assignment contains table
		try {
						
			// Get rows of last table
			List<XWPFTableRow> rows = TestUtilities.createRowsFromTable(gradedFile);
			
			// Assert content of first cell in table
			assertEquals("C#", rows.get(0).getCell(0).getText());
			
		} catch (IOException e) {
			
			fail("Error retrieving result to test for table.");
		} 
		
	}
	
	@Test
	void testGetResultTableString() {
		
		// Create an LinkedList of checkpoints
		List<Checkpoint> checkpointList = new ArrayList<>();
		
		try {
			checkpointList.add(new Checkpoint(7, 90, "", 1));
			checkpointList.add(new Checkpoint(5, 91, "great clarity", 2));
			checkpointList.add(new Checkpoint(7, 95, "Use generics, "
					+ "but overall good work", 3));
			
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoint data encountered.");
		}
		
		// Create Result object
		JustInTimeEvaluator jitEval1 = new JustInTimeEvaluator(checkpointList);
		Result result = jitEval1.evaluate();
		
		// Create a new document
		Document document = new Document();
		document.setCheckpointList(checkpointList);
		document.setResult(result);
		
		// Retrieve resultTable
		String resultTable = document.getResultTableString();
		
		// Expected output
		String expectedTable =  "  C#   Weight   Grade   Feedback"
			+ "\n   1        7      90   "
			+ "\n   2        5      91   great clarity"
			+ "\n   3        7      95   Use generics, but overall good work"
			+ "\n            Σ   92.11\n";
		
		assertEquals(expectedTable, resultTable);

	}
	
	/**
	 * Test that an LinkedList of Checkpoint objects is created when 
	 * Document.parseCheckponts is called.
	 */
	@Test
	void testParseCheckpoints() {
		
		try {
			
			// Locate test assignment file
			Path resourceDocument = TestUtilities
					.returnAssignmentPath("document-test.docx");
						
			// Create a Document object and parse it for checkpoints
			Document document = new Document(resourceDocument);
			document.parseComments();			
			document.parseCheckpoints();
			
			// Create an LinkedList of checkpoints for comparison
			List<Checkpoint> checkpointList = new ArrayList<>();
			
			checkpointList.add(new Checkpoint(3, 85, "Throws "
					+ "ArrayIndexOutOfBoundsException; watch out for the " 
					+ "Boolean condition that controls the for loop’s "
					+ "execution. This for loop executes one more time than " 
					+ "you would want it to because of the greater than or "  
					+ "equal to sign.", 1));
			checkpointList.add(new Checkpoint(3, 100, "Excellent work. Please "
					+ "make sure to include comments next time.", 2));
			checkpointList.add(new Checkpoint(4, 90, "Per the prompt, "
					+ "the method should return the first element in the array; " 
					+ "use the break keyword to exit the for loop once the " 
					+ "element is found.", 3));
			
			
			assertTrue(Arrays.deepEquals(checkpointList.toArray(), 
					document.getCheckpointList().toArray()));
		
		} catch (IOException e) {
			fail("Attempted to parse invalid document for comments."); 
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		
		} catch (InvalidCommentException e) {
			fail("Invalid comment detected.");
		}
		
	}
	
	/**
	 * Test that an  of Comment objects is created when 
	 * Document.parseComments is called.
	 */
	@Test
	void testParseComments() {
		
		try {
			
			// Locate test assignment file
			Path resourceDocument = TestUtilities
					.returnAssignmentPath("document-test.docx");
			
			// Create a document and parse it for comments
			Document document1 = new Document(resourceDocument);
			document1.parseComments();
			
			// Create a list of comments to compare Document operation result to
			List<Comment> commentsList = new ArrayList<>();
			Comment c1 = new Comment("0", "Nikki Tebaldi", 
					"CHECKPOINT( WEIGHT=3, GRADE=85, FEEDBACK=[Throws "
					+ "ArrayIndexOutOfBoundsException; watch out for the "
					+ "Boolean condition that controls the for loop’s "
					+ "execution. This for loop executes one more time than "
					+ "you would want it to because of the greater than or "
					+ "equal to sign.])");
			Comment c2 = new Comment("1", "Nikki Tebaldi", 
					"CHECKPOINT( WEIGHT=3, GRADE=100, FEEDBACK=[Excellent work."
					+ " Please make sure to include comments next time.])");
			Comment c3 = new Comment("2", "Nikki Tebaldi", 
					"CHECKPOINT( WEIGHT=4, GRADE=90, FEEDBACK=[Per the prompt, "
					+ "the method should return the first element in the array; "
					+ "use the break keyword to exit the for loop once the "
					+ "element is found.])");
			Comment c4 = new Comment("3", "Nikki Tebaldi", 
					"GRADEMAPPING( A+=97, A=95, A-=93, B+=87, B=85, B-=83, "
					+ "C+=77, C=75, C-=73, F=67)");
			
			commentsList.add(c1);
			commentsList.add(c2);
			commentsList.add(c3);
			commentsList.add(c4);
			
			// Make sure comment equality is tested
			assertTrue(Arrays.deepEquals(commentsList.toArray(), 
					document1.getCommentList().toArray()));
		
		} catch (IOException e) {
			fail("Attempted to parse invalid document for comments.");
		} catch (InvalidCommentException e) {
			fail("Invalid comment detected.");
		} 
		
	}
	
	/**
	 * Test that a GradeMapping objects is created when 
	 * Document.parseGradeMapping is called.
	 */
	@Test
	void testParseGradeMapping() {
		
		try {
			
			// Locate test assignment file
			Path resourceDocument = TestUtilities
					.returnAssignmentPath("document-test.docx");
			
			// Create a Document and parse it for a grade mapping
			Document document = new Document(resourceDocument);
			document.parseComments();
			document.parseGradeMapping();
			
			// Create a default grade mapping for comparison
			GradeMapping gradeMapping = new GradeMapping();
			gradeMapping.setDefaultGradeMapping();
			
			assertEquals(gradeMapping, document.getGradeMapping());
		
		} catch (IOException e) {
			fail("Attempted to parse invalid document for comments."); 
		
		} catch (InvalidCommentException e) {
			fail("Invalid comment detected.");
		}
		
	}
}
