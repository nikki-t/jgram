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
import jgram.exceptions.InvalidCheckpointException;
import jgram.exceptions.InvalidCommentException;
import jgram.exceptions.InvalidGradeMappingException;
import jgram.exceptions.InvalidTableException;
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
		document.createHashString(secret.getID(), secret.getIssuer(), 
				secret.getSubject(), secret.getSecret());
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
	
	/**
	 * Intent: Test that the hash string returned from a graded file
	 * is the same as the hash string constructed from grading the file.
	 */
	@Test
	void testGetHashStringFromFile() {
		
		// Locate ungraded test file
		Path ungradedDocumentPath = TestUtilities
				.returnAssignmentPath("document-test.docx");
		
		// Create a secret object to use for hash string creation
		Secret secret = new Secret("secret");
					
		// Create a Document object, parse it for checkpoints, and grade it
		Document ungradedDocument = new Document(ungradedDocumentPath);
		try {
			
			createDocument(ungradedDocument, secret);
			
		} catch (IOException | InvalidCommentException e) {
			fail("Invalid test file.");
		}
		
		// Retrieve graded result with hash string from graded test file
		Path gradedDocumentPath = TestUtilities
				.returnAssignmentPath("/GRADED/GRADED_document-test.docx");
		Document gradedDocument = new Document(gradedDocumentPath);
		try {
			
			gradedDocument.retrieveResult(secret.getSecret());
			
		} catch (IOException | InvalidGradeMappingException 
				| InvalidTableException | InvalidCheckpointException e) {
			fail("Unable to grade document or retrieve graded result.");
		}
		
		// Assert that hash string from document prior to writing result equals 
		// hash string in graded document
		assertEquals(ungradedDocument.getHashString(),
				gradedDocument.getHashString());
		
	}
	
	/**
	 * Test that an ArrayList of Checkpoint objects is created when 
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
			
			// Create an ArrayList of checkpoints for comparison
			ArrayList<Checkpoint> checkpointList = new ArrayList<>();
			
			checkpointList.add(new Checkpoint(3, 85, "Throws "
					+ "ArrayIndexOutOfBoundsException; watch out for the " 
					+ "Boolean condition that controls the for loop’s "
					+ "execution. This for loop executes one more time than " 
					+ "you would want it to because of the greater than or "  
					+ "equal to sign."));
			checkpointList.add(new Checkpoint(3, 100, "Excellent work. Please "
					+ "make sure to include comments next time."));
			checkpointList.add(new Checkpoint(4, 90, "Per the prompt, "
					+ "the method should return the first element in the array; " 
					+ "use the break keyword to exit the for loop once the " 
					+ "element is found."));
			
			assertEquals(checkpointList, document.getCheckpointList());
		
		} catch (IOException e) {
			fail("Attempted to parse invalid document for comments."); 
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		
		} catch (InvalidCommentException e) {
			fail("Invalid comment detected.");
		}
		
	}
	
	/**
	 * Test that an ArrayList of Comment objects is created when 
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
			ArrayList<Comment> commentsList = new ArrayList<>();
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
