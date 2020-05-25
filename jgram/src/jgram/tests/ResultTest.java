package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import jgram.assessment.Checkpoint;
import jgram.assessment.JustInTimeEvaluator;
import jgram.assessment.Result;
import jgram.exceptions.InvalidCheckpointException;

class ResultTest {
	
	/**
	 * Test that two Result objects are equal when they have the same 
	 * Checkpoints (Checkpoint map) and total grade.
	 */
	@Test
	void testEqualsObject() {
		
		try {
		
			// Create a list of checkpoints
			ArrayList<Checkpoint> checkpointList1 = new ArrayList<>();
			checkpointList1.add(new Checkpoint(3, 85, "Okay Job."));
			checkpointList1.add(new Checkpoint(3, 100, "Excellent Job."));
			checkpointList1.add(new Checkpoint(4, 90, "Good Job."));
			JustInTimeEvaluator jitEval1 = new JustInTimeEvaluator(checkpointList1);
			Result result1 = jitEval1.evaluate();
			
			// Create a second list of checkpoints
			ArrayList<Checkpoint> checkpointList2 = new ArrayList<>();
			checkpointList2.add(new Checkpoint(3, 85, "Okay Job."));
			checkpointList2.add(new Checkpoint(3, 100, "Excellent Job."));
			checkpointList2.add(new Checkpoint(4, 90, "Good Job."));
			JustInTimeEvaluator jitEval2 = new JustInTimeEvaluator(checkpointList2);
			Result result2 = jitEval2.evaluate();
			
			// Assert that the evaluation of both checkpoint lists are equal
			assertTrue(result1.equals(result2));
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		}
	}

}
