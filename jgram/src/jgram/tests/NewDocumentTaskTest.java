package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jgram.task.NewDocumentTask;

public class NewDocumentTaskTest {
	
	/**
	 * Intent: Test the performTask method in the NewDocumenTask class on an
	 * invalid Word document (i.e. a Word document with grading data).
	 */
	@Test
	void testPerformTaskInvalid() {
		
		// Locate test assignment file
		Path resourceDocument = TestUtilities
				.returnAssignmentPath("new-doc-test-invalid.docx");		
		
		// Create NewDocumentTask object
		NewDocumentTask task = new NewDocumentTask();
		
		// Mock createFileList
		task = spy(NewDocumentTask.class);
		try {
			doNothing().when(task).createFileList("new");
		} catch (IOException e) {
			fail("Could not create mock of createFileList method.");
		}
		
		// Simulate output from Task.createFileList method
		List<Path> fileList = new ArrayList<>();
		fileList.add(resourceDocument);
		task.setFileList(fileList);
		
		// Perform task
		task.performTask();
		
		// Expected message
		String expMessage = "\nFOUND checkpoint data in the following comments: "
			+ "\n\tComment #1"
			+ "\n\tComment #2"
			+ "\n\tComment #3"
			+ "\nFOUND grade mapping data.";
		
		// Assert task message
		assertEquals(expMessage, task.getMessage());

	}
	
	/**
	 * Intent: Test the performTask method in the NewDocumenTask class on a
	 * valid Word document (i.e. a Word document with no grading data).
	 */
	@Test
	void testPerformTaskValid() {
		
		// Locate test assignment file
		Path resourceDocument = TestUtilities
				.returnAssignmentPath("new-doc-test-valid.docx");		
		
		// Create NewDocumentTask object
		NewDocumentTask task = new NewDocumentTask();
		
		// Mock createFileList
		task = spy(NewDocumentTask.class);
		try {
			doNothing().when(task).createFileList("new");
		} catch (IOException e) {
			fail("Could not create mock of createFileLlist method.");
		}
		
		// Simulate output from Task.createFileList method
		List<Path> fileList = new ArrayList<>();
		fileList.add(resourceDocument);
		task.setFileList(fileList);
		
		// Perform task
		task.performTask();
		
		// Assert task message
		assertEquals("\nNo grading data detected.", task.getMessage());

	}

}
