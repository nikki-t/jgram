package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import jgram.assessment.Comment;
import jgram.assessment.GradeMapping;
import jgram.exceptions.InvalidCommentException;
import jgram.exceptions.InvalidGradeMappingException;

class GradeMappingTest {
	
	/**
	 * Test that two GradeMapping objects are equal if they have the same
	 * grade letters mapped to the same grade numbers.
	 */
	@Test
	void testEquals() {
		
		GradeMapping gradeMapping1 = new GradeMapping();
		gradeMapping1.setDefaultGradeMapping();
		
		Comment comment = new Comment("3", "Nikki Tebaldi", 
				"GRADEMAPPING(A+=97, A=95, A-=93, B+=87, B=85, B-=83, C+=77, "
				+ "C=75, C-=73, F=67)");
		
		try {
			GradeMapping gradeMapping2 = comment.extractGradeMapping();
			assertTrue(gradeMapping1.equals(gradeMapping2));
		
		} catch (InvalidCommentException e) {
			fail("Invalid grade mapping detected.");
		}
	}
	
	/**
	 * Test that InvalidGradeMappingException is thrown when invalid
	 * data is added to a GradeMapping object.
	 */
	@Test
	void testInvalidGradeMapping() {
		
		assertThrows(InvalidGradeMappingException.class, () -> {
			@SuppressWarnings("unused")
			// Create grade mapping
			GradeMapping gradeMapping = new GradeMapping();
			gradeMapping.setGrade("A+", 97);
			gradeMapping.setGrade("A", 95);
			gradeMapping.setGrade("A-", 93);
			gradeMapping.setGrade("B+", 102);
			gradeMapping.setGrade("B", 85);
			gradeMapping.setGrade("B-", 83);
			gradeMapping.setGrade("C+", 77);
			gradeMapping.setGrade("C", 75);
			gradeMapping.setGrade("C-", 73);
			gradeMapping.setGrade("Z", 67);
		});
		
	}
	
	/**
	 * Test that two GradeMapping objects are not equal if they do not have the 
	 * same grade letters mapped to the same grade numbers.
	 */
	@Test
	void testNotEquals() {
		
		GradeMapping gradeMapping1 = new GradeMapping();
		gradeMapping1.setDefaultGradeMapping();
		
		Comment comment = new Comment("3", "Nikki Tebaldi", 
				"GRADEMAPPING(A+=100, A=95, A-=93, B+=87, B=85, B-=83, C+=77, "
				+ "C=75, C-=73, F=65)");
		
		try {
			GradeMapping gradeMapping2 = comment.extractGradeMapping();
			assertFalse(gradeMapping1.equals(gradeMapping2));
		
		} catch (InvalidCommentException e) {
			fail("Invalid grade mapping detected.");
		}
	}
	
	/**
	 * Test the creation of GradeMapping objects.
	 */
	@Test
	void testToString() {
		
		GradeMapping gradeMapping1 = new GradeMapping();
		gradeMapping1.setDefaultGradeMapping();
		
		Comment comment = new Comment("3", "Nikki Tebaldi", 
				"GRADEMAPPING(A+=97, A=95, A-=93, B+=87, B=85, B-=83, C+=77, "
				+ "C=75, C-=73, F=67)");
		
		try {
			GradeMapping gradeMapping2 = comment.extractGradeMapping();
			assertEquals(gradeMapping2, gradeMapping1);
		
		} catch (InvalidCommentException e) {
			fail("Invalid grade mapping detected.");
		}
		
	}

}
