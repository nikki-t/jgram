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
import jgram.task.TamperTask;
import jgram.task.Task;

public class TamperTaskTest {
	
	private TamperTask performTask(Path resourceDir) {
		
		// Change System.in to point to input
		InputStream in = new ByteArrayInputStream(resourceDir
				.getParent()
				.toString()
				.getBytes());
		System.setIn(in);
		
		// Create new TamperTask object
		Task task = new TamperTask();
		
		// Mock createFileList
		task = spy(TamperTask.class);
		try {
			doNothing().when(task).createFileList();
		} catch (IOException e) {
			fail("Could not create mock of createFileList method.");
		}
		
		// Set task secret
		Secret secret = new Secret("secret");
		task.setSecret(secret);
		
		// Simulate output from Task createFileList method
		Path path1 = Paths.get(resourceDir.toString(), 
				"GRADED_tamper-task-test-valid.docx");
		Path path2 = Paths.get(resourceDir.toString(),
				"GRADED_tamper-task-test-invalid.docx");
		List<Path> fileList = new ArrayList<>();
		fileList.add(path1);
		fileList.add(path2);
		task.setFileList(fileList);
		
		/// Perform task
		task.performTask();
		
		return (TamperTask) task;
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
		
		// Perform task
		TamperTask task = performTask(resourceDir);
		
		// Assert dat file exists
		Path datPath = Paths.get(resourceDir.toString(), "jgram.dat");
		File datFile = datPath.toFile();
		assertTrue(datFile.exists());
		
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
