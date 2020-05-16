package jgram;

import java.util.Scanner;

import jgram.task.EvaluationTask;
import jgram.task.NewDocumentTask;
import jgram.task.TamperTestTask;
import jgram.task.Task;

public class MainJGRAM {
	
	/**
	 * Intent: Provide the user with the option to run various evaluation and 
	 * grading tasks. Once the user selects a task, the task is run and the 
	 * result of the task is displayed to the console.
	 * 
	 * Postcondition1 (Task List): Create a list of tasks
	 * Postcondition2 (Prompt): Prompt the user for task selection.
	 * Postcondition3 (Task Execution): Execute the task the user selected.
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Post1
		Task evalTask = new EvaluationTask();
		Task tamperTask = new TamperTestTask();
		Task newDocTask = new NewDocumentTask();
		Task[] taskList = {evalTask, tamperTask, newDocTask};
		
		System.out.println("Welcome to JGRAM.");
		
		// Loop until the user indicates they wish to exit
		boolean keepGoing = true;
		while (keepGoing) {
		
			// Post2
			System.out.println("\n---------------------------------[ INPUT ]-------------------------------------\n");
			String task = prompt("Select Task : " 
					+ "\n\t 1 : New Document Test " 
					+ "\n\t 2 : Generate Grade"
					+ "\n\t 3 : Tamper Test" 
					+ "\n\t 4 : Help"
					+ "\n\t 5 : Exit"
					+ "\n\t\t (Example: 1): ");
			
			// Post3 (Downcasting)
			String secret = "";
			switch (task) {
				
				// New Document
				case "1":
					newDocTask.performTask();
					break;
				
				// Evaluation
				case "2":
					// Set secret for evaluation task
					secret = prompt("\nEnter secret (Example: mysecret):");
					System.out.println("\tMake sure to save this secret "
							+ "somewhere safe \n\tas it is needed for the tamper "
							+ "test.\n");
					if (evalTask instanceof EvaluationTask) {
						((EvaluationTask) evalTask).setSecret(secret);
						evalTask.performTask();
					} else {
						System.out.println("Invalid evaluation task.");
					}
					break;
				
				// Tamper
				case "3":
					// Set secret for tamper task
					secret = prompt("\nEnter secret (Example: mysecret):");
					System.out.println("\tMake sure to save this secret "
							+ "somewhere safe \n\tas it is needed for the tamper "
							+ "test.\n");
					if (tamperTask instanceof TamperTestTask) {
						((TamperTestTask) tamperTask).setSecret(secret);
						tamperTask.performTask();
					} else {
						System.out.println("Invalid tamper test task.");
					}
					break;
				
				// Help
				case "4":
					help(taskList);
					break;
				
				// Exit
				case "5":
					keepGoing = false;
					break;
				
				// Invalid selection
				default:
					System.out.println("Invalid selection. Please try again.");
					break;
					
			} // End switch
					
			
		} // End while
		System.out.println("Goodbye...");
	} 
	
	
	/**
	 * Intent: Display a message to the user and return user's answer.
	 * @param message
	 * @return String of stored user input.
	 */
	private static String prompt(String message) {
		
		Scanner keyboard = new Scanner(System.in);
		
		System.out.println(message);
		
		String userInput = keyboard.next();
		
		return userInput;		
		
	}
	
	/**
	 * Intent: Display descriptions of each task on the console.
	 * 
	 * Postcondition1 (Display help): Display each tasks help description.
	 * 
	 * @param taskList : List of Task objects
	 */
	private static void help(Task[] taskList) {
		
		System.out.println("\n---------------------------------[ HELP ]--------------------------------------\n");
		
		// Post1 (Polymorphism)
		for (Task task: taskList) {
			task.displayHelp();
		}
		
	}
	
}
