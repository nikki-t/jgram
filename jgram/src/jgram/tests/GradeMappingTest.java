package jgram.tests;

import jgram.assessment.Comment;
import jgram.assessment.GradeMapping;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GradeMappingTest {
	
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
		GradeMapping gradeMapping2 = comment.extractGradeMapping();
		
		assertEquals(gradeMapping2, gradeMapping1);
		
	}
	
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
		GradeMapping gradeMapping2 = comment.extractGradeMapping();
		
		assertTrue(gradeMapping1.equals(gradeMapping2));
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
		GradeMapping gradeMapping2 = comment.extractGradeMapping();
		
		assertFalse(gradeMapping1.equals(gradeMapping2));
	}

}
