package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

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
			List<Checkpoint> checkpointList1 = new ArrayList<>();
			checkpointList1.add(new Checkpoint(3, 85, "Okay Job.", 1));
			checkpointList1.add(new Checkpoint(3, 100, "Excellent Job.", 2));
			checkpointList1.add(new Checkpoint(4, 90, "Good Job.", 3));
			JustInTimeEvaluator jitEval1 = new JustInTimeEvaluator(checkpointList1);
			Result result1 = jitEval1.evaluate();
			
			// Create a second list of checkpoints
			List<Checkpoint> checkpointList2 = new ArrayList<>();
			checkpointList2.add(new Checkpoint(3, 85, "Okay Job.", 1));
			checkpointList2.add(new Checkpoint(3, 100, "Excellent Job.", 2));
			checkpointList2.add(new Checkpoint(4, 90, "Good Job.", 3));
			JustInTimeEvaluator jitEval2 = new JustInTimeEvaluator(checkpointList2);
			Result result2 = jitEval2.evaluate();
			
			// Assert that the evaluation of both checkpoint lists are equal
			assertTrue(result1.equals(result2));
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		}
	}
	
	/**
	 * Tests Result extractStudentName method.
	 */
	@Test
	void testExtractStudentName() {
		
		// Create a result object and set the assignment name
		Result result = new Result();
		result.setAssignmentName("last_first_a1.docx");
		
		// Extract the student name from the assignment name
		result.extractStudentName();
		
		// Assert first name
		assertTrue("first".equals(result.getStudentFirstName()));
		
		// Assert last name
		assertTrue("last".equals(result.getStudentLastName()));
		
	}

}
