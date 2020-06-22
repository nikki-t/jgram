package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jgram.security.Secret;
import jgram.storage.Assignment;
import jgram.storage.RecordManager;
import jgram.task.EvaluationTask;
import jgram.task.TamperTask;
import jgram.task.Task;

public class TamperTaskTest {
	
	/**
	 * Intent run EvalTask so records are present in JGRAM test database.
	 * 
	 * @param resourceDir
	 */
	private void performEvalTask(Path resourceDir) {
		
		// Change System.in to point to input
			InputStream in = new ByteArrayInputStream(resourceDir.toString().getBytes());
			System.setIn(in);

			// Create new EvaluationTask object
			EvaluationTask evalTask = new EvaluationTask();
			
			// Mock createFileList and createAssignment
			evalTask = spy(EvaluationTask.class);
			try {
				doNothing().when(evalTask).prep();
			} catch (IOException e) {
				fail("Could not create mock of createFileList or "
						+ "retreiveAssignmentTitle method.");
			}
			
			// Set task secret
			Secret secret = new Secret("secret");
			evalTask.setSecret(secret);
			
			// Simulate output from Task createFileList method
			Path path1 = Paths.get(resourceDir.toString(), 
					"tamper-valid.docx");
			Path path2 = Paths.get(resourceDir.toString(),
					"tamper-invalid.docx");
			List<Path> fileList = new ArrayList<>();
			fileList.add(path1);
			fileList.add(path2);
			evalTask.setFileList(fileList);
			
			// Set Assignment
			Assignment assignment = new Assignment("testUser", 
					"Tamper Task Test", "/jgram/test/");
			evalTask.setAssignment(assignment);
			
			// Set Record Manager (including test database)
			RecordManager rm = new RecordManager(assignment);
			String dbPath = "jdbc:sqlite:" 
					+ TestUtilities.returnPath("jgramTest.db").toString();
			rm.setURL(dbPath);
			evalTask.setRecordManager(rm);

			// Perform task
			evalTask.performTask();
		
	}
	
	/**
	 * Intent: Perform Tamper Task to produce testable output.
	 * 
	 * @param resourceDir
	 * @return
	 */
	private TamperTask performTask(Path resourceDir) {
		
		// Create new TamperTask object
		Task task = new TamperTask();
		
		// Mock createFileList, getDirectory, and testTamperPath
		task = spy(TamperTask.class);
		try {
			doNothing().when(task).createFileList();
			doNothing().when(task).getDirectory();
			doNothing().when((TamperTask) task).testTamperPath();
		} catch (IOException e) {
			fail("Could not create mock of createFileList method.");
		}
		
		// Set working directory
		task.setWorkingDirectory(resourceDir);
		
		// Set task secret
		Secret secret = new Secret("secret");
		task.setSecret(secret);
		
		// Simulate output from Task createFileList method
		Path path1 = Paths.get(resourceDir.toString(), 
				"GRADED_tamper-valid.docx");
		Path path2 = Paths.get(resourceDir.toString(),
				"GRADED_tamper-invalid.docx");
		List<Path> fileList = new ArrayList<>();
		fileList.add(path1);
		fileList.add(path2);
		task.setFileList(fileList);
		
		// Set database URL
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		TamperTask tamperTask = (TamperTask) task;
		tamperTask.getRecordManager().setURL(dbPath);
		
		/// Perform task
		tamperTask.performTask();
		
		return tamperTask;
}
	
	/**
	 * Intent: Test TamperTaskTest.performTask method. Asserts that 'jgram.dat'
	 * exists, 'report.txt' exists, and that 2 threads were executed and
	 * terminated.
	 */
	@Test
	void testPerformTask() {
				
		// Locate test assignment file
		Path resourceDir = TestUtilities
				.returnAssignmentDir("tamper/GRADED/"
						+ "GRADED_tamper-task-test-valid.docx");
		
		// Perform eval task
		performEvalTask(resourceDir.getParent());
		
		// Perform tamper task
		TamperTask task = performTask(resourceDir);
		
		// Assert 'report.txt' exists
		String reportFileString = resourceDir.toString() + "/report.txt";
		File reportFile = new File(reportFileString);
		assertTrue(reportFile.exists());
		
		// Assert finished executing threads
		assertTrue(task.getExecutorService().isTerminated());
		
		// Assert expected thread count
		assertEquals(2, task.getThreadCount());
		
	}
}
