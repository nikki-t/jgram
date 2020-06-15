package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import jgram.task.NewDocumentTask;
import jgram.task.Task;

public class TaskTest {

	/**
	 * Intent: Test the method createFileList of the Task class.
	 */
	@Test
	void testCreateFileList() {
		
		// Locate new doc sample file directory
		Path resourceDir = TestUtilities
				.returnAssignmentDir("newdoc/new-doc-test-invalid.docx");
		
		// Change System.in to point to input
		InputStream in = new ByteArrayInputStream(resourceDir.toString().getBytes());
		System.setIn(in);
		
		// Create a new concrete subclass Task
		Task task = new NewDocumentTask();
		
		// Get working directory
		Scanner keyboard = new Scanner(System.in);
		task.getDirectory(keyboard);
		
		// Run createFileList method
		try {
			task.createFileList();
		} catch (IOException e) {
			fail("Unable to obtain files in directory.");
		}
		
		// Assert file list size
		assertEquals(2, task.getFileList().size());
		
		// Assert first item in the list
		// Extract name
		int nameCount = task.getFileList().get(0).getNameCount();
		String name = task
				.getFileList()
				.get(0)
				.getName(nameCount - 1)
				.toString();
		assertEquals("new-doc-test-invalid.docx", name);		
		
	}
	
	/**
	 * Test the getDirectory method of the Task class.
	 */
	@Test
	void testGetDirectory() {
		
		// Locate new doc sample file directory
		Path resourceDir = TestUtilities
				.returnAssignmentDir("newdoc/new-doc-test-invalid.docx");
		
		// Change System.in to point to input
		InputStream in = new ByteArrayInputStream(resourceDir.toString().getBytes());
		System.setIn(in);
		
		// Create a new concrete subclass Task
		Task task = new NewDocumentTask();
		
		// Run getDirectory method
		Scanner keyboard = new Scanner(System.in);
		task.getDirectory(keyboard);
		
		// Store reference to directory obtained
		Path path = task.getWorkingDirectory();
		
		// Assert returned directory
		File pathFile = path.toFile();
		assertTrue(pathFile.exists());
		
		// Assert name of directory
		// Extract name
		int nameCount = path.getNameCount();
		String name = path
				.getName(nameCount - 1)
				.toString();
		assertEquals("newdoc", name);		
		
	}

}
