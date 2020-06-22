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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jgram.security.Secret;
import jgram.storage.Assignment;
import jgram.storage.RecordManager;
import jgram.task.EvaluationTask;

public class EvaluationTaskTest {
	
	private EvaluationTask performTask(Path resourceDir) {
		
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
				"eval-task-test-valid.docx");
		Path path2 = Paths.get(resourceDir.toString(),
				"eval-task-test-invalid.docx");
		List<Path> fileList = new ArrayList<>();
		fileList.add(path1);
		fileList.add(path2);
		evalTask.setFileList(fileList);
		
		// Set Assignment
		Assignment assignment = new Assignment("testUser", "Eval Task Test",
				"/jgram/test/");
		evalTask.setAssignment(assignment);
		
		// Set Record Manager (including test database)
		RecordManager rm = new RecordManager(assignment);
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		rm.setURL(dbPath);
		evalTask.setRecordManager(rm);

		// Perform task
		evalTask.performTask();
		
		return evalTask;
		
	}
	
	/**
	 * Intent: Retrieve result rows of evaluation task test.
	 * 
	 * @return
	 */
	private ArrayList<String[]> retrieveRows() {
		
		// RecordManager
		RecordManager rm = new RecordManager();
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		rm.setURL(dbPath);
		
		// Select all students from assignment
		ArrayList<String[]> rows = new ArrayList<>();
		try {
			rm.openConnection();
			rows = rm.selectAllStudents("Eval Task Test");
			rm.closeConnection();
		
		} catch (SQLException e1) {
			fail("Could not fetch assignment grading data for comparison.");
		}
		
		return rows;
		
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
		
		// Retrieve rows
		ArrayList<String[]> rows = retrieveRows();
		
		// Assert JGRAM database data
		assertEquals("testUser", rows.get(0)[0]);
		assertEquals("Eval Task Test", rows.get(0)[1]);
		assertEquals("eval-task-test-valid.docx", rows.get(0)[2]);
		assertEquals("eval-task-test-valid.docx", rows.get(0)[3]);
		assertEquals("eval-task-test-valid.docx", rows.get(0)[4]);
		assertEquals("92.5", rows.get(0)[5]);
		assertEquals("3", rows.get(0)[6]);
		assertEquals("85", rows.get(0)[7]);
		assertEquals("Okay job", rows.get(0)[8]);
		
	}

}
