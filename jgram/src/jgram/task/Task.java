package jgram.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jgram.security.Secret;

/**
 * Intent: The Task class represents one of many tasks the JGRAM program can 
 * complete.
 * Precondition1 (Task Selection): User has selected a task to complete.
 * Postcondition1 (Task Completion): The task is completed and the result is 
 * displayed on the console.
 *
 */
public abstract class Task {
	
	// Instance variable(s)
	private Secret secret;
	private List<Path> fileList;
	private Path workingDirectory;
	private ExecutorService executorService;
	private int threadCount;
	private Scanner keyboard;

	// Constructor(s)
	public Task() {
		fileList = new ArrayList<>();
		executorService = Executors.newFixedThreadPool(10);
	}
	
	public Task(Scanner inputKeyboard) {
		fileList = new ArrayList<>();
		executorService = Executors.newFixedThreadPool(10);
		keyboard = inputKeyboard;
	}
	
	public Task(String userSecret, Scanner inputKeyboard) {
		secret = new Secret(userSecret);
		fileList = new ArrayList<>();
		executorService = Executors.newFixedThreadPool(10);
		keyboard = inputKeyboard;
	}
	
	/**
	 * Intent: To retrieve all directories found at path provided as user
	 * input.
	 * 
	 * Precondition1 (Selected task): User has selected a task that requires
	 * a list of files to execute on.
	 * 
	 * Postcondition1 (Filter Predicate interface object): A Predicate interface
	 * object is created for use with stream filter method and tests for
	 * file names that end with ".docx".
	 * Postcondition2 (Filter Predicate interface object): A Predicate interface
	 * object is created for use with stream filter method and tests for
	 * file names that start with "~".  
	 * Postcondition3 (Create Path stream and store valid documents): A Path
	 * stream is created and valid Path files are extracted from the stream
	 * and stored in fileList.
	 * 
	 * @throws IOException
	 */
	public void createFileList() throws IOException {
		
		// Valid path has been entered
		if (workingDirectory != null) {
			
			// Post1 Filter Predicate interface object
			Predicate<Path> isDocx = (p -> {
				String name = p.getName(p.getNameCount() - 1).toString();
				return name.endsWith(".docx");
			});
			
			// Post2 Filter Predicate interface object
			Predicate<Path> isNotHidden = (p -> {
				String name = p.getName(p.getNameCount() - 1).toString();
				return !name.startsWith("~");
			});
			
			// Post3 Create Path stream and store valid documents in file list
			Stream<Path> pathStream = Files.list(workingDirectory);
			fileList = pathStream.filter(isDocx.and(isNotHidden))
					.collect(Collectors.toList());
			pathStream.close();
		
		} // End outer if
		
	}
	
	/**
	 * Intent: Display Exception messages and current status to the console.
	 * 
	 * Postcondition1 (Error message): The error message is displayed on the
	 * console.
	 * Postcondition2 (Directory name): The working directory name is converted
	 * to a string.
	 * 
	 * @param e
	 */
	public void displayException(Exception e, String message) {
		
		// Post1 Error message
		System.out.println("\nERROR: " + e.getMessage());
		System.out.println("\t" + message);
		
		// Post2 Directory name
		String directory;
		if (workingDirectory != null) {
			directory = workingDirectory.toString();
			System.out.println("\tError occured in the following directory: " 
					+ directory);
		}
		
	}
	
	/**
	 * Intent: A brief description of each task will be displayed to the console.
	 * Precondition1 (Help selection): The user indicated that they would like help.
	 */
	abstract public void displayHelp();
	
	/**
	 * Obtain a Path from the user that represents a directory path on their
	 * file system.
	 * 
	 * Postcondition1 (User input collected): The user has entered a directory
	 * path and it has been validated.
	 * Postcondtion2 (Handle invalid input): The user has entered an invalid 
	 * directory path and has been notified on the console. The user is given 
	 * the choice to enter another directory path or exit.
	 * 
	 * @param keyboard
	 */
	public void getDirectory() {
		
		// Path object to return
		Path path = null;
		
		// Loop until the user enters a directory path or chooses to exit
		boolean notADirectory = true;
		boolean keepGoing = true;
		while (notADirectory && keepGoing) {
								
			System.out.println("Enter a directory: "
					+ "\n\t(Example: /Users/username/Documents/Assignments/):");
			String input = keyboard.nextLine();
			
			// Convert input into a path
			path = Paths.get(input);
			
			if (Files.isDirectory(path)) {
				notADirectory = false;
			
			// Post2 Handle invalid input	
			} else if (input.equals("0")) {
				path = null;
				keepGoing = false;
				
			} else {
				
				String message = "\nYou entered an invalid directory. "
						+ "\n\tPlease enter '0' to exit to the main menu "
						+ "\n\tOR enter a new directory path.";
				System.out.println(message);
			}
			
		}
		
		// Post1 User input collected
		workingDirectory = path;
		
	}
	
	/**
	 * Intent: Get working directory and create a list of valid Word files found
	 * in working directory. Throws FileNotFoundException is no valid files
	 * are found.
	 * @throws IOException
	 */
	public void getDirectoryAndFileList() throws IOException {
		// Post1 Create file list
		getDirectory();
		createFileList();
			
		// Return to main menu if file list was not found
		if (getFileList().isEmpty()) {
			throw new FileNotFoundException("\nNo Word documents were "
					+ "found.");
		}
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}
	
	public List<Path> getFileList() {
		return fileList;
	}
	
	public Scanner getKeyboard() {
		return keyboard;
	}
	
	public Secret getSecret() {
		return secret;
	}
	
	public synchronized int getThreadCount() {
		return threadCount;
	}
	
	public Path getWorkingDirectory() {
		return workingDirectory;
	}
	
	public synchronized void incrementThreadCount() {
		threadCount++;
	}
	
	/**
	 * Intent: Performs task specific to subclass implementation.
	 * Precondition1 (Task Selection)
	 * See EvaluationTask, TamperTask, and NewDocumentTask method documentation
	 * for postconditions.
	 */
	abstract public void performTask();
	
	/**
	 * Intent: Perform preparation operations required to run specific Task 
	 * performTask method.
	 */
	abstract public void prep() throws Exception;
	
	public void setFileList(List<Path> paths) {
		fileList = paths;
	}
	
	public void setKeyboard(Scanner inputKeyboard) {
		keyboard = inputKeyboard;
	}
	
	public void setSecret(Secret uSecret) {
		secret = uSecret;
	}
	
	public void setWorkingDirectory(Path path) {
		workingDirectory = path;
	}
	
}
