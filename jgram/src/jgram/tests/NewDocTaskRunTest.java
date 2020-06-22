package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import jgram.task.NewDocTaskRun;

public class NewDocTaskRunTest {

	/**
	 * Intent: Test the run method in the NewDocTaskRun class on an
	 * invalid Word document (i.e. a Word document with grading data).
	 */
	@Test
	void testRunInvalid() {
		
		// Locate test assignment file
		Path resourceDocument = TestUtilities
				.returnPath("newdoc/new-doc-test-invalid.docx");		
		
		// Create NewDocumentTask object
		NewDocTaskRun taskRun = new NewDocTaskRun(resourceDocument);
		
		// Execute run method
		taskRun.run();
		
		// Expected message
		String expMessage = "\nFOUND checkpoint data in the following comments: "
			+ "\n\tComment #1"
			+ "\n\tComment #2"
			+ "\n\tComment #3"
			+ "\nFOUND grade mapping data.";
		
		// Assert task message
		assertEquals(expMessage, taskRun.getMessage());

	}
	
	/**
	 * Intent: Test the run method in the NewDocTaskRun class on a
	 * valid Word document (i.e. a Word document with no grading data).
	 */
	@Test
	void testRunValid() {
		
		// Locate test assignment file
		Path resourceDocument = TestUtilities
				.returnPath("newdoc/new-doc-test-valid.docx");		
		
		/// Create NewDocumentTask object
		NewDocTaskRun taskRun = new NewDocTaskRun(resourceDocument);
		
		// Execute run method
		taskRun.run();
		
		// Assert task message
		assertEquals("\nNo grading data detected.", taskRun.getMessage());

	}

}
