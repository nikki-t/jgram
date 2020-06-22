package jgram.task;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import jgram.storage.Assignment;
import jgram.storage.RecordManager;

/**
 * Intent: Calculate and output the grade for an assignment.
 * 
 * Precondition1 (Task Selection): The user has selected the evaluation task
 * to grade the assignment document.
 * 
 * Postcondition1 (Document creation): A Document object is created that 
 * contains checkpoints, a grade mapping, and a result.
 * Postcondition2 (Document grade calculation): The Document is graded and
 * a Result object is created.
 * Postcondition3 (Result is appended to Document): The Result object is 
 * is used to create a table that is displayed to the console.
 * 
 */
public class EvaluationTask extends Task {
	
	// Instance variable(s)
	private RecordManager recordManager;
	private Assignment assignment;
	
	// Constructor(s)
	public EvaluationTask() {
		super();
	}
		
	public EvaluationTask(String userSecret, Scanner inputKeyboard) {
		super(userSecret, inputKeyboard);
	}
	
	/**
	 * Intent: Create Assignment object.
	 * 
	 * Precondition1 (Working directory): The task's working directory has been
	 * obtained from the user.
	 * 
	 * Postcondition1 (Define data): All the data required by an Assignment 
	 * object is obtained.
	 * Postcondition2 (Create Assignment): An Assignment object is created.
	 */
	public void createAssignment() {
		
		// Post1 Define data
		String grader = System.getProperty("user.name");
		String title = retrieveAssignmentTitle();
		String location = getWorkingDirectory().toString();
		
		// Post2 Create Assignment
		assignment = new Assignment(grader, title, location);
	}
	
	/**
	 * Intent: A description of the evaluation task is displayed to the console.
	 */
	@Override
	public void displayHelp() {
		
		String help = "Evaluation Task Help:"
				+ "\n\n\tThe evaluation task accepts a file system directory" 
				+ "\n\tas input and searches the directory for Word document "
				+ "\n\tassignment files. The evaluation task then parses each " 
				+ "\n\tWord document file's for comments that contain either "
				+ "\n\tcheckpoints or a grade mapping. Once validated and " 
				+ "\n\textracted, the evaluation task then calculates the " 
				+ "\n\tassignment's total grade. A table of checkpoints and the " 
				+ "\n\tfinal grade is appended to a copy of the Word document " 
				+ "\n\tassignment which is created and saved in the same " 
				+ "\n\tdirectory as the assignment.\n";
		
		System.out.println(help);	
		
	}
	
	/**
	 * Intent: Evaluate, calculate and display assignment grade.
	 * 
	 * Postcondition1 (Preparation): A file list of valid files is created and 
	 * a RecordManager object is created for writing records.
	 * Postcondition2 (Create ExecutorService): An ExecutorService object is
	 * created to handle the execution of a task run as a new thread on each 
	 * file on up to 5 files at a time.
	 * Postcondition3 (Evaluate each document): Each document is evaluated in
	 * a new thread and a grade is calculated and a hash string of encoded 
	 * results has been created and stored.
	 * Postocndition4 (Reset document number): The TaskRun class document
	 * number is reset to zero for multiple assignment grading tasks.
	 * Postcondition5 (Write assignment): Assignment record which contains
	 * all of the grading results is written to the JGRAM database.
	 * Postcondition6 (Handle exceptions): Exceptions are reported to the 
	 * console and control returns to caller.
	 */
	@Override
	public void performTask() {
		
		// Notice on overwritten graded files
		System.out.println("\nIMPORTANT: Any previously graded assignments "
		+ "will be overwritten.");
		
		try {
		
			//Post1 Preparation
			prep();
			
			// Post2 Create ExecutorService
			ExecutorService executorService = getExecutorService();
			
			// Post3 Evaluate each document
			EvalTaskRun taskRun = null;
			for (Path path : getFileList()) {
					
				taskRun = new EvalTaskRun(assignment, path, 
						getSecret());
				
				executorService.execute(taskRun);
				incrementThreadCount();
				
			} 
			
			// Shut down executor service and block until thread 
			// execution is complete
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, 
					TimeUnit.NANOSECONDS);
			
			// Post4 Reset document number
			taskRun.resetDocNumber();
			
			// Post5 Write assignment
			writeAssignmentData();
						
			System.out.println("\nFINISHED GRADING. Check 'GRADED' directory "
					+ "for graded assignments."
					+ "\nAssignment grading results have been SAVED.");
		
		// Post6 Handle exceptions
		} catch (SQLException e) {
			displayException(e, "Could not save grading results.");
		
		} catch (Exception e) {
			displayException(e, "Could not grade any assignments.");
		}
	}
	
	/**
	 * Intent: Run several operations to prepare for EvaluationTask execution.
	 * 
	 * Postcondition1 (Create file list): A list of files that need to be 
	 * evaluated for grading data is created.
	 * Postcondition2 (Assignment title): The title of the assignment to be 
	 * graded is obtained and stored.
	 * Postcondition3 (Grader): The username of the grader is stored.
	 * Postcondition4 (Create RecordManager): A RecordManager object has been 
	 * created.
	 * 
	 * @throws IOException
	 */
	@Override
	public void prep() throws IOException {
		
		// Post1 Create file list
		System.out.println("\nChoose a directory that contains Word documents "
				+ "that require grading.");
		getDirectoryAndFileList();
		
		// Post2 Create Assignment
		createAssignment();
		
		// Post3 Create RecordManager
		recordManager = new RecordManager(assignment);
		
	}
	
	/**
	 * Intent: Ask the user for the name of the assignment to be graded.
	 */
	public String retrieveAssignmentTitle() {
		
		System.out.println("\nPlease enter the name of the assignment: ");
		
		String assignmentTitle = getKeyboard().nextLine();
		
		return assignmentTitle;
		
	}

	/**
	 * Intent: Set Assignment instance variable.
	 */
	public void setAssignment(Assignment inputAssignment) {
		assignment = inputAssignment;
	}
	
	/**
	 * Intent: Set RecordManager instance variable.
	 */
	public void setRecordManager(RecordManager rm) {
		recordManager = rm;
	}
	
	/**
	 * Intent: Write assignment data to JGRAM database.
	 * 
	 * Postcondition1 (Open connection): A connection to the JGRAM database is
	 * established.
	 * Postcondition2 (Write data): Assignment data is written to tables in the
	 * JGRAM database.
	 * Postcondition3 (Close connection): The connection to the JGRAM database
	 * is closed.
	 * 
	 * @throws SQLException
	 */
	private void writeAssignmentData() throws SQLException {
		
		// Post1 Open connection
		recordManager.openConnection();
		
		// Post2 Write data
		recordManager.writeAssignmentData();
		
		// Post2 Close connection
		recordManager.closeConnection();
		
	}
	
	
}
