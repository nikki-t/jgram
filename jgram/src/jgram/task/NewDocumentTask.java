package jgram.task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class NewDocumentTask extends Task {
	
	// Constructor(s)
	public NewDocumentTask() {
		super();
	}
	
	/**
	 * Intent: A description of the new document task is displayed to the console.
	 */
	@Override
	public void displayHelp() {
		
		String help = "New Document Task Help:"
				+ "\n\n\tThe new document task evaluates Word document "
				+ "\n\tassignments that are ready to be graded and determines if"
				+ "\n\tthe assignment contains any checkpoints, a grade mapping, or"
				+ "\n\tresults table. This task is used to evaluate assignments "
				+ "\n\tthat are ready to be graded and can be run prior to the "
				+ "\n\tinsertion of checkpoints and a grade mapping in preparation"
				+ "\n\tfor the evaluation task.\n";
		
		System.out.println(help);
	}
	
	/**
	 * Intent: Validate a new document to see if it contains any grading data.
	 * 
	 * This task is used to determine if an assignment is ready to be graded
	 * by an instructor.
	 * 
	 * Postcondition1 (Preparation): A list of valid files is created.
	 * Postcondition2 (Create ExecutorService): An ExecutorService object is
	 * created to handle the execution of a task run as a new thread on each 
	 * file on up to 5 files at a time.
	 * Postcondition3 (Document loop): All valid documents are stored in a list
	 * and each document is ready for validation.
	 * Postcondition4 (Task run in a new thread): The new document task run
	 * is executed in a separate thread.
	 * 
	 */
	@Override
	public void performTask() {
		
		try {
			
			// Post1 Preparation
			prep();
			
			// Post2 Create ExecutorService
			ExecutorService executorService = getExecutorService();
			
			// Post3 Document loop
			for (Path path : getFileList()) {
				
				// Post4 Task run in a new thread
				NewDocTaskRun taskRun = new NewDocTaskRun(path);
				executorService.execute(taskRun);
				incrementThreadCount();
					
			}
			
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, 
					TimeUnit.NANOSECONDS);
				
		} catch (Exception e) {
			displayException(e, "Could not process files in directory.");
		
		}
		
	}
	
	/**
	 * Intent: Run several operations to prepare for NewDocumentTask execution.
	 * 
	 * Postcondition1 (Create file list): A list of files that need to be 
	 * tested for grading data is created.
	 * 
	 * @throws IOException
	 */
	@Override
	protected void prep() throws IOException {
		
		// Post1 Create file list
		getDirectoryAndFileList();
	}
	
}
