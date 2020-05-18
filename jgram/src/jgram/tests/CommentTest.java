package jgram.tests;

import jgram.assessment.Checkpoint;
import jgram.assessment.Comment;
import jgram.assessment.GradeMapping;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommentTest {
	
	/**
	 * Test if two Comment objects are equal if they have the same id, author,
	 * and text.
	 */
	@Test
	void testEquals() {
		
		Comment c1 = new Comment("3", "Nikki Tebaldi", "GRADEMAPPING(A+=97, "
				+ "A=95, A-=93, B+=87, B=85, B-=83, C=77, F=67)");
		Comment c2 = new Comment("3", "Nikki Tebaldi", "GRADEMAPPING(A+=97, "
				+ "A=95, A-=93, B+=87, B=85, B-=83, C=77, F=67)");
		
		assertTrue(c1.equals(c2));
	}
	
	/**
	 * Test the creation of Comment objects.
	 */
	@Test
	void testToString() {
		
		Comment comment = new Comment("0", "Nikki Tebaldi", 
			"CHECKPOINT(WEIGHT=3, GRADE=100, FEEDBACK=[Excellent work.])");
		
		String commentString = "ID: 0, Author: Nikki Tebaldi, "
				+ "Comment Text: CHECKPOINT(WEIGHT=3, GRADE=100, "
				+ "FEEDBACK=[Excellent work.]) \n";
		
		assertEquals(commentString, comment.toString());
		
	}
	
	/**
	 * Test that a Grade Mapping object is created when correctly extracted
	 * from a Comment object.
	 */
	@Test
	void testExtractGradeMapping() {
		
		Comment comment = new Comment("3", "Nikki Tebaldi", 
				"GRADEMAPPING(A+=97, A=95, A-=93, B+=87, B=85, B-=83, C+=77, "
				+ "C=75, C-=73, F=67)");
		GradeMapping gradeMapping1 = comment.extractGradeMapping();
		
		GradeMapping gradeMapping2 = new GradeMapping();
		gradeMapping2.setDefaultGradeMapping();
		
		assertEquals(gradeMapping2, gradeMapping1);
		
	}
	
	/**
	 * Test that a Checkpoint object is created when correctly extracted
	 * from a Comment object.
	 */
	@Test
	void testExtractCheckpoint() {
		
		Comment comment = new Comment("0", "Nikki Tebaldi", 
				"CHECKPOINT(WEIGHT=3, GRADE=100, FEEDBACK=[Excellent work.])");
		GradeMapping gradeMapping = new GradeMapping();
		gradeMapping.setDefaultGradeMapping();
		Checkpoint checkpoint1 = comment.extractCheckpoint(gradeMapping);
		
		Checkpoint checkpoint2 = new Checkpoint(3, 100, "Excellent work.");
		
		assertEquals(checkpoint1, checkpoint2);
		
			
	}

}
