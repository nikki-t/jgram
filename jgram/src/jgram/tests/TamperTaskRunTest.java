package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import jgram.security.Secret;
import jgram.storage.RecordManager;
import jgram.task.TamperTaskRun;

public class TamperTaskRunTest {
	
	/**
	 * Intent: Create PrintWriter object.
	 * @param resourceDocument
	 * @return
	 */
	private PrintWriter createPrintWriter(Path resourceDocument) {
		// Create PrintWriter
		String reportFilename = resourceDocument.getParent().toString() 
				+ "/report.txt";
		PrintWriter outStream = null;
		try {
			outStream = new PrintWriter(reportFilename);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		}
		
		return outStream;
	}
		
	/**
	 * Intent: Create RecordManager object.
	 * @param resourceDocument
	 * @return
	 */
	private RecordManager createRecordManager(Path resourceDocument) {
		
		RecordManager recordManager = new RecordManager(resourceDocument
				.getParent());
		try {
			recordManager.createInputStream();
			recordManager.createRecordListFromFile();
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getMessage());
		}
		
		return recordManager;
		
	}
	
	/**
	 * Intent: Run task using appropriate input data for test comparison of 
	 * output.
	 * @param resourceDocument
	 */
	private void runTask(Path resourceDocument) {
		
		// Create PrintWriter
		PrintWriter outStream = createPrintWriter(resourceDocument);
		
		// Create RecordManager
		RecordManager recordManager = createRecordManager(resourceDocument);
		
		// Create Secret
		Secret secret = new Secret("secret");
		
		// Create new task run
		TamperTaskRun taskRun = new TamperTaskRun(outStream, resourceDocument, 
				recordManager, secret);
		
		// Run task
		taskRun.run();
		
		try {
			recordManager.getInputStream().close();
		} catch (IOException e) {
			fail(e.getMessage());
		}
		outStream.close();
	}
	
	/**
	 * Intent: Test run method of TamperTaskRun class. Asserts that output of
	 * report.txt file contains: Filename, Tamper Status, Grade Mapping, and 
	 * Result Table for modified graded file.
	 */
	@Test
	void testRunInvalid() {
		
		// Locate graded test assignment file
		Path resourceDocument = TestUtilities
				.returnAssignmentPath("tamper/GRADED/GRADED_"
						+ "tamper-task-test-invalid.docx");
		
		runTask(resourceDocument);
		
		// Assert dat file exists
		Path datPath = Paths.get(resourceDocument.getParent().toString(), 
				"jgram.dat");
		File datFile = datPath.toFile();
		assertTrue(datFile.exists());
		
		// Assert 'report.txt' exists
		String reportFileString = resourceDocument
				.getParent()
				.toString() 
				+ "/report.txt";
		File reportFile = new File(reportFileString);
		
		assertTrue(reportFile.exists());
		
		// Assert 'report.txt' contains key report sections
		try {
			// Read entire contents of report.txt
			Path reportFilePath = reportFile.toPath();
			byte[] encoded = Files.readAllBytes(reportFilePath);
			String report = new String(encoded, StandardCharsets.UTF_8);
			
			// Assert key report sections exist in report.txt
			assertTrue(report.contains("\nFilename: "
					+ "GRADED_tamper-task-test-invalid.docx\n"));
			assertTrue(report.contains("\nTamper Status: \n\tFAILED"));
			assertTrue(report.contains("\nCurrent Grade Mapping: \n"));
			assertTrue(report.contains("\nPrevious Result Table: \n"));
			assertTrue(report.contains("\nCurrent Result Table: \n"));
			
		} catch (IOException e) {
			fail("Could not read report.txt.");
		}
		
	}
	
	/**
	 * Intent: Test run method of TamperTaskRun class. Asserts that output of
	 * report.txt file contains: Filename, Tamper Status, Grade Mapping, and 
	 * Result Table for an unmodified graded file.
	 */
	@Test
	void testRunValid() {
		
		// Locate graded test assignment file
		Path resourceDocument = TestUtilities
				.returnAssignmentPath("tamper/GRADED/GRADED_"
						+ "tamper-task-test-valid.docx");
		
		runTask(resourceDocument);
		
		// Assert dat file exists
		Path datPath = Paths.get(resourceDocument.getParent().toString(), 
				"jgram.dat");
		File datFile = datPath.toFile();
		assertTrue(datFile.exists());
		
		// Assert 'report.txt' exists
		String reportFileString = resourceDocument
				.getParent()
				.toString() 
				+ "/report.txt";
		File reportFile = new File(reportFileString);
		
		assertTrue(reportFile.exists());
		
		// Assert 'report.txt' contains key report sections
		try {
			// Read entire contents of report.txt
			Path reportFilePath = reportFile.toPath();
			byte[] encoded = Files.readAllBytes(reportFilePath);
			String report = new String(encoded, StandardCharsets.UTF_8);
			
			// Assert key report sections exist in report.txt
			assertTrue(report.contains("\nFilename: "
					+ "GRADED_tamper-task-test-valid.docx\n"));
			assertTrue(report.contains("\nTamper Status: \n\tPASSED"));
			assertTrue(report.contains("\nGrade Mapping: \n"));
			assertTrue(report.contains("\nResult Table: \n"));
			
		} catch (IOException e) {
			fail("Could not read report.txt.");
		}
		
	}


}
