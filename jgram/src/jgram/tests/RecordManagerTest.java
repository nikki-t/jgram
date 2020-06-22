package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jgram.assessment.Checkpoint;
import jgram.assessment.GradeMapping;
import jgram.assessment.Result;
import jgram.exceptions.InvalidCheckpointException;
import jgram.storage.Assignment;
import jgram.storage.RecordManager;

public class RecordManagerTest {
	
	private Assignment createAssignment() throws InvalidCheckpointException {
				
		// Create Result
		// Checkpoints
		List<Checkpoint> checkpointList = new ArrayList<>();
		checkpointList.add(new Checkpoint(3, 85, "Okay job", 1));
		checkpointList.add(new Checkpoint(3, 90, "Good job", 2));
		checkpointList.add(new Checkpoint(4, 100, "Excellent job", 3));
		
		// Total Grade
		float totalGrade = (float) 92.5;
		
		// Hash String
		String hashString = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTkyNTI0MzA5LCJzdWIiOiJKR1JBTSIsImlzcyI6IkJVLU1FVCIsIjEtV2VpZ2h0IjozLCIxLUdyYWRlIjo4NSwiMS1GZWVkYmFjayI6Ik9rYXkgam9iIiwiMi1XZWlnaHQiOjMsIjItR3JhZGUiOjkwLCIyLUZlZWRiYWNrIjoiR29vZCBqb2IiLCIzLVdlaWdodCI6NCwiMy1HcmFkZSI6MTAwLCIzLUZlZWRiYWNrIjoiRXhjZWxsZW50IGpvYiIsIkNQSW5kZXhlcyI6IlsxLCAyLCAzXSIsIkdyYWRlTWFwcGluZyI6IkErID0gOTdcbkEgID0gOTVcbkEtID0gOTNcbkIrID0gODdcbkIgID0gODVcbkItID0gODNcbkMgID0gNzdcbkYgID0gNjdcbiIsIlRvdGFsR3JhZGUiOjkyLjV9.ABHM5z_0x8egMEYRCNBt-H4d8hxBZv_DaQowyrJo0w0";
		
		// Assignment Name
		String assignmentName = "last_first_a1.docx";
		
		Result result = new Result(checkpointList, totalGrade);
		result.setHashString(hashString);
		result.setAssignmentName(assignmentName);
		result.extractStudentName();
		
		// Create GradeMapping
		GradeMapping gm = new GradeMapping();
		gm.setDefaultGradeMapping();
		
		// Create Assignment
		Assignment assignment = new Assignment("testUser", 
				"Record Manager Test", "/jgram/test/");
		assignment.addResult(result);
		assignment.setGradeMapping(gm);
		
		return assignment;
		
	}
	
	/**
	 * Intent: Test the createPathHashMap method of the RecordManager class.
	 */
	@Test
	void testCreatePathHashMap() {
		
		// Write assignment data
		RecordManager rm = writeAssignmentData();
		
		// Create file map
		Path testPath = TestUtilities.returnPath("/record/last_first_a1.docx");
		Map<String, Path> fileMap = new HashMap<>();
		fileMap.put("last_first_a1.docx", testPath);
		
		// Retrieve hash string for assignment
		Map<Path, String> pathHashMap = new HashMap<>();
		try {
			rm.openConnection();
			pathHashMap = rm.createPathHashMap(fileMap);
			rm.closeConnection();
		} catch (SQLException e) {
			fail("Could not retrieve hash string");
		}
		
		// Assert hash string
		String hashString = pathHashMap.get(testPath);
		String expected = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTkyNTI0MzA5LCJzdWIiOiJKR1JBTSIsImlzcyI6IkJVLU1FVCIsIjEtV2VpZ2h0IjozLCIxLUdyYWRlIjo4NSwiMS1GZWVkYmFjayI6Ik9rYXkgam9iIiwiMi1XZWlnaHQiOjMsIjItR3JhZGUiOjkwLCIyLUZlZWRiYWNrIjoiR29vZCBqb2IiLCIzLVdlaWdodCI6NCwiMy1HcmFkZSI6MTAwLCIzLUZlZWRiYWNrIjoiRXhjZWxsZW50IGpvYiIsIkNQSW5kZXhlcyI6IlsxLCAyLCAzXSIsIkdyYWRlTWFwcGluZyI6IkErID0gOTdcbkEgID0gOTVcbkEtID0gOTNcbkIrID0gODdcbkIgID0gODVcbkItID0gODNcbkMgID0gNzdcbkYgID0gNjdcbiIsIlRvdGFsR3JhZGUiOjkyLjV9.ABHM5z_0x8egMEYRCNBt-H4d8hxBZv_DaQowyrJo0w0";
		assertTrue(expected.equals(hashString));
		
	}
	
	/**
	 * Intent; Test the selectAllStudents method of the RecordManager class.
	 */
	@Test
	void testSelectAllStudents() {
		
		// RecordManager
		RecordManager rm = new RecordManager();
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		rm.setURL(dbPath);
		
		// Select all students from assignment
		ArrayList<String[]> rows = new ArrayList<>();
		try {
			rm.openConnection();
			rows = rm.selectAllStudents("Assignment 1: Array Basics");
			rm.closeConnection();
		
		} catch (SQLException e1) {
			fail("Could not fetch assignment grading data for comparison.");
		}
		
		// Assert first row data
		assertEquals("testGrader", rows.get(0)[0]);
		assertEquals("Assignment 1: Array Basics", rows.get(0)[1]);
		assertEquals("olive", rows.get(0)[2]);
		assertEquals("griffs", rows.get(0)[3]);
		assertEquals("griffs_olive_a1.docx", rows.get(0)[4]);
		assertEquals("91.5", rows.get(0)[5]);
		assertEquals("3", rows.get(0)[6]);
		assertEquals("85", rows.get(0)[7]);
		assertEquals("Okay work.", rows.get(0)[8]);
		
	}
	
	/**
	 * Intent; Test the selectAssginmentStats method of the RecordManager class.
	 */
	@Test
	void testSelectAssignmentStats() {
		
		// RecordManager
		RecordManager rm = new RecordManager();
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		rm.setURL(dbPath);
		
		// Select all students from assignment
		ArrayList<String[]> rows = new ArrayList<>();
		try {
			rm.openConnection();
			rows = rm.selectAssignmentStats();
			rm.closeConnection();
		
		} catch (SQLException e1) {
			fail("Could not fetch assignment grading data for comparison.");
		}
		
		// Assert first row data
		assertEquals("Assignment 1: Array Basics", rows.get(0)[0]);
		assertEquals("3", rows.get(0)[1]);
		assertEquals("82.0", rows.get(0)[2]);
		assertEquals("94.2", rows.get(0)[3]);
		assertEquals("89.23", rows.get(0)[4]);
		
	}
	
	/**
	 * Intent; Test the selectStudentAssignments method of the RecordManager 
	 * class.
	 */
	@Test
	void testSelectStudentAssignments() {
		
		// RecordManager
		RecordManager rm = new RecordManager();
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		rm.setURL(dbPath);
		
		// Student name
		String[] name = {"rose", "griffs"};
		
		// Select all students from assignment
		ArrayList<String[]> rows = new ArrayList<>();
		try {
			rm.openConnection();
			rows = rm.selectStudentAssignments(name);
			rm.closeConnection();
		
		} catch (SQLException e1) {
			fail("Could not fetch assignment grading data for comparison.");
		}
		
		// Assert first row data
		assertEquals("testGrader", rows.get(0)[0]);
		assertEquals("Assignment 1: Array Basics", rows.get(0)[1]);
		assertEquals("rose", rows.get(0)[2]);
		assertEquals("griffs", rows.get(0)[3]);
		assertEquals("griffs_rose_a1.docx", rows.get(0)[4]);
		assertEquals("82.0", rows.get(0)[5]);
		assertEquals("3", rows.get(0)[6]);
		assertEquals("70", rows.get(0)[7]);
		assertEquals("Poor work.", rows.get(0)[8]);
		
	}
	
	/**
	 * Intent: Test writeAssignmentData method of RecordManager class.
	 */
	@Test
	void testWriteAssignmentData() {
		
		// Write assignment data
		RecordManager rm = writeAssignmentData();
		
		// Select all students from assignment
		ArrayList<String[]> rows = new ArrayList<>();
		try {
			rm.openConnection();
			rows = rm.selectAllStudents("Record Manager Test");
			rm.closeConnection();
		} catch (SQLException e) {
			fail("Could not fetch assignment grading data for comparison.");
		}
		
		// Assert first row data
		assertEquals("testUser", rows.get(0)[0]);
		assertEquals("Record Manager Test", rows.get(0)[1]);
		assertEquals("first", rows.get(0)[2]);
		assertEquals("last", rows.get(0)[3]);
		assertEquals("last_first_a1.docx", rows.get(0)[4]);
		assertEquals("92.5", rows.get(0)[5]);
		assertEquals("3", rows.get(0)[6]);
		assertEquals("85", rows.get(0)[7]);
		assertEquals("Okay job", rows.get(0)[8]);		
		
	}
	
	private RecordManager writeAssignmentData() {
		
		// Create an Assignment object with grading data
		Assignment assignment = null;
		try {
			 assignment = createAssignment();
		} catch (InvalidCheckpointException e) {
			fail("Could not create a checkpoint list");
		}
		
		// Create a RecordManager object
		RecordManager rm = new RecordManager(assignment);
		
		// Set DB URL
		String dbPath = "jdbc:sqlite:" 
				+ TestUtilities.returnPath("jgramTest.db").toString();
		rm.setURL(dbPath);
		
		// Write assignment data
		try {
			rm.openConnection();
			rm.writeAssignmentData();
			rm.closeConnection();
		} catch (SQLException e) {
			fail("Could not write assignment data");
		}
		
		return rm;
	}

}
