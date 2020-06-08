package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import jgram.task.EvaluationTask;
import jgram.task.Task;

public class EvaluationTaskTest {
	
	/**
	 * Intent: Test EvaluationTask.performTask method. Asserts that a 'GRADED'
	 * directory is created with a graded assignment copy called 
	 * 'GRADED_<assignment_name>' and the graded assignment copy contains a
	 * results table.
	 */
	@Test
	void testPerformTask() {
		
		// Locate test assignment file
		Path resourceDocument = TestUtilities
				.returnAssignmentPath("eval-task-test.docx");
		
		// Run performTask method
		Task task = new EvaluationTask();
		try {
			TestUtilities.runPerformTask(task, resourceDocument, "evaluation");
		} catch (IOException e) {
			fail("Failed to mock Task.createFileList method.");
		}
		
		// Assert graded directory exists
		String gradedDirectory = resourceDocument.getParent().toString() + "/GRADED";
		Path gradedPath = Paths.get(gradedDirectory);
		
		assertTrue(Files.isDirectory(gradedPath));
		
		// Assert graded assignment exists
		String gradedAssignment = gradedDirectory + "/GRADED_eval-task-test.docx";
		File gradedFile = new File(gradedAssignment);
		
		assertTrue(gradedFile.exists());
		
		// Assert graded assignment has a results table
		try {
			
			// Get rows of last table
			List<XWPFTableRow> rows = TestUtilities.createRowsFromTable(gradedFile);
			
			// Assert content of first cell in table
			assertEquals("C#", rows.get(0).getCell(0).getText());
			
		} catch (IOException e) {
			
			fail("Error retrieving result to test for table.");
		}
		
		// Assert dat file exists
		Path datPath = Paths.get(gradedPath.toString(), "jgram.dat");
		File datFile = datPath.toFile();
		assertTrue(datFile.exists());
	}

}
