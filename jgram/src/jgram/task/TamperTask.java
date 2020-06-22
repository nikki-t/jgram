package jgram.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
	private String reportFilename;
	private Map<Path, String> pathHashMap;
	private RecordManager rm;
	
	// Constructor(s)
	public TamperTask() {
		super();
		pathHashMap = new HashMap<>();
		rm = new RecordManager();
	}
	
	public TamperTask(String userSecret, Scanner inputKeyboard) {
		super(userSecret, inputKeyboard);
		pathHashMap = new HashMap<>();
		rm = new RecordManager();
		
	}
	
	/**
	 * Intent: Create a list of graded file names.
	 * 
	 * Postcondition1 (Loop through files): All files from the Task file list
	 * are iterated over.
	 * Postcondition2 (Extract name): The original file name is extracted for 
	 * any files that begin with the String "GRADED_".
	 * Postcondition3 (Return list): A list of original file names is returned 
	 * to the calling method.
	 * @return
	 */
	private Map<String, Path> createFileMap() {
		
		Map<String, Path> fileMap = new HashMap<>();
		String gradedFile;
		String filename;
		
		// Post1 Loop through files
		for (Path path : getFileList()) {
			
			// Post2 Extract name
			gradedFile = path.getFileName().toString();
			
			if (gradedFile.startsWith("GRADED_")) {
				filename = gradedFile.substring(7);
				fileMap.put(filename, path);
			}
		}
		
		// Post3 Return list
		return fileMap;
		
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
	
	/**
	 * Intent: Retrieve RecordManager object.
	 * 
	 * @return RecordManager
	 */
	public RecordManager getRecordManager() {
		return rm;
	}
	
	/**
	 * Intent: Create 'report.txt' file path.
	 * 
	 * @param path
	 * @return
	 */
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
	 * Postcondition3 (Map loop): All documents found at a path have been
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
						
			// Post3 Map Loop
			for (Map.Entry<Path, String> entry : pathHashMap.entrySet()) {
				
				TamperTaskRun taskRun = new TamperTaskRun(outStream, 
						entry.getValue(), entry.getKey(), getSecret());
				
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
	 * Postcondition5 (Extract a list of file names): A list of original
	 * previously graded file names is extracted from the Task file list and
	 * stored in a map with associated graded file paths.
	 * Postcondition6 (Retrieve hash strings): A map of hash strings associated
	 * with previously graded file names is created.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	@Override
	public void prep() throws IOException, ClassNotFoundException, 
		SQLException {
		
		// Post1 Working directory
		System.out.println("\nChoose a directory with graded Word documents.");
		getDirectory();
		
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
		
		// Post5 Extract a list of file names
		Map<String, Path> fileMap = createFileMap();
		if (fileMap.isEmpty()) {
			throw new FileNotFoundException("Could not find previously "
					+ "graded files.");
		}
		
		// Post6 Retrieve hash strings
		retrieveHashString(fileMap);
		if (pathHashMap.isEmpty()) {
			throw new FileNotFoundException("Could not find any previously "
					+ "graded data.");
		}
	}
	
	/**
	 * Intent: Create a map of previously saved hash strings associated with
	 * specific files.
	 * 
	 * Postcondition1 (RecordManager): A  connection to the JGRAM database is 
	 * opened.
	 * Postcondition2 (Retrieve map): A hash map is created and contains the
	 * graded file path reference as a key and the associated hash string as a
	 * value.
	 * Postcondition3 (Close connection): The connection to the JGRAM database
	 * is closed.
	 * 
	 * @param fileMap
	 * @throws SQLException 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void retrieveHashString(Map<String, Path> fileMap) 
			throws SQLException {
		
		// Post1 RecordManager
		rm.openConnection();
		
		// Post2 Retrieve map
		pathHashMap = rm.createPathHashMap(fileMap);
		
		// Post3 Close connection
		rm.closeConnection();
		
	}
	
	/**
	 * Intent: Set RecordManager object reference.
	 * 
	 * @param inputRM
	 */
	public void setRecordManager(RecordManager inputRM) {
		rm = inputRM;
	}
	
	/**
	 * Intent: Adjust the working directory for the 'GRADED' sub-directory. 
	 * If the working directory is null, no changes are made.
	 * If 'GRADED' is not found through new FileNotFoundException.
	 * 
	 * @throws FileNotFoundException
	 */
	public void testTamperPath() throws FileNotFoundException {
		
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
