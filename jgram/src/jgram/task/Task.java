package jgram.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
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

	// Constructor(s)
	public Task() {
		fileList = new ArrayList<>();
	}
	
	public Task(String userSecret) {
		secret = new Secret(userSecret);
		fileList = new ArrayList<>();
	}
	
	/**
	 * Intent: To retrieve all directories found at path provided as user
	 * input.
	 * 
	 * Precondition1 (Selected task): User has selected a task that requires
	 * a list of files to execute on.
	 * 
	 * Postcondition1 (Obtain directory path): A directory path is obtained 
	 * from the user or a null value is returned if the user wishes to exit.
	 * Postcondition3 (Filter Predicate interface object): A Predicate interface
	 * object is created for use with stream filter method and tests for
	 * file names that end with ".docx".
	 * Postcondition4 (Filter Predicate interface object): A Predicate interface
	 * object is created for use with stream filter method and tests for
	 * file names that start with "~".  
	 * Postcondition4 (Create Path stream and store valid documents): A Path
	 * stream is created and valid Path files are extracted from the stream
	 * and stored in fileList.
	 * 
	 * @throws IOException
	 */
	public void createFileList(String taskType) throws IOException {
		
		// Post1 Obtain directory path
		Scanner keyboard = new Scanner(System.in);
		getDirectory(keyboard);
		
		// Valid path has been entered
		if (workingDirectory != null) {
			
			// Test what directory path to obtain
			testTamperPath(taskType);
			
			// Post2 Filter Predicate interface object
			Predicate<Path> isDocx = (p -> {
				String name = p.getName(p.getNameCount() - 1).toString();
				return name.endsWith(".docx");
			});
			
			// Post3 Filter Predicate interface object
			Predicate<Path> isNotHidden = (p -> {
				String name = p.getName(p.getNameCount() - 1).toString();
				return name.startsWith("~");
			});
			
			// Post4 Create Path stream and store valid documents in file list
			Stream<Path> pathStream = Files.list(workingDirectory);
			fileList = pathStream.filter(isDocx.or(isNotHidden))
					.collect(Collectors.toList());
			pathStream.close();
		
		} // End outer if
		
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
	public void getDirectory(Scanner keyboard) {
		
		// Path object to return
		Path path = null;
		
		// Loop until the user enters a directory path or chooses to exit
		boolean notADirectory = true;
		boolean keepGoing = true;
		while (notADirectory && keepGoing) {
								
			System.out.println("\nPlease enter a directory that contains Word "
					+ "documents."
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
						+ "\n\tOR enter a new directory path: ";
				System.out.println(message);
			}
			
		}
		
		// Post1 User input collected
		workingDirectory = path;
		
	}
	
	public List<Path> getFileList() {
		return fileList;
	}
	
	public Secret getSecret() {
		return secret;
	}
	
	public Path getWorkingDirectory() {
		return workingDirectory;
	}
	
	/**
	 * Intent: Performs task specific to subclass implementation.
	 * Precondition1 (Task Selection)
	 * See EvaluationTask, TamperTask, and NewDocumentTask method documentation
	 * for postconditions.
	 */
	abstract public void performTask();
	
	public void setFileList(List<Path> paths) {
		fileList = paths;
	}
	
	public void setSecret(Secret uSecret) {
		secret = uSecret;
	}
	
	public void setWorkingDirectory(Path path) {
		workingDirectory = path;
	}
	
	/**
	 * Intent: Determine if task to run is a tamper task and adjust the
	 * working directory for the 'GRADED' sub-directory. If 'GRADED' is not
	 * found through new FileNotFoundException.
	 * @param taskType
	 * @throws FileNotFoundException
	 */
	private void testTamperPath(String taskType) throws FileNotFoundException {
		
		// Test is task to execute is a tamper task
		if (taskType.equals("tamper")) {
			// Get 'GRADED' directory
			workingDirectory = Paths.get(workingDirectory.toString(), 
					"GRADED");
			// Determine if assignments have been graded
			if (!(Files.isDirectory(workingDirectory))) {
				throw new FileNotFoundException("Assignments have not "
						+ "been graded.\n\tPlease grade assignments first.");
			}	
		}
	}
	
}
