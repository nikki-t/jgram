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

import org.junit.jupiter.api.Test;

import jgram.security.Secret;
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
	 * Intent: Run task using appropriate input data for test comparison of 
	 * output.
	 * @param resourceDocument
	 */
	private void runTask(Path resourceDocument) {
		
		// Create PrintWriter
		PrintWriter outStream = createPrintWriter(resourceDocument);
		
		// Hash String
		String hashString = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTkyNTI3MTczLCJzdWIiOiJKR1JBTSIsImlzcyI6IkJVLU1FVCIsIjEtV2VpZ2h0Ijo3LCIxLUdyYWRlIjo5MCwiMS1GZWVkYmFjayI6IiIsIjItV2VpZ2h0Ijo1LCIyLUdyYWRlIjo5NywiMi1GZWVkYmFjayI6ImdyZWF0IGNsYXJpdHkiLCIzLVdlaWdodCI6NywiMy1HcmFkZSI6OTUsIjMtRmVlZGJhY2siOiJVc2UgZ2VuZXJpY3MsIGJ1dCBvdmVyYWxsIGdvb2Qgd29yayIsIkNQSW5kZXhlcyI6IlsxLCAyLCAzXSIsIkdyYWRlTWFwcGluZyI6IkErID0gOTdcbkEgID0gOTVcbkEtID0gOTNcbkIrID0gODdcbkIgID0gODVcbkItID0gODNcbkMgID0gNzdcbkYgID0gNjdcbiIsIlRvdGFsR3JhZGUiOjkzLjY4NDIxfQ.zfQhS04Itl3WzTvCv_uVOhJI8v68nsK6jP-4QIi5Wrs";
		
		// Create Secret
		Secret secret = new Secret("secret");
		
		// Create new task run
		TamperTaskRun tamperTaskRun = new TamperTaskRun(outStream, hashString,
				resourceDocument, secret);
		
		// Run task
		tamperTaskRun.run();
		
		// Close resources
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
				.returnPath("tamperRun/GRADED/GRADED_tamper-invalid.docx");
		
		runTask(resourceDocument);
		
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
					+ "GRADED_tamper-invalid.docx\n"));
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
				.returnPath("tamperRun/GRADED/GRADED_tamper-valid.docx");
		
		runTask(resourceDocument);
		
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
					+ "GRADED_tamper-valid.docx\n"));
			assertTrue(report.contains("\nTamper Status: \n\tPASSED"));
			assertTrue(report.contains("\nGrade Mapping: \n"));
			assertTrue(report.contains("\nResult Table: \n"));
			
		} catch (IOException e) {
			fail("Could not read report.txt.");
		}
		
	}


}
