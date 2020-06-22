package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;

import jgram.security.Secret;
import jgram.storage.Assignment;
import jgram.task.EvalTaskRun;

public class EvalTaskRunTest {
	
	
	/**
	 * Intent: Run task using appropriate input data for test comparison of 
	 * output.
	 * @param path
	 */
	private Assignment runTask(Path path) {
		
		// Create Assignment
		Assignment assignment = new Assignment("testUser", 
				"Eval Task Run Test", "/jgram/tests/");
		
		// Create EvalTaskRun
		Secret secret = new Secret("secret");
		EvalTaskRun evalTask = new EvalTaskRun(assignment, path, secret);
		
		// Run task
		evalTask.run();
		
		return assignment;

	}

	/**
	 * Intent: Test run method of EvalTaskRun class with invalid grading data.
	 * Assert that 'GRADED' sub-directory exists, graded invalid document
	 * does NOT exist, and 'jgram.dat' file exists. 
	 */
	@Test
	void testRuninValid() {
		
		// Locate test assignment file
		Path resourceDocument = TestUtilities
				.returnPath("eval/eval-task-test-invalid.docx");
		
		// Run task and get assignment output 
		Assignment assignment = runTask(resourceDocument);
		
		// Assert graded directory exists
		String gradedDirectory = resourceDocument.getParent().toString() 
				+ "/GRADED";
		Path gradedPath = Paths.get(gradedDirectory);
		assertTrue(Files.isDirectory(gradedPath));
		
		// Assert graded assignment does not exist
		String gradedAssignment = gradedDirectory 
				+ "/GRADED_eval-task-test-invalid.docx";
		File gradedFile = new File(gradedAssignment);
		assertTrue(!gradedFile.exists());
		
		// Assert Assignment result and grade mapping are null
		assertEquals(0, assignment.getResultList().size());
		assertEquals(null, assignment.getGradeMapping());
	}
	
	/**
	 * Intent: Test run method of EvalTaskRun class. Asserts that a 'GRADED'
	 * directory is created with a graded assignment copy called 
	 * 'GRADED_<assignment_name>', the graded assignment copy contains a
	 * results table, and a file named 'jgram.dat' exists.
	 */
	@Test
	void testRunValid() {
		
		// Locate test assignment file
		Path resourceDocument = TestUtilities
				.returnPath("eval/eval-task-test-valid.docx");
		
		// Run task and get assignment output 
		Assignment assignment = runTask(resourceDocument);
		
		// Assert graded directory exists
		String gradedDirectory = resourceDocument.getParent().toString() 
				+ "/GRADED";
		Path gradedPath = Paths.get(gradedDirectory);
		assertTrue(Files.isDirectory(gradedPath));
		
		// Assert graded assignment exists
		String gradedAssignment = gradedDirectory 
				+ "/GRADED_eval-task-test-valid.docx";
		File gradedFile = new File(gradedAssignment);
		assertTrue(gradedFile.exists());
		
		// Assert graded assignment has a results table
		try {
			
			// Get rows of last table
			List<XWPFTableRow> rows = TestUtilities
					.createRowsFromTable(gradedFile);
			
			// Assert content of first cell in table
			assertEquals("C#", rows.get(0).getCell(0).getText());
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		// Assert Assignment has a Result
		// Checkpoints
		int checkpointListSize = assignment
				.getResultList()
				.get(0)
				.getCheckpointList()
				.size();
		assertEquals(3, checkpointListSize);
		// Total grade
		float totalGrade = assignment.getResultList().get(0).getTotalGrade();
		assertEquals(92.5, totalGrade);
		
		// Assert Assignment has a Grade Mapping
		assertNotEquals(assignment.getGradeMapping(), null);
		assertEquals(8, assignment.getGradeMapping().getLimits().size());
		
	}

}
