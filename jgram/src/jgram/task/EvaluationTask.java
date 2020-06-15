package jgram.task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
	
	// Constructor(s)
	public EvaluationTask() {
		super();
	}
		
	public EvaluationTask(String userSecret) {
		super(userSecret);
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
	 * Postcondition4 (Handle exceptions): Exceptions are reported to the 
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
			
			// Post4 Evaluate each document
			for (Path path : getFileList()) {
					
				EvalTaskRun taskRun = new EvalTaskRun(path, recordManager,
						getSecret());
				executorService.execute(taskRun);
				incrementThreadCount();
				
			} 
			
			// Shut down executor service and block until thread 
			// execution is complete
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, 
					TimeUnit.NANOSECONDS);
			
			System.out.println("\nFINISHED GRADING. Check 'GRADED' directory "
					+ "for graded assignments.");
			
			// Close object output stream
			recordManager.getOutputStream().close();
		
		// Post4 Handle exceptions
		} catch (Exception e) {
			displayException(e, "Could not grade any assignments.");
		}
	}
	
	/**
	 * Intent: Run several operations to prepare for EvaluationTask execution.
	 * 
	 * Postcondition1 (Create file list): A list of files that need to be 
	 * evaluated for grading data is created.
	 * Postcondition2 (Create RecordManager): A RecordManager object has been created
	 * and an output stream has been set to write the record to.
	 * 
	 * @throws IOException
	 */
	@Override
	protected void prep() throws IOException {
		
		// Post1 Create file list
		getDirectoryAndFileList();
		
		// Post2 Create RecordManager
		recordManager = new RecordManager(getWorkingDirectory());
		recordManager.createOutputStream();
		
	}
}
