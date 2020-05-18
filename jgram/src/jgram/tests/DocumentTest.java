package jgram.tests;

import static org.junit.jupiter.api.Assertions.*;

import jgram.assessment.Checkpoint;
import jgram.assessment.Comment;
import jgram.assessment.Document;
import jgram.assessment.GradeMapping;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class DocumentTest {
	
	/**
	 * Test that an ArrayList of Comment objects is created when 
	 * Document.parseComments is called.
	 */
	@Test
	void testParseComments() {
		
		// Simulate document comments
		String[] comment1 = {"0", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=85, FEEDBACK=[Okay work.])"};
		String[] comment2 = {"1", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=100, FEEDBACK=[Excellent work.])"};
		String[] comment3 = {"2", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=4, "
				+ "GRADE=90, FEEDBACK=[Good work.])"};
		String[] comment4 = {"3", "Nikki Tebaldi", "GRADEMAPPING(A+=97, "
				+ "A=95, A-=93, B+=87, B=85, B-=83, C=77, F=67)"};
		String[][] documentComments = {comment1, comment2, comment3, comment4};
		
		// Create a document and parse it for comments
		Document document1 = new Document(documentComments);
		document1.parseComments();
		
		// Create a list of comments to compare Document operation result to
		ArrayList<Comment> commentsList = new ArrayList<>();
		Comment c1 = new Comment("0", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=85, FEEDBACK=[Okay work.])");
		Comment c2 = new Comment("1", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=100, FEEDBACK=[Excellent work.])");
		Comment c3 = new Comment("2", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=4, "
				+ "GRADE=90, FEEDBACK=[Good work.])");
		Comment c4 = new Comment("3", "Nikki Tebaldi", "GRADEMAPPING(A+=97, "
				+ "A=95, A-=93, B+=87, B=85, B-=83, C=77, F=67)");
		commentsList.add(c1);
		commentsList.add(c2);
		commentsList.add(c3);
		commentsList.add(c4);
		
		// Make sure comment equality is tested
		assertTrue(Arrays.deepEquals(commentsList.toArray(), 
				document1.getCommentList().toArray()));
		
	}
	
	/**
	 * Test that a GradeMapping objects is created when 
	 * Document.parseGradeMapping is called.
	 */
	@Test
	void testParseGradeMapping() {
		
		// Simulate a documents comments
		String[] comment1 = {"0", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=85, FEEDBACK=[Okay work.])"};
		String[] comment2 = {"1", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=100, FEEDBACK=[Excellent work.])"};
		String[] comment3 = {"2", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=4, "
				+ "GRADE=90, FEEDBACK=[Good work.])"};
		String[] comment4 = {"3", "Nikki Tebaldi", 
				"GRADEMAPPING(A+=97, A=95, A-=93, B+=87, B=85, B-=83, "
				+ "C+=77, C=75, C-=73, F=67)"};
		String[][] documentComments = {comment1, comment2, comment3, comment4};
		
		// Create a Document and parse it for a grade mapping
		Document document = new Document(documentComments);
		document.parseComments();
		document.parseGradeMapping();
		
		// Create a default grade mapping for comparison
		GradeMapping gradeMapping = new GradeMapping();
		gradeMapping.setDefaultGradeMapping();
		
		assertEquals(gradeMapping, document.getGradeMapping());
		
	}
	
	/**
	 * Test that an ArrayList of Checkpoint objects is created when 
	 * Document.parseCheckponts is called.
	 */
	@Test
	void testParseCheckpoints() {
		
		// Simulate a document's comments
		String[] comment1 = {"0", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=85, FEEDBACK=[Okay work.])"};
		String[] comment2 = {"1", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=100, FEEDBACK=[Excellent work.])"};
		String[] comment3 = {"2", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=4, "
				+ "GRADE=90, FEEDBACK=[Good work.])"};
		String[] comment4 = {"3", "Nikki Tebaldi", 
				"GRADEMAPPING(A+=97, A=95, A-=93, B+=87, B=85, B-=83, "
				+ "C+=77, C=75, C-=73, F=67)"};
		String[][] documentComments = {comment1, comment2, comment3, comment4};
		
		// Create a Document object and parse it for checkpoints
		Document document = new Document(documentComments);
		document.parseComments();
		GradeMapping gradeMapping = new GradeMapping();
		gradeMapping.setDefaultGradeMapping();
		document.setGradeMapping(gradeMapping);
		document.parseCheckpoints();
		
		// Create an ArrayList of checkpoints for comparison
		ArrayList<Checkpoint> checkpointList = new ArrayList<>();
		checkpointList.add(new Checkpoint(3, 85, "Okay work."));
		checkpointList.add(new Checkpoint(3, 100, "Excellent work."));
		checkpointList.add(new Checkpoint(4, 90, "Good work."));
		
		assertEquals(checkpointList, document.getCheckpointList());
		
	}

}
