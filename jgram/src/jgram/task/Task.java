package jgram.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import jgram.security.Secret;
import jgram.utilities.LinkedList;

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
	private LinkedList<Path> fileList;

	// Constructor(s)
	public Task() {
		fileList = new LinkedList<>();
	}
	
	public Task(String userSecret) {
		secret = new Secret(userSecret);
		fileList = new LinkedList<>();
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
	 * Postcondition2 (Directory contents): A list of the valid
	 * directory contents is created.
	 * Postcondition3 (Parsed list): The directory contents list is parsed 
	 * for valid Word documents and stored in filesList.
	 * 
	 * @throws IOException
	 */
	public void createFileList(String taskType) throws IOException {
		
		// Post1 Obtain directory path
		Scanner keyboard = new Scanner(System.in);
		Path path = getDirectory(keyboard);
		
		// Valid path has been entered
		if (path != null) {
			
			// Test what directory path to obtain
			if (taskType.equals("tamper")) {
				// Get 'GRADED' directory
				path = Paths.get(path.toString(), "GRADED");
				// Determine if assignments have been graded
				if (!(Files.isDirectory(path))) {
					throw new FileNotFoundException("\nAssignments have not "
							+ "been graded. Please grade assignments first.");
				}
			}
			
			// Post2 Directory contents
			DirectoryStream<Path> directoryStream = 
					Files.newDirectoryStream(path);
			
			// Post3 parsed list
			for (Path p : directoryStream) {
				
				// Extract name
				int nameCount = p.getNameCount();
				Path name = p.getName(nameCount - 1);
				
				// Word document
				if (name.toString().endsWith(".docx")) {
					
					// Ignore Word temp files
					if (!(name.toString().startsWith("~"))) {
						getFileList().add(p);
					}
					
				}
			} // End for
			
			// Close resource
			directoryStream.close();
		
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
	 * @return Path object or null if user decides to exit.
	 */
	public Path getDirectory(Scanner keyboard) {
		
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
		return path;
		
	}
	
	public LinkedList<Path> getFileList() {
		return fileList;
	}
	
	public Secret getSecret() {
		return secret;
	}
	
	/**
	 * Intent: Performs task specific to subclass implementation.
	 * Precondition1 (Task Selection)
	 * See EvaluationTask, TamperTask, and NewDocumentTask method documentation
	 * for postconditions.
	 */
	abstract public void performTask();
	
	public void setFileList(LinkedList<Path> paths) {
		fileList = paths;
	}
	
	public void setSecret(Secret uSecret) {
		secret = uSecret;
	}
	
}
