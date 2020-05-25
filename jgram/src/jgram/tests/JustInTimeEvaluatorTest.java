package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import jgram.assessment.Checkpoint;
import jgram.assessment.JustInTimeEvaluator;
import jgram.assessment.Result;
import jgram.exceptions.InvalidCheckpointException;

class JustInTimeEvaluatorTest {
	
	/**
	 * Test the JustInTimeEvaluator class' evaluate method calculate correct 
	 * total grade.
	 */
	@Test
	void Checkpointte() {
		
		// Create checkpoints
		ArrayList<Checkpoint> checkpointList = new ArrayList<>();
		
		try {
			checkpointList.add(new Checkpoint(3, 85, "Okay Job."));
			checkpointList.add(new Checkpoint(3, 100, "Excellent Job."));
			checkpointList.add(new Checkpoint(4, 90, "Good Job."));
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		}
		
		// Evaluate checkpoints in list to determine total grade
		JustInTimeEvaluator jitEval = new JustInTimeEvaluator(checkpointList);
		Result result = jitEval.evaluate();
		
		assertEquals(91.5, result.getTotalGrade());
		
	}

}
