package jgram.tests;

import jgram.assessment.Checkpoint;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CheckpointTest {
	
	/**
	 * Test the creation of Checkpoint objects.
	 */
	@Test
	void testToString() {
		
		Checkpoint checkpoint = new Checkpoint(3, 85, "Great Job.");
		
		assertEquals("Weight: 3, Grade: 85, Feedback: Great Job.", 
				checkpoint.toString());
		
	}
	
	/**
	 * Test that two Checkpoint objects are equal if they have the same
	 * weight, grade, and feedback.
	 */
	@Test
	void testEquals() {
		
		Checkpoint checkpoint1 = new Checkpoint(3, 85, "Great Job.");
		Checkpoint checkpoint2 = new Checkpoint(3, 85, "Great Job.");
		
		assertTrue(checkpoint1.equals(checkpoint2));
		
	}
	
	/**
	 * Test that two Checkpoint objects are not equal if they do not have the 
	 * same weight, grade, and feedback.
	 */
	@Test
	void testNotEquals() {
		
		Checkpoint checkpoint1 = new Checkpoint(3, 85, "Great Job.");
		Checkpoint checkpoint2 = new Checkpoint(3, 90, "Great Job.");
		
		assertFalse(checkpoint1.equals(checkpoint2));
		
	}

}
