package jgram.tests;

import jgram.assessment.Checkpoint;
import jgram.assessment.JustInTimeEvaluator;
import jgram.assessment.Result;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ResultTest {

	@Test
	void testEqualsObject() {
		
		ArrayList<Checkpoint> checkpointList1 = new ArrayList<>();
		checkpointList1.add(new Checkpoint(3, 85, "Okay Job."));
		checkpointList1.add(new Checkpoint(3, 100, "Excellent Job."));
		checkpointList1.add(new Checkpoint(4, 90, "Good Job."));
		JustInTimeEvaluator jitEval1 = new JustInTimeEvaluator(checkpointList1);
		Result result1 = jitEval1.evaluate();
		
		ArrayList<Checkpoint> checkpointList2 = new ArrayList<>();
		checkpointList2.add(new Checkpoint(3, 85, "Okay Job."));
		checkpointList2.add(new Checkpoint(3, 100, "Excellent Job."));
		checkpointList2.add(new Checkpoint(4, 90, "Good Job."));
		JustInTimeEvaluator jitEval2 = new JustInTimeEvaluator(checkpointList2);
		Result result2 = jitEval2.evaluate();
		
		assertTrue(result1.equals(result2));
	}

}
