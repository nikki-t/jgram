package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import jgram.assessment.Checkpoint;
import jgram.assessment.JustInTimeEvaluator;
import jgram.assessment.Result;
import jgram.exceptions.InvalidCheckpointException;
import jgram.utilities.LinkedList;

class JustInTimeEvaluatorTest {
	
	/**
	 * Test the JustInTimeEvaluator class' evaluate method calculate correct 
	 * total grade.
	 */
	@Test
	void testEvaluate() {
		
		// Create checkpoints
		LinkedList<Checkpoint> checkpointList = new LinkedList<>();
		
		try {
			checkpointList.add(new Checkpoint(3, 85, "Okay Job.", 1));
			checkpointList.add(new Checkpoint(3, 100, "Excellent Job.", 2));
			checkpointList.add(new Checkpoint(4, 90, "Good Job.", 3));
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		}
		
		// Evaluate checkpoints in list to determine total grade
		JustInTimeEvaluator jitEval = new JustInTimeEvaluator(checkpointList);
		Result result = jitEval.evaluate();
		
		assertEquals(91.5, result.getTotalGrade());
		
	}

}
