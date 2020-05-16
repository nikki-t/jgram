package jgram.tests;

import jgram.assessment.Checkpoint;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CheckpointTest {

	@Test
	void testToString() {
		
		Checkpoint checkpoint = new Checkpoint(3, 85, "Great Job.");
		
		assertEquals("Weight: 3, Grade: 85, Feedback: Great Job.", 
				checkpoint.toString());
		
	}

	@Test
	void testEquals() {
		
		Checkpoint checkpoint1 = new Checkpoint(3, 85, "Great Job.");
		Checkpoint checkpoint2 = new Checkpoint(3, 85, "Great Job.");
		
		assertTrue(checkpoint1.equals(checkpoint2));
		
	}
	
	@Test
	void testNotEquals() {
		
		Checkpoint checkpoint1 = new Checkpoint(3, 85, "Great Job.");
		Checkpoint checkpoint2 = new Checkpoint(3, 90, "Great Job.");
		
		assertFalse(checkpoint1.equals(checkpoint2));
		
	}

}
