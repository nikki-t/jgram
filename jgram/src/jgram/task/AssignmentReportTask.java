package jgram.task;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import jgram.storage.RecordManager;

/**
 * Intent: Provide the user with three different assignment reports obtained
 * from saved grading results.
 * 
 * Postcondition1 (All students for one assignment): The grading results for all
 * students for one assignment are saved to the file system as a txt file.
 * 
 * Postcondition2 (Get one student's assignments): The grading results for all
 * assignments for one student are saved to the file system as a txt file.
 * 
 * Postcondition3 (Get assignment stats): The stats for all previously graded
 * assignments are saved to the file system as a txt file.
 *
 */
public class AssignmentReportTask extends Task {
	
	// Class variable(s)
	private static final String REPORT_SELECTION = "Select Report : " 
			+ "\n\t 1 : Assignment student grades " 
			+ "\n\t 2 : Student grade data for all assignments"
			+ "\n\t 3 : Assignment stats" 
			+ "\n\t 4 : Exit"
			+ "\n\t\t (Example: 1): ";
	
	// Instance variable(s)
	private PrintWriter outStream;
	private Path reportPath;
	private RecordManager rm;
	
	private List<String[]> rows;

	public AssignmentReportTask() {
		super();
		rm = new RecordManager();
	}
	
	public AssignmentReportTask(Scanner keyboard) {
		super(keyboard);
		rm = new RecordManager();
	}
	
	/**
	 * Intent: Get the path where the assignment report will be saved.
	 * @throws FileNotFoundException 
	 */
	private void createReportPath(String filename) 
			throws FileNotFoundException {
		
		// Report Path
		reportPath = Paths.get(getWorkingDirectory().toString() 
				+ "/REPORT_" + filename + ".txt");
		
		// Output stream
		outStream = new PrintWriter(reportPath.toString());
	}
	
	/**
	 * Intent: A description of the report task is displayed to the console.
	 */
	@Override
	public void displayHelp() {
		
		String help = "Assignment Report Task Help:"
				+ "\n\n\tThe assignment report task provides 3 different "
				+ "reports:\n"
				+ "\n\t1. A report of all student grading data for one "
				+ "assignment."
				+ "\n\t2. A report of all assignment grading data for one "
				+ "student."
				+ "\n\t3. A report of assignment stats for all saved "
				+ "assignments.";
		
		System.out.println(help);
		
	}
	
	/**
	 * Intent: Class Postcondition1 All students for one assignment.
	 * 
	 * Postcondition1 (Obtain assignment name): The assignment name is obtained
	 * from the user.
	 * Postcondition2 (Query RecordManager): The RecordManager has queried the
	 * JGRAM database for past grading results and returned an ArrayList of
	 * rows.
	 * Postcondition3 (Obtain report path): The report path is determined for
	 * the text file.
	 * Postcondition4 (Print report): A report is printed to a text file that 
	 * contains the previous grading data.
	 * 
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	private void getAllStudents() throws FileNotFoundException, SQLException {
		
		// Post1 Obtain assignment name
		String assignment = getAssignmentTitle();
		
		// Post2 Query RecordManager
		rm.openConnection();
		try {
			rows = rm.selectAllStudents(assignment);
		} catch (SQLException e) {
			throw new SQLException("\nERROR: " + e.getMessage() + ". \n\tIt's "
					+ "possible no assignment could be found for the title you "
					+ "entered.");
		}
		rm.closeConnection();
		
		// Post3 Obtain report path 
		createReportPath(assignment);
		
		// Post4 Print report
		outStream.println(assignment + " Assignment Report\n");
		printStudentReport();
		outStream.close();
		
	}
	
	/**
	 * Intent: Class Postcondition3 Get assignment stats
	 * 
	 * Postcondition1 (Query RecordManager): The RecordManager has queried the
	 * JGRAM database for assignment stats and returned an ArrayList of rows.
	 * Postcondition2 (Obtain report path): The report path is determined for
	 * the text file.
	 * Postcondition3 (Print report): A report is printed to a text file that 
	 * contains the assignment stats.
	 * 
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	private void getAssignmentStats() 
			throws FileNotFoundException, SQLException {
		
		// Post1 Query RecordManager
		rm.openConnection();
		rows = rm.selectAssignmentStats();
		rm.closeConnection();
		
		// Post2 Test rows
		if (rows.isEmpty()) {
			throw new SQLException("\nNo assignment stats were located.");
		}
		
		// Post2 Obtain report path 
		createReportPath("assignment_stats");
		
		// Post3 Print report
		outStream.println("Assignment Stats Report\n");
		printStatsReport();
		outStream.close();
		
	}
	
	/**
	 * Intent: Obtain the assignment name from the user.
	 * 
	 * @return
	 */
	public String getAssignmentTitle() {
		
		System.out.println("\nPlease enter the assignment title: ");
		String assignment = getKeyboard().nextLine();
		return assignment;
		
	}
	
	/**
	 * Intent: Return RecordManager reference.
	 * 
	 * @return
	 */
	public RecordManager getRecordManager() {
		return rm;
	}
	
	/**
	 * Intent: Obtain the type of report the user would like to run.
	 * @return
	 */
	public String getReportSelection() {
		
		System.out.println("\n----------------------------[ REPORT SELECTION ]"
				+ "-------------------------------\n");
		
		System.out.println(REPORT_SELECTION);
		String userInput = getKeyboard().nextLine();
		
		return userInput;
		
		
	}
	
	/**
	 * Intent: Class Postcondition2 Get one student's assignments
	 * 
	 * Postcondition1 (Obtain student name): The student's name is obtained
	 * from the user and converted into an array with the last name at the
	 * second element and the first name at the first element.
	 * Postcondition2 (Query RecordManager): The RecordManager has queried the
	 * JGRAM database for past grading results for one student and 
	 * returned an ArrayList of rows.
	 * Postcondition3 (Obtain report path): The report path is determined for
	 * the text file.
	 * Postcondition4 (Print report): A report is printed to a text file that 
	 * contains the previous grading data for one student.
	 * 
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	private void getStudentAssignments() 
			throws FileNotFoundException, SQLException {
		
		// Post1 Obtain student name
		String[] studentName = getStudentName();
		
		// Post2 Query RecordManager
		rm.openConnection();
		rows = rm.selectStudentAssignments(studentName);
		rm.closeConnection();
		
		// Test rows
		if (rows.isEmpty()) {
			throw new SQLException("\nNo assignment data could be found for"
					+ " student: " + studentName[0] + " " + studentName[1]);
		}
		
		// Post3 Obtain report path 
		createReportPath(studentName[1] + "_" + studentName[0]);
		
		// Post4 Print report
		outStream.println(studentName[0] + " " + studentName[1] 
				+ " Assignment Report\n");
		printStudentReport();
		outStream.close();
		
		
	}
	
	/**
	 * Intent: Obtain a student's name from the user and create an array where
	 * the student's first name is at the first element and the student's last
	 * name is at the second element.
	 * 
	 * @return
	 */
	public String[] getStudentName() {
		
		// First
		System.out.println("\nEnter the student's first name: ");
		String first = getKeyboard().nextLine();
		
		// Last
		System.out.println("\nEnter the student's last name: ");
		String last = getKeyboard().nextLine();
		
		// Create name array
		String[] studentName = {first, last};
		return studentName;
	}

	/**
	 * Intent: Class Postcondition 1, 2, or 3 is performed based on user 
	 * selection.
	 * 
	 * Postcondition4 (Handle Exceptions): All report method exceptions are 
	 * handled and the user is notified.
	 */
	@Override
	public void performTask() {
		
		try {
			
			prep();
		
			// Loop until the user indicates they wish to exit
			boolean keepGoing = true;
			while (keepGoing) {
				
				try {
					
					// Obtain report selection
					String selection = getReportSelection();
					
					// Query based on selection
					switch(selection) {
						
						// Post1 All students for one assignment
						case "1":
							getAllStudents();
							break;
						
						// Post2 Get one student's assignments
						case "2":
							getStudentAssignments();
							break;
						
						// Post3 Get assignment stats
						case "3":
							getAssignmentStats();
							break;
						
						// Exit
						case "4":
							keepGoing = false;
							break;
						
						default:
							System.out.println("Invalid selection. Please try "
									+ "again.");
							break;						
					}
				
				} catch (SQLException e) {
					System.out.println(e.getMessage());	
				}
			} // End while
			
		// Post4 Handle exceptions	
		} catch (Exception e) {
			displayException(e, "Could not write report.");
	
		}
		
	}
	
	/**
	 * Intent: Run several operations to prepare for AssignmentReportTask 
	 * execution.
	 * 
	 * Postcondition1 (Report file path): A report file path is defined.
	 * Postcondition2 (Output stream): An output stream is set.
	 */
	@Override
	public void prep() throws Exception {
		
		// Report file path
		System.out.println("\nChoose a directory to save a report file to.");
		getDirectory();
		
	}
	
	/**
	 * Intent: Print all students grades for one assignment to a text file.
	 * 
	 * Postcondition1 (Columns): The column names are printed to the text
	 * file.
	 * Postcondition2 (Rows): The rows are printed to the text file.
	 * Postcondition3 (Notify user): The user is notified on the console
	 * that the report has been printed and the location of the report.
	 */
	private void printStatsReport() {
		
		// Post1 Columns
		String columns = String.format("%-30s  %-5s   %-7s   %-7s   %-7s", 
				"Assignment Title", "Count", "Minimum", "Maximum", "Average");
		outStream.println(columns);
		
		// Post2 Rows
		for (String[] row : rows) {
			String rowString = String.format("%-30s  %-5s   %-7s   %-7s   %-7s", 
					row[0], row[1], row[2], row[3], row[4]);
			outStream.println(rowString);
		}
		
		// Post3 Notify user
		System.out.println("\nReport printed to: " + reportPath.toString());
		
	}
	
	/**
	 * Intent: Print all students grades for one assignment to a text file.
	 * 
	 * Postcondition1 (Columns): The column names are printed to the text
	 * file.
	 * Postcondition2 (Rows): The rows are printed to the text file.
	 * Postcondition3 (Notify user): The user is notified on the console
	 * that the report has been printed and the location of the report.
	 */
	private void printStudentReport() {
		
		// Post1 Columns
		String columns = String.format("%-10s   %-30s  %-13s   %-12s   %-30s   "
				+ "%-11s   %-6s   %-5s   %s", 
				"Grader", "Assignment Title", "Student First", "Student Last", 
				"Assignment Name", "Total Grade", "Weight", "Grade", "Feedback");
		outStream.println(columns);
		
		// Post2 Rows
		for (String[] row : rows) {
			String rowString = String.format("%-10s   %-30s  %-13s   %-12s   "
					+ "%-30s   %-11s   %-6s   %-5s   %s", 
					row[0], row[1], row[2], row[3], row[4], row[5], row[6], 
					row[7], row[8]);
			outStream.println(rowString);
		}
		
		// Post3 Notify user
		System.out.println("\nReport printed to: " + reportPath.toString());
		
	}

}
