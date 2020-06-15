package jgram.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import jgram.storage.RecordManager;

/**
 * Intent: Determine if a previously graded assignment has been modified.
 * 
 * Precondition1 (Graded assignments): Word document assignments have been
 * previously graded and stored in sub-directory named 'GRADED'.
 * 
 * Postcondition1 (Task performed for all files): All files are evaluated for
 * modifications and a report on each file is output to the sub-directory 
 * 'GRADED' and it titled 'report.txt'.
 *
 */
public class TamperTask extends Task {
	
	// Instance variable(s)
	private PrintWriter outStream;
	private RecordManager recordManager;
	private String reportFilename;
	
	// Constructor(s)
	public TamperTask() {
		super();
	}
	
	public TamperTask(String userSecret) {
		super(userSecret);
	}
	
	/**
	 * Intent: Create a list of previously saved records.
	 * 
	 * Postcondition1 (RecordManager): A RecordManager object is created from
	 * the current working directory.
	 * Postcondition2 (Input stream): An ObjectInputStream object is created
	 * in order to read data from the previously saved file.
	 * Postcondition3 (Record list): A list of records is created from the 
	 * previously saved file.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void createRecordManager() throws FileNotFoundException, IOException,
			ClassNotFoundException {
		
		// Post1 RecordManager
		recordManager = new RecordManager(getWorkingDirectory());
		
		// Post2 Input stream
		recordManager.createInputStream();
		
		// Post3 Record list
		recordManager.createRecordListFromFile();
		
		// Close open resources
		recordManager.getInputStream().close();
		
	}
	
	/**
	 * Intent: A description of the tamper test task is displayed to the console.
	 */
	@Override
	public void displayHelp() {
		
		String help = "Tamper Test Task Help:"
				+ "\n\n\tThe tamper test task determines if the checkpoints,"
				+ "\n\tgrade mapping, or results have been modified for a "
				+ "\n\tpreviously graded assignment. The task uses a hash string "
				+ "\n\tcreated from the 'secret' to determine if any portion of"
				+ "\n\tthe assignment grading has been changed. The results are"
				+ "\n\tdisplayed to the console.\n";
		
		System.out.println(help);
		
	}
	
	private String getReportFilename(Path path) {
		
		// Locate previously graded file directory
		String prevGradedFile =  path.toString();
		
		// Return string that includes 'report.txt' as report file name
		return prevGradedFile + "/report.txt";
		
	}
	
	/**
	 * Intent: Post 1 Task performed for all files: 
	 * Detect if any grade mappings, checkpoints, or assignment results
	 * have been modified.
	 * 
	 * Precondition1 (Graded assignment): The assignment that is evaluated has
	 * already been graded by JGRAM.
	 * 
	 * Postcondition1 (Preparation): A list of valid files is 
	 * created, a PrintWriter object is created for the report, and a 
	 * RecordManager object is created for retrieving records.
	 * Postcondition2 (Create ExecutorService): An ExecutorService object is
	 * created to handle the execution of a task run as a new thread on each 
	 * file on up to 5 files at a time.
	 * Postcondition3 (Document loop): All documents in a directory have been
	 * iterated on and the task operations have been executed if applicable. 
	 * Postcondition4 (Handle exceptions): Exceptions are reported to the 
	 * console and control returns to the caller.
	 */
	@Override
	public void performTask() {
		
		try {
			
			// Post1 Preparation
			prep();
			
			// Post2 Create ExecutorService
			ExecutorService executorService = getExecutorService();
						
			// Post3 Document Loop
			for (Path path : getFileList()) {
				
				TamperTaskRun taskRun = new TamperTaskRun(outStream, path, 
						recordManager, getSecret());
				executorService.execute(taskRun);
				incrementThreadCount();
			}
			
			// Shut down executor service and block until thread 
			// execution is complete
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, 
					TimeUnit.NANOSECONDS);
		
		// Post4 Handle exceptions
		} catch (Exception e) {
			displayException(e, "Could not determine tamper status.");
			
		} finally {
			
			// Close out stream if possible
			if (outStream != null) {
				outStream.close();
			}
			
			// Write report if possible
			if (reportFilename != null) {
				System.out.println("\nReport written to: " + reportFilename);
			}
		}
		
	}
	
	/**
	 * Intent: Run several operations to prepare for TamperTask execution.
	 * 
	 * Postcondition1 (Working directory): The working directory is determined.
	 * Postcondition2 (Graded): The working directory is adjusted to target
	 * the nested 'GRADED' sub-directory for the tamper task.
	 * Postcondition3 (File list): A list of valid graded files is created.
	 * Postcondition4 (PrintWriter): A PrinterWriter object is created with
	 * the name of the report file.
	 * Postcondition5 (RecordManager creation): RecordManager object is 
	 * created and a list of records is retrieved.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	@Override
	protected void prep() throws IOException, ClassNotFoundException {
		
		// Post1 Working directory
		Scanner keyboard = new Scanner(System.in);
		getDirectory(keyboard);
		
		// Post2 Graded
		testTamperPath();
		
		// Post3 File list
		createFileList();
		
		// Return to main menu if file list was not found
		if (getFileList().isEmpty()) {
			throw new FileNotFoundException("No Word documents were found.");
		}
		
		// Post4 PrintWriter
		reportFilename = getReportFilename(getWorkingDirectory());
		outStream = new PrintWriter(reportFilename);
		
		// Post5 RecordManager
		createRecordManager();
	}
	
	/**
	 * Intent: Adjust the working directory for the 'GRADED' sub-directory. 
	 * If the working directory is null, no changes are made.
	 * If 'GRADED' is not found through new FileNotFoundException.
	 * @throws FileNotFoundException
	 */
	private void testTamperPath() throws FileNotFoundException {
		
		if (getWorkingDirectory() != null) {
			setWorkingDirectory(Paths.get(getWorkingDirectory().toString(), 
					"GRADED"));
			// Determine if assignments have been graded
			if (!(Files.isDirectory(getWorkingDirectory()))) {
				throw new FileNotFoundException("Assignments have not "
						+ "been graded.\n\tPlease grade assignments first.");
			}
		}
	}

}
