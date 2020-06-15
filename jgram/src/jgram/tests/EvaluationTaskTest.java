package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jgram.security.Secret;
import jgram.task.EvaluationTask;
import jgram.task.Task;

public class EvaluationTaskTest {
	
	private EvaluationTask performTask(Path resourceDir) {
		
		// Change System.in to point to input
		InputStream in = new ByteArrayInputStream(resourceDir.toString().getBytes());
		System.setIn(in);

		// Create new EvaluationTask object
		Task task = new EvaluationTask();
		
		// Mock createFileList
		task = spy(EvaluationTask.class);
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
				"eval-task-test-valid.docx");
		Path path2 = Paths.get(resourceDir.toString(),
				"eval-task-test-invalid.docx");
		List<Path> fileList = new ArrayList<>();
		fileList.add(path1);
		fileList.add(path2);
		task.setFileList(fileList);

		// Perform task
		task.performTask();
		
		return (EvaluationTask) task;
		
	}
	
	/**
	 * Intent: Test EvaluationTask.performTask method. Asserts that a 'GRADED'
	 * directory is created with a graded assignment copy called 
	 * 'GRADED_<assignment_name>' and the graded assignment copy contains a
	 * results table.
	 */
	@Test
	void testPerformTask() {
		
		// Locate test assignment file
		Path resourceDir = TestUtilities
				.returnAssignmentDir("eval/eval-task-test-valid.docx");
		
		// Perform task
		EvaluationTask task = performTask(resourceDir);
		
		// Assert finished executing threads
		assertTrue(task.getExecutorService().isTerminated());
		
		// Assert expected thread count
		assertEquals(2, task.getThreadCount());
	}

}
