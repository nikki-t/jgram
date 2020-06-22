package jgram;

import java.util.Scanner;

import jgram.task.AssignmentReportTask;
import jgram.task.EvaluationTask;
import jgram.task.NewDocumentTask;
import jgram.task.TamperTask;
import jgram.task.Task;

public class MainJGRAM {
	
	// Class variable(s)
	private static final String TASK_SELECTION = "Select Task : " 
			+ "\n\t 1 : New Document Test " 
			+ "\n\t 2 : Generate Grade"
			+ "\n\t 3 : Tamper Test" 
			+ "\n\t 4 : Assignment Report"
			+ "\n\t 5 : Help"
			+ "\n\t 6 : Exit"
			+ "\n\t\t (Example: 1): ";
	
	private static final String GET_SECRET = "\nEnter secret (Example: "
			+ "mysecret):";
	private static final String SECRET_REMINDER = "\tMake sure to save this "
			+ "secret somewhere safe "
			+ "\n\tas it is needed for the tamper test.";
	
	/**
	 * Intent: Provide the user with the option to run various evaluation and 
	 * grading tasks. Once the user selects a task, the task is run and the 
	 * result of the task is displayed to the console.
	 * 
	 * Postcondition1 (Task and secret prompt): The user is prompted to select a
	 * task and is then prompted to enter a secret.
	 * Postcondition2 (Task execution): The task the user selected has been 
	 * executed.
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Set up Scanner object
		Scanner keyboard = new Scanner(System.in);
		
		// Print welcome message
		System.out.println("Welcome to JGRAM.");
		
		// Loop until the user indicates they wish to exit
		boolean keepGoing = true;
		while (keepGoing) {
		
			// Post1 Task and secret prompt
			System.out.println("\n---------------------------------[ INPUT ]--"
					+ "-----------------------------------\n");
			String task = prompt(TASK_SELECTION, keyboard);
			
			// Post3 Task execution
			switch (task) {
				
				// New Document
				case "1":
					Task newDocTask = new NewDocumentTask(keyboard);
					newDocTask.performTask();
					break;
				
				// Evaluation
				case "2":
					String evalSecret = prompt(GET_SECRET, keyboard);
					System.out.println(SECRET_REMINDER);
					Task evalTask = new EvaluationTask(evalSecret, keyboard);
					evalTask.performTask();
					break;
				
				// Tamper
				case "3":
					String tamperSecret = prompt(GET_SECRET, keyboard);
					System.out.println(SECRET_REMINDER);
					Task tamperTask = new TamperTask(tamperSecret, keyboard);
					tamperTask.performTask();
					break;
					
				// Report
				case "4":
					Task assignmentReportTask = new AssignmentReportTask(keyboard);
					assignmentReportTask.performTask();
					break;
					
				// Help
				case "5":
					help();
					break;
				
				// Exit
				case "6":
					keepGoing = false;
					break;
				
				// Invalid selection
				default:
					System.out.println("Invalid selection. Please try again.");
					break;
					
			} // End switch
					
			
		} // End while
		
		keyboard.close();
		System.out.println("Goodbye...");
	}
	
	/**
	 * Intent: Create and return a list of tasks.
	 *
	 * @return An array of Task objects
	 */
	private static Task[] createTaskList() {
		
	    Task evalTask = new EvaluationTask();
	    Task tamperTask = new TamperTask();
	    Task newDocTask = new NewDocumentTask();
	    Task reportTask = new AssignmentReportTask();

	    Task[] taskList = {evalTask, tamperTask, newDocTask, reportTask};

	    return taskList;
	} 
	
	
	/**
	 * Intent: Display descriptions of each task on the console.
	 * 
	 * Postcondition1 (Display help): Each task's help description is displayed 
	 * on the console.
	 * 
	 */
	private static void help() {
		
		Task[] taskList = createTaskList();
		
		System.out.println("\n---------------------------------[ HELP ]--------"
				+ "------------------------------\n");
		
		// Post1 Display help (Polymorphism)
		for (Task task : taskList) {
			task.displayHelp();
		}
		
	}
	
	/**
	 * Intent: Display a message to the user and return user's answer.
	 * @param message
	 * @param keyboard Scanner object
	 * @return String of stored user input.
	 */
	public static String prompt(String message, Scanner keyboard) {
		
		System.out.println(message);
		
		String userInput = keyboard.nextLine();
		
		return userInput;		
		
	}
	
}
