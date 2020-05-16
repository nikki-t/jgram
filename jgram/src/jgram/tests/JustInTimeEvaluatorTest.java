package jgram.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import jgram.assessment.Checkpoint;
import jgram.assessment.JustInTimeEvaluator;
import jgram.assessment.Result;

class JustInTimeEvaluatorTest {

	@Test
	void Checkpointte() {
		
		ArrayList<Checkpoint> checkpointList = new ArrayList<>();
		checkpointList.add(new Checkpoint(3, 85, "Okay Job."));
		checkpointList.add(new Checkpoint(3, 100, "Excellent Job."));
		checkpointList.add(new Checkpoint(4, 90, "Good Job."));
		
		JustInTimeEvaluator jitEval = new JustInTimeEvaluator(checkpointList);
		
		Result result = jitEval.evaluate();
		
		assertEquals(91.5, result.getTotalGrade());
		
	}

}
