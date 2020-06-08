package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jgram.storage.Record;

public class RecordTest {
	
	/**
	 * Intent: Test Record object creation via toString method.
	 */
	@Test
	void testToString() {
		
		// Create Record
		Record record = new Record(1, "GRADED_assignment1.docx", 
				"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTkxMTI4MzM0LCJz");
		
		// Create expected String representation of a Record object
		String grader = System.getProperty("user.name");
		String expected = "ID: 1, Grader: " 
				+ grader 
				+ ", Assignment Name: GRADED_assignment1.docx,"
				+ " Hash: eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTkxMTI4"
				+ "MzM0LCJz";
		
		// Assert Record object equals expected string
		assertEquals(expected, record.toString());
		
	}

}
