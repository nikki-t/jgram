package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import jgram.storage.Record;
import jgram.storage.RecordManager;

public class RecordManagerTest {
	
	/**
	 * Intent: test createInputStream method of RecordManager class.
	 */
	@Test
	void testCreateInputStream() {
		
		// Locate test assignment file
		Path directory = TestUtilities
				.returnAssignmentPath("eval-task-test.docx")
				.getParent();
		
		// Create a ResourceManager object to test
		RecordManager manager = new RecordManager(directory);
		
		// Create an input stream
		try {
			manager.createInputStream();
		} catch (IOException e) {
			fail("Failed to create input stream.");
		}
		
		// Assert input stream has been created
		assertTrue(manager.getInputStream() instanceof ObjectInputStream);
		
	}
	
	/**
	 * Intent: test createOutputStream method of RecordManager class.
	 */
	@Test
	void testCreateOutputStream() {
		
		// Locate test assignment file
		Path directory = TestUtilities
				.returnAssignmentPath("eval-task-test.docx")
				.getParent();
		
		// Create a RecordManager object to test
		RecordManager manager = new RecordManager(directory);
		
		// Create an input stream
		try {
			manager.createOutputStream();
		} catch (IOException e) {
			fail("Failed to create input stream.");
		}
		
		// Assert input stream has been created
		assertTrue(manager.getOutputStream() instanceof ObjectOutputStream);
		
	}
	
	/**
	 * Intent: Test createResultList method of RecordManager class.
	 */
	@Test
	void testCreateRecordListFromFile() {
		// Locate test assignment file
		Path directory = TestUtilities
				.returnAssignmentPath("eval-task-test.docx")
				.getParent();
		
		// Write output to dat file to ensure can test input
		RecordManager outputManager = new RecordManager(directory);
		
		try {
			outputManager.createOutputStream();
			outputManager.writeRecord(1, "GRADED_assignment1.docx", 
					"eyJhbGciOiJIUzI1NiJ9");
			outputManager.writeRecord(2, "GRADED_assignment2.docx", 
					"fyJhbGciOiJIUzI1NiJ9");
			outputManager.writeRecord(3, "GRADED_assignment3.docx", 
					"gyJhbGciOiJIUzI1NiJ9.");

		} catch (IOException e) {
			fail("Could not write .dat file.");
		} 
		
		// Create a ResourceManager object to test for input
		RecordManager inputManager = new RecordManager(directory);
		
		// Create record list
		try {
			inputManager.createInputStream();
			inputManager.createRecordListFromFile();
		
		} catch (ClassNotFoundException | IOException e) {
			fail("Could not create a record list.");
		}
		
		// Assert size of record list
		assertEquals(3, inputManager.getRecordList().size());
		
		// Create expected String representation of a Record object
		String grader = System.getProperty("user.name");
		String expected = "ID: 1, Grader: " 
				+ grader 
				+ ", Assignment Name: GRADED_assignment1.docx,"
				+ " Hash: eyJhbGciOiJIUzI1NiJ9";
		
		// Assert expected string equals first record in the record list
		assertTrue(expected.equals(inputManager
				.getRecordList()
				.get(0)
				.toString()));
		
	}
	
	/**
	 * Intent: Test retrieveRecord method of RecordManager class.
	 */
	@Test
	void testRetrieveRecord() {
		// Locate test assignment file
		Path directory = TestUtilities
				.returnAssignmentPath("eval-task-test.docx")
				.getParent();
		
		// Write output to dat file to ensure test input
		RecordManager outputManager = new RecordManager(directory);
		
		try {
			outputManager.createOutputStream();
			outputManager.writeRecord(1, "GRADED_assignment1.docx", 
					"eyJhbGciOiJIUzI1NiJ9");
			outputManager.writeRecord(2, "GRADED_assignment2.docx", 
					"fyJhbGciOiJIUzI1NiJ9");
			outputManager.writeRecord(3, "GRADED_assignment3.docx", 
					"gyJhbGciOiJIUzI1NiJ9.");

		} catch (IOException e) {
			fail("Could not write .dat file.");
		} 
		
		// Create a ResourceManager object to test for input
		RecordManager inputManager = new RecordManager(directory);
		
		// Create record list and attempt to retrieve a record from it
		Record record = null;
		try {
			inputManager.createInputStream();
			inputManager.createRecordListFromFile();
			record = inputManager.retrieveRecord("GRADED_assignment2.docx");
		
		} catch (ClassNotFoundException | IOException e) {
			fail("Could not create a record list.");
		}
		
		// Create expected String representation of a Record object
		String grader = System.getProperty("user.name");
		String expected = "ID: 2, Grader: " 
				+ grader 
				+ ", Assignment Name: GRADED_assignment2.docx,"
				+ " Hash: fyJhbGciOiJIUzI1NiJ9";
		
		// Assert expected String equals Record string
		assertTrue(expected.equals(record.toString()));
		
	}
	
	@Test
	void testWriteRecord() {
		
		// Locate test assignment file
		Path directory = TestUtilities
				.returnAssignmentPath("eval-task-test.docx")
				.getParent();
		
		// Create a RecordManager object to test
		RecordManager manager = new RecordManager(directory);
		
		// Write a Record object
		try {
			manager.writeRecord(2, "GRADED_assignment2.docx", 
					"fyJhbGciOiJIUzI1NiJ9");
		} catch (IOException e) {
			fail("Could not write to .dat file");
		}
		
		// Create a representation of the .dat file
		Path datFilePath = Paths.get(directory.toString(), "jgram.dat");
		File datFile = new File (datFilePath.toString());
		
		// Assert dat file exists
		assertTrue(datFile.exists());
		
	}

}
