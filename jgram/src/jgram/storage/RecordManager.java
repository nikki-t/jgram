package jgram.storage;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jgram.assessment.Checkpoint;
import jgram.assessment.GradeMapping;
import jgram.assessment.Result;

/**
 * Intent: To manage Record objects.
 * 
 * Postcondition1 (Record written)
 * Postcondition3 (Record retrieved)
 */
public class RecordManager {
	
	// Class variable(s)
	// Inserts
	private static final String ASSIGNMENT_INSERT = "INSERT INTO Assignment "
			+ "(grader, assignment_title, location) VALUES (?, ?, ?);";
	private static final String RESULT_INSERT = "INSERT INTO Result "
			+ "(student_fname, student_lname, assignment_name, total_grade, "
			+ "hash_string, a_id) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String GM_INSERT = "INSERT INTO GradeMapping (letter, "
			+ "num, a_id) VALUES (?, ?, ?);";
	private static final String CP_INSERT = "INSERT INTO Checkpoint (weight, "
			+ "grade, feedback, r_id) VALUES (?, ?, ?, ?)";
	
	// Selects
	private static final String ASSIGNMENT_ID = "SELECT assignment_id FROM "
			+ "Assignment WHERE assignment_title = ?;";
	private static final String RESULT_ID = "SELECT result_id FROM "
			+ "Result WHERE assignment_name = ?;";
	private static final String RESULT_LIST = "SELECT result_id FROM Result "
			+ "WHERE a_id = ?;";
	private static final String HASH_STRING = "Select hash_string FROM Result "
			+ "WHERE assignment_name = ?;";
	private static final String ALL_STUDENTS = "SELECT Assignment.grader, Assignment.assignment_title, Result.student_fname, Result.student_lname, Result.assignment_name, Result.total_grade, Checkpoint.weight, Checkpoint.grade, Checkpoint.feedback FROM Result JOIN Assignment ON Result.a_id = Assignment.assignment_id JOIN Checkpoint ON Result.result_id = Checkpoint.r_id WHERE assignment_id = ? ORDER BY Result.student_lname, Result.student_fname;";
	private static final String STUDENT_ASSIGNMENTS = "SELECT Assignment.grader, Assignment.assignment_title, Result.student_fname, Result.student_lname, Result.assignment_name, Result.total_grade, Checkpoint.weight, Checkpoint.grade, Checkpoint.feedback FROM Result JOIN Assignment ON Result.a_id = Assignment.assignment_id JOIN Checkpoint ON Result.result_id = Checkpoint.r_id WHERE Result.student_fname = ? AND Result.student_lname = ? ORDER BY Assignment.assignment_id;";
	private static final String ASSIGNMENTS_STATS = "SELECT Assignment.assignment_title, count(*) as Count, ROUND(min(Result.total_grade), 2) as Minimum, ROUND(max(Result.total_grade), 2) as Maximum, ROUND(avg(Result.total_grade), 2) as Average FROM Result JOIN Assignment ON Result.a_id = Assignment.assignment_id GROUP BY Assignment.assignment_id;";
	
	// Deletes
	private static final String DELETE_CHECKPOINTS = "DELETE FROM Checkpoint "
			+ "WHERE r_id = ?;";
	private static final String DELETE_RESULT = "DELETE FROM Result WHERE a_id "
			+ "= ?;";
	private static final String DELETE_GRADEMAPPING = "DELETE FROM "
			+ "GradeMapping WHERE a_id = ?;";
	private static final String DELETE_ASSIGNMENT = "DELETE FROM Assignment "
			+ "WHERE assignment_id = ?;";

	
	// Instance variable(s)
	private Assignment assignment;
	private int assignmentID;
	private Connection connection;
	private String URL = "jdbc:sqlite:" + getDBFile();
	
	public RecordManager() {}
	
	public RecordManager(Assignment inputAssignment) {
		assignment = inputAssignment;
	}
	
	/**
	 * Intent: Close a connection to the JGRAM database.
	 * 
	 * Postcondition1 (Database connection): A connection to the JGRAM database
	 * is closed.
	 * @throws SQLException 
	 */
	public void closeConnection() throws SQLException {
		connection.close();
	}
	
	/**
	 * Intent: Return a list of result record ids.
	 * 
	 * Postcondition1 (Create query): A prepared statement is created with 
	 * the assignment id as the first input parameter.
	 * Poscondition2 (Execute query): The query to obtain the 
	 * result ids is executed and the result is returned.
	 * Postcondition3 (Create and return list): A list is created from the 
	 * query results and contains all result ids.
	 * 
	 * @return
	 * @throws SQLException
	 */
	private ArrayList<Integer> createResultList() throws SQLException {
		
		// Post1 Create query
		PreparedStatement preparedStatement = connection
				.prepareStatement(RESULT_LIST);
		// Assignment id
		preparedStatement.setInt(1, assignmentID);
		
		// Post2 Execute query
		ResultSet results = preparedStatement.executeQuery();
		
		// Post3 Create and return list
		ArrayList<Integer> resultList = new ArrayList<>();
		while (results.next()) {
			resultList.add(results.getInt(1));
		}
		
		// Close resources
		results.close();
		preparedStatement.close();
		
		return resultList;
		
	}
	
	/**
	 * Intent: Delete data in a table according to an id value.
	 * 
	 * Postcondition1 (Create query): A query is created that deletes rows from 
	 * a table based on the id parameter.
	 * Postcondition2 (Execute query): The query is executed and rows are 
	 * deleted from the table.
	 * 
	 * @param sql
	 * @param id
	 * @throws SQLException
	 */
	private void deleteData(String sql, int id) throws SQLException {
		
		// Post1 Create query
		PreparedStatement preparedStatement = connection
				.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		
		// Post2 Execute query
		preparedStatement.executeUpdate();
		
		// Close resources
		preparedStatement.close();
	}
	
	/**
	 * Intent: Create a String array that represents a row in the ResultSet.
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	private String[] createStudentRow(ResultSet results) 
			throws SQLException {
		
		// Column count
		int columnCount = results.getMetaData().getColumnCount();

		// Create row
		String[] row = new String[columnCount];
		row[0] = results.getString("grader");
		row[1] = results.getString("assignment_title");
		row[2] = results.getString("student_fname");
		row[3] = results.getString("student_lname");
		row[4] = results.getString("assignment_name");
		row[5] = Float.toString(results.getFloat("total_grade"));
		row[6] = Integer.toString(results.getInt("weight"));
		row[7] = Integer.toString(results.getInt("grade"));
		row[8] = results.getString("feedback");

		return row;
	}
	
	/**
	 * Intent: Create a String array that represents a row in the ResultSet.
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	private String[] createStatRow(ResultSet results) throws SQLException {
		
		// Column count
		int columnCount = results.getMetaData().getColumnCount();
		
		// Create row
		String[] row = new String[columnCount];
		row[0] = results.getString("assignment_title");
		row[1] = Integer.toString(results.getInt("count"));
		row[2] = Float.toString(results.getFloat("minimum"));
		row[3] = Float.toString(results.getFloat("maximum"));
		row[4] = Float.toString(results.getFloat("average"));
		
		return row;
		
	}
	
	/**
	 * Intent: Find the hash string associated with a file name.
	 * 
	 * Postcondition1 (Loop through map): Each file name key in the parameter 
	 * map is iterated over.
	 * Postcondition2 (Retrieve hash string): A hash string is searched for each
	 * filename. If one is not found, the user is notified on the console.
	 * Postcondition3 (Path and hash string map): A hash string is found for
	 * each filename and the graded path and hash string are stored in a map.
	 * 
	 * @param filename
	 * @return
	 */
	public Map<Path,String> createPathHashMap(Map<String, Path> fileMap) 
			throws SQLException {
		
		Map<Path, String> pathHashMap = new HashMap<>();
		
		// Post1 Loop through map
		PreparedStatement ps = null;
		ResultSet rs = null;
		for (Map.Entry<String, Path> entry : fileMap.entrySet()) {
			
			try {
				// Post2 Retrieve hash string
				ps = connection.prepareStatement(HASH_STRING);
				ps.setString(1, entry.getKey());
				rs = ps.executeQuery();
				
				// Post3 Path and hash string map
				pathHashMap.put(entry.getValue(),rs.getString(1));
			
			} catch (SQLException e) {
				System.out.println("\nCould not find hash string for: " 
						+ entry.getKey());
			}
			
		}
		
		// Close resources
		rs.close();
		ps.close();
		
		return pathHashMap;
		
	}
	
 	
	/**
	 * Intent: Locate previous assignment data in JGRAM database.
	 * 
	 * Postcondition1 (Assignment id): A previous assignment record is searched
	 * for in the Assignment table.
	 * Postcondition2 (Result id list): A list of associated result record ids
	 * is created.
	 * Postcondition3 (Delete data): The related records that contain
	 * previous assignment data are deleted from the JGRAM database.
	 * 
	 * @throws SQLException
	 */
	private void findPreviousAssignment() throws SQLException {
		
		// Post1 Assignment id
		try {
			assignmentID = getRowID(ASSIGNMENT_ID, assignment
					.getAssignmentTitle());
		} catch (SQLException e) {
			// No assignment id found
			return;
		}
		
		// Post2 Result id list
		ArrayList<Integer> resultList = createResultList();
		
		// Post3 Delete data
		// Checkpoint
		for (int rID : resultList) {
			deleteData(DELETE_CHECKPOINTS, rID);
		}
		
		// Result
		deleteData(DELETE_RESULT, assignmentID);
		
		// GradeMapping
		deleteData(DELETE_GRADEMAPPING, assignmentID);
		
		// Assignment
		deleteData(DELETE_ASSIGNMENT, assignmentID);
		
	}
	
	/**
	 * Intent: Return Assignment object.
	 * @return
	 */
	public Assignment getAssignment() {
		return assignment;
	}
	
	/**
	 * Intent: Return current directory of the JGRAM database.
	 * @return
	 */
	private static String getDBFile() {
		String cwd = new File("").getAbsolutePath();
		cwd += "/src/jgram/database/jgram.db";
		return cwd;
	}
	
	/**
	 * Intent: Retrieve the row's unique identifier (primary key).
	 * 
	 * Postcondition1 (Prepare statement): A prepared statement is created with 
	 * the assignment title as the first input parameter.
	 * Poscondition2 (Execute and return query): The query to obtain the 
	 * assignment id is executed and the result is returned.
	 * 
	 * @param sql
	 * @param where
	 * 
	 * @throws SQLException
	 */
	private int getRowID(String sql, String where) throws SQLException {
		
		// Post1 Prepare statement
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, where);
		
		// Post 2 Execute and return query
		ResultSet results = preparedStatement.executeQuery();
		int rowID = results.getInt(1);
		
		// Close resources
		results.close();
		preparedStatement.close();
				
		return rowID;
		
		
	}
	
	/**
	 * Intent: Open a connection to the JGRAM database.
	 * 
	 * Postcondition1 (Database connection): A connection to the JGRAM database
	 * is established.
	 * @throws SQLException 
	 */
	public void openConnection() throws SQLException {
		connection = DriverManager.getConnection(URL);
	}
	
	/**
	 * Intent: Return an array of rows that contains all student grading data
	 * for an assignment.
	 * 
	 * Postcondition1 (Prepare statement): The query statement is prepared to
	 * select assignment by ID which has been determined by the assignmentTitle
	 * parameter.
	 * Postcondition 2 (Execute query and extract results): The query is 
	 * executed and results are returned. These results are parsed into a String
	 * array of arrays (rows).
	 * 
	 * @param assignmentTitle
	 * @return
	 * @throws SQLException 
	 */
	public ArrayList<String[]> selectAllStudents(String assignmentTitle) 
			throws SQLException {
		
		// Post1 Prepare statement
		assignmentID = getRowID(ASSIGNMENT_ID, assignmentTitle);
		PreparedStatement preparedStatement = connection.prepareStatement(ALL_STUDENTS);
		preparedStatement.setInt(1, assignmentID);
		
		// Post 2 Execute query and extract results
		ResultSet results = preparedStatement.executeQuery();
		ArrayList<String[]> rows = new ArrayList<>();
		
		while (results.next()) {
			rows.add(createStudentRow(results));
		}
				
		// Close resources
		results.close();
		preparedStatement.close();
				
		return rows;
		
	}
	
	/**
	 * Intent: Return an array of rows that contains all assignment stats.
	 * 
	 * Postcondition1 (Prepare statement): The query statement is prepared to
	 * select all assignments with aggregate functions grouped by assignment id.
	 * Postcondition 2 (Execute query and extract results): The query is 
	 * executed and results are returned. These results are parsed into a String
	 * array of arrays (rows).
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public ArrayList<String[]> selectAssignmentStats() throws SQLException {
		
		// Post1 Prepare statement
		Statement statement = connection.createStatement();
		
		// Post 2 Execute query and extract results
		ResultSet results = statement.executeQuery(ASSIGNMENTS_STATS);
		ArrayList<String[]> rows = new ArrayList<>();
		
		while (results.next()) {
			rows.add(createStatRow(results));
		}
				
		// Close resources
		results.close();
		statement.close();
				
		return rows;
		
	}
	
	/**
	 * Intent: Return an array of rows that contains one student's grading data
	 * for all assignments.
	 * 
	 * Postcondition1 (Prepare statement): The query statement is prepared to
	 * select student by name.
	 * Postcondition 2 (Execute query and extract results): The query is 
	 * executed and results are returned. These results are parsed into a String
	 * array of arrays (rows).
	 * 
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	public ArrayList<String[]> selectStudentAssignments(String[] name) 
			throws SQLException {
		
		// Post1 Prepare statement
		PreparedStatement preparedStatement = connection.prepareStatement(STUDENT_ASSIGNMENTS);
		preparedStatement.setString(1, name[0]);
		preparedStatement.setString(2, name[1]);
		
		// Post 2 Execute query and extract results
		ResultSet results = preparedStatement.executeQuery();
		ArrayList<String[]> rows = new ArrayList<>();
		
		while (results.next()) {
			rows.add(createStudentRow(results));
		}
				
		// Close resources
		results.close();
		preparedStatement.close();
				
		return rows;
		
	}
	
	/**
	 * Intent: Set the database URL string.
	 */
	public void setURL(String inputURL) {
		URL = inputURL;
	}
	
	/**
	 * Intent: Write assignment record.
	 * 
	 * Postcondition1 (Create query): A query is created to insert a row into 
	 * the Assignment table.
	 * Postcondition2 (Execute query): A row is inserted into the Assignment
	 * table.
	 * 
	 * @throws SQLException
	 */
	private void writeAssignment() throws SQLException {
		
		// Post1 Create query
		PreparedStatement preparedStatement = connection
				.prepareStatement(ASSIGNMENT_INSERT);
		// Grader
		preparedStatement.setString(1, assignment.getGrader());
		
		// Title
		preparedStatement.setString(2, assignment.getAssignmentTitle());
		
		// Location
		preparedStatement.setString(3, assignment.getLocation());
		
		// Post2 Execute query
		preparedStatement.executeUpdate();
		
		// Close resources
		preparedStatement.close();

	}
	
	
	/**
	 * Intent: Write assignment data to various JGRAM database tables.
	 * 
	 * Postcondition1 (Previous assignment): If a previous assignment exists in
	 * the JGRAM database, it is deleted.
	 * Postcondition2 (Assignment): The Assignment object is written to the
	 * JGRAM database Assignment table.
	 * Postcondition3 (Assignment ID): The id of the Assignment record is 
	 * retrieved for later use.
	 * Postcondition4 (GradeMapping): The GradeMapping object is written to the
	 * JGRAM database GradeMapping table.
	 * Postcondition5 (Result): The Result object is written to the JGRAM 
	 * database Result table and Checkpoint table. 
	 * 
	 * @throws SQLException
	 */
	public void writeAssignmentData() throws SQLException {
				
		// Post1 Previous assignment
		findPreviousAssignment();
		
		// Post2 Assignment
		writeAssignment();
		
		// Post3 Get Assignment ID
		assignmentID = getRowID(ASSIGNMENT_ID, assignment.getAssignmentTitle());
		
		// Post4 GradeMapping
		writeGradeMapping();
		
		// Post5 Result
		writeResultData();
		
	}
	
	/**
	 * Intent: Write Checkpoint records.
	 * 
	 * Postcondition1 (Checkpoint List): Each Checkpoint object is iterated
	 * upon from the Result object's checkpoint list.
	 * Postcondition2 (Create query): A query is created to insert the 
	 * Checkpoint object as a row in the JGRAM database Checkpoint table.
	 * Postcondition3 (Execute query): A row is inserted into the Checkpoint
	 * table.
	 * 
	 * @param result
	 * @param resultID
	 * @throws SQLException
	 */
	private void writeCheckpoints(Result result, int resultID) 
			throws SQLException {
		
		PreparedStatement preparedStatement = null;
		
		// Post1 Checkpoint list
		for (Checkpoint cp : result.getCheckpointList()) {
			
			// Post2 Create query
			preparedStatement = connection
					.prepareStatement(CP_INSERT);
			
			// Weight
			preparedStatement.setInt(1, cp.getWeight());
			
			// Grade
			preparedStatement.setInt(2, cp.getGrade());
			
			// Feedback
			preparedStatement.setString(3, cp.getFeedback());
			
			// Result id
			preparedStatement.setInt(4, resultID);
			
			// Post3 Execute query
			preparedStatement.executeUpdate();
		}
		
		// Close resources
		preparedStatement.close();
			
	}
	
	/**
	 * Intent: Write grade mapping record.
	 * 
	 * Postcondition1 (Create query): A query is created to insert one row
	 * for each grade mapping limit into the GradeMapping table.
	 * Postcondition2 (Execute query): A row is inserted into the GradeMapping
	 * table.
	 * 
	 * @throws SQLException
	 */
	private void writeGradeMapping() throws SQLException {
		
		// Post1 Create query
		PreparedStatement preparedStatement = connection
				.prepareStatement(GM_INSERT);
		
		// Limits
		GradeMapping gm = assignment.getGradeMapping();
		for (Map.Entry<String, Integer> entry : gm.getLimits().entrySet()) {
			
			int counter = 1;
			
			preparedStatement.setString(counter, entry.getKey());
			counter++;
			
			preparedStatement.setInt(counter, entry.getValue());
			counter++;
			
			preparedStatement.setInt(counter, assignmentID);
			
			// Post2 Execute query
			preparedStatement.executeUpdate();
			
		}
		
		// Close resources
		preparedStatement.close();

	}
	
	/**
	 * Intent: Write result record.
	 * 
	 * Postcondition1 (Create query): A query is created to insert the Result
	 * object parameter as a row in the JGRAM database Result table.
	 * Postcondition2 (Execute query): A row is inserted into the Result
	 * table.
	 * 
	 * @param result
	 * @throws SQLException
	 */
	private void writeResult(Result result) throws SQLException {
		
		// Post2 Create query
		PreparedStatement preparedStatement = connection
				.prepareStatement(RESULT_INSERT);
		
		// First name
		preparedStatement.setString(1, result.getStudentFirstName());
		
		// Last name
		preparedStatement.setString(2, result.getStudentLastName());
		
		// Assignment name
		preparedStatement.setString(3, result.getAssignmentName());
		
		// Total grade
		preparedStatement.setDouble(4, result.getTotalGrade());
		
		// Hash string
		preparedStatement.setString(5, result.getHashString());
		
		// Assignment id
		preparedStatement.setInt(6, assignmentID);
		
		// Post2 Execute query
		preparedStatement.executeUpdate();
		
		// Close resources
		preparedStatement.close();
	}
	
	/**
	 * Intent: Write result record.
	 * 
	 * Postcondition1 (Result list): Each Result object is iterated upon from
	 * the Assignment object's result list.
	 * Postcondition2 (Write result): A row is inserted into the Result
	 * table for each Result object.
	 * Postcondition3 (Get result id): The previous Result record id is 
	 * retrieved and stored for later use.
	 * Postcondition4 (Write checkpoints): All Checkpoint objects contained as a
	 * list in the Result object are written to the Checkpoint table.
	 * 
	 * @throws SQLException
	 */
	private void writeResultData() throws SQLException {
		
		// Post1 Result list
		for (Result result : assignment.getResultList()) {
			
			// Post2 Write result
			writeResult(result);
			
			// Post3 Get result id
			int resultID = getRowID(RESULT_ID, result.getAssignmentName());
			
			// Post4 Write checkpoints
			writeCheckpoints(result, resultID);
		}

	}
 
}
