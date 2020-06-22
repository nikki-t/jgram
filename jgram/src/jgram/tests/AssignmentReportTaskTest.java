package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import jgram.task.AssignmentReportTask;

public class AssignmentReportTaskTest {
	
	/**
	 * Intent: Mock AssignmentReportTask methods: prep, getReportSelection,
	 * getAssignmentName
	 * 
	 * @param aName
	 * @param selectionNum
	 * @return reportTask
	 */
	private AssignmentReportTask createMocks(String aName, String selectionNum) {
		
		// Create new AssignmentReportTask object
		AssignmentReportTask reportTask = new AssignmentReportTask();
		
		// Mock methods
		reportTask = spy(AssignmentReportTask.class);
		try {
			
			// prep
			doNothing().when(reportTask).prep();
			
			// report selection - return exit code on second invocation
			doAnswer(new Answer<Object>() {
				
				private int count = 0;
				
				public Object answer(InvocationOnMock invocation) {
					count++;
					if (count == 1) {
						return selectionNum;
					}
					return "4";
				}
				
			}).when(reportTask).getReportSelection();
			
			// assignment name
			doReturn(aName).when(reportTask).getAssignmentTitle();
		
		} catch (Exception e) {
			fail("Could not create mock of prep method.");
		}
		
		return reportTask;

	}
	
	/**
	 * Intent: Test performTask method of AssignmentReportTask class. The report
	 * selection is set to 1 to retrieve all student grades from one assignment.
	 */
	@Test
	void testPerformTaskAssignment() {
		
		// Create new AssignmentReportTask object
		AssignmentReportTask reportTask = createMocks(
				"Assignment 2: Loop Basics", "1");
		
		// Set working directory
		Path resourceDir = TestUtilities.returnAssignmentDir("report/test.txt");
		reportTask.setWorkingDirectory(resourceDir);
		
		// Set database URL
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		reportTask.getRecordManager().setURL(dbPath);
		
		// Run task
		reportTask.performTask();
		
		// Assert report exists
		String reportFileString = resourceDir.toString() 
				+ "/REPORT_Assignment 2: Loop Basics.txt";
		File reportFile = new File(reportFileString);
		assertTrue(reportFile.exists());
		
		// Report contents
		try {
			Path reportFilePath = reportFile.toPath();
			byte[] encoded;
			encoded = Files.readAllBytes(reportFilePath);
			String report = new String(encoded, StandardCharsets.UTF_8);
		
			// Assert report contains a title
			assertTrue(report.contains("Assignment 2: Loop Basics Assignment Report"));
			
			// Assert columns
			assertTrue(report.contains("Grader"));
			assertTrue(report.contains("Assignment Title"));
			assertTrue(report.contains("Student First"));
			assertTrue(report.contains("Student Last"));
			assertTrue(report.contains("Assignment Name"));
			assertTrue(report.contains("Total Grade"));
			assertTrue(report.contains("Weight"));
			assertTrue(report.contains("Grade"));
			assertTrue(report.contains("Feedback"));
			
		} catch (IOException e) {
			fail("Could not read report file.");
		}
	}
	
	/**
	 * Intent: Test performTask method of AssignmentReportTask class. The report
	 * selection is set to 3 to retrieve all assignment stats.
	 */
	@Test
	void testPerformTaskStats() {
		
		// Create new AssignmentReportTask object
		AssignmentReportTask reportTask = createMocks("", "3");
		
		// Set working directory
		Path resourceDir = TestUtilities.returnAssignmentDir("report/test.txt");
		reportTask.setWorkingDirectory(resourceDir);
		
		// Set database URL
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		reportTask.getRecordManager().setURL(dbPath);
		
		// Run task
		reportTask.performTask();
		
		// Assert report exists
		String reportFileString = resourceDir.toString() 
				+ "/REPORT_assignment_stats.txt";
		File reportFile = new File(reportFileString);
		assertTrue(reportFile.exists());
		
		// Report contents
		try {
			Path reportFilePath = reportFile.toPath();
			byte[] encoded;
			encoded = Files.readAllBytes(reportFilePath);
			String report = new String(encoded, StandardCharsets.UTF_8);
		
			// Assert report contains a title
			assertTrue(report.contains("Assignment Stats Report"));
			
			// Assert columns
			assertTrue(report.contains("Assignment Title"));
			assertTrue(report.contains("Count"));
			assertTrue(report.contains("Minimum"));
			assertTrue(report.contains("Maximum"));
			assertTrue(report.contains("Average"));
			
		} catch (IOException e) {
			fail("Could not read report file.");
		}
	}
	
	/**
	 * Intent: Test performTask method of AssignmentReportTask class. The report
	 * selection is set to 2 to retrieve all assignment data for one student.
	 */
	@Test
	void testPerformTaskStudent() {
		
		// Create new AssignmentReportTask object
		AssignmentReportTask reportTask = createMocks(
				"Assignment 2: Loop Basics", "2");
		
		// Mock student name
		String[] name = {"olive", "griffs"};
		doReturn(name).when(reportTask).getStudentName();
		
		// Set working directory
		Path resourceDir = TestUtilities.returnAssignmentDir("report/test.txt");
		reportTask.setWorkingDirectory(resourceDir);
		
		// Set database URL
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		reportTask.getRecordManager().setURL(dbPath);
		
		// Run task
		reportTask.performTask();
		
		// Assert report exists
		String reportFileString = resourceDir.toString() 
				+ "/REPORT_griffs_olive.txt";
		File reportFile = new File(reportFileString);
		assertTrue(reportFile.exists());
		
		// Report contents
		try {
			Path reportFilePath = reportFile.toPath();
			byte[] encoded;
			encoded = Files.readAllBytes(reportFilePath);
			String report = new String(encoded, StandardCharsets.UTF_8);
		
			// Assert report contains a title
			assertTrue(report.contains("olive griffs Assignment Report"));
			
			// Assert columns
			assertTrue(report.contains("Grader"));
			assertTrue(report.contains("Assignment Title"));
			assertTrue(report.contains("Student First"));
			assertTrue(report.contains("Student Last"));
			assertTrue(report.contains("Assignment Name"));
			assertTrue(report.contains("Total Grade"));
			assertTrue(report.contains("Weight"));
			assertTrue(report.contains("Grade"));
			assertTrue(report.contains("Feedback"));
			
		} catch (IOException e) {
			fail("Could not read report file.");
		}
	}

}
