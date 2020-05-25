package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import jgram.task.TamperTestTask;
import jgram.task.Task;

public class TamperTaskTest {
	
	/**
	 * Intent: Test TamperTaskTest.performTask method. Asserts that output of
	 * report.txt file contains: Filename, Tamper Stauts, Grade Mapping, and 
	 * Result Table.
	 */
	@Test
	void testPerformTask() {
		
		// Locate test assignment file
		Path resourceDocument = TestUtilities
				.returnAssignmentPath("tamper-task-test.docx");
		
		// Run performTask
		Task task = new TamperTestTask();
		try {
			TestUtilities.runPerformTask(task, resourceDocument, "tamper");
		} catch (IOException e) {
			fail("Failed to mock Task.createFileList method.");
		}
		
		// Assert 'report.txt' exists
		String reportFileString = resourceDocument
				.getParent()
				.toString() 
				+ "/GRADED/report.txt";
		File reportFile = new File(reportFileString);
		
		assertTrue(reportFile.exists());
		
		// Assert 'report.txt' contains key report sections
		try {
			// Read entire contents of report.txt
			Path reportFilePath = reportFile.toPath();
			byte[] encoded = Files.readAllBytes(reportFilePath);
			String report = new String(encoded, StandardCharsets.UTF_8);
			
			// Assert key report sections exist in report.txt
			assertTrue(report.contains("\nFilename: tamper-task-test.docx\n"));
			assertTrue(report.contains("\nTamper Status: \n\tPASSED"));
			assertTrue(report.contains("\nGrade Mapping: \n"));
			assertTrue(report.contains("\nResult Table: \n"));
			
		} catch (IOException e) {
			fail("Could not read report.txt.");
		}
		
	}

}
