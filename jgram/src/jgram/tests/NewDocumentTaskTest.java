package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import jgram.task.NewDocumentTask;

public class NewDocumentTaskTest {
	
	private NewDocumentTask performTask(Path resourceDir) {
		
		// Change System.in to point to input
		InputStream in = new ByteArrayInputStream(resourceDir.toString().getBytes());
		System.setIn(in);
		
		// Create NewDocumentTask object
		NewDocumentTask task = new NewDocumentTask();
		
		// Mock createFileList
		task = spy(NewDocumentTask.class);
		try {
			doNothing().when(task).prep();
		} catch (IOException e) {
			fail("Could not create mock of createFileList method.");
		}
		
		// Simulate output from Task.createFileList method
		try {
			Stream<Path> pathStream = Files.list(resourceDir);
			List<Path> fileList = pathStream.collect(Collectors.toList());
			pathStream.close();
			task.setFileList(fileList);
			
		} catch (IOException e) {
			fail("Could not create file list.");
		}
		
		// Perform task
		task.performTask();
		
		return (NewDocumentTask) task;
	}
	
	/**
	 * Intent: Test the performTask method in the NewDocumenTask class on an
	 * invalid Word document (i.e. a Word document with grading data).
	 */
	@Test
	void testPerformTask() {
		
		// Locate test assignment file
		Path resourceDir = TestUtilities
				.returnAssignmentDir("newdoc/new-doc-test-invalid.docx");
		
		// Perform task
		NewDocumentTask task = performTask(resourceDir);
		
		// Assert finished executing threads
		assertTrue(task.getExecutorService().isTerminated());
		
		// Assert expected thread count
		assertEquals(2, task.getThreadCount());
		
	}

}
