package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import jgram.assessment.Checkpoint;
import jgram.exceptions.InvalidCheckpointException;

class CheckpointTest {
	
	/**
	 * Test that two Checkpoint objects are equal if they have the same
	 * weight, grade, and feedback.
	 */
	@Test
	void testEquals() {
		
		try {
			Checkpoint checkpoint1 = new Checkpoint(3, 85, "Great Job.");
			Checkpoint checkpoint2 = new Checkpoint(3, 85, "Great Job.");
			assertTrue(checkpoint1.equals(checkpoint2));
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		}
		
	}
	
	/**
	 * Test that InvalidCheckpointException is thrown when invalid
	 * data is passed to Checkpoint constructor.
	 */
	@Test
	void testInvalidCheckpoint() {
		
		assertThrows(InvalidCheckpointException.class, () -> {
			@SuppressWarnings("unused")
			Checkpoint checkpoint = new Checkpoint(3, 101, "Great Job.");
		});
		
	}
	
	/**
	 * Test that two Checkpoint objects are not equal if they do not have the 
	 * same weight, grade, and feedback.
	 */
	@Test
	void testNotEquals() {
		
		try {
			Checkpoint checkpoint1 = new Checkpoint(3, 85, "Great Job.");
			Checkpoint checkpoint2 = new Checkpoint(3, 90, "Great Job.");
			assertFalse(checkpoint1.equals(checkpoint2));
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		}
		
	}
	
	/**
	 * Test the creation of Checkpoint objects.
	 */
	@Test
	void testToString() {
		
		try {
			Checkpoint checkpoint = new Checkpoint(3, 85, "Great Job.");
			assertEquals("Weight: 3, Grade: 85, Feedback: Great Job.", 
				checkpoint.toString());
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		}
		
	}

}
