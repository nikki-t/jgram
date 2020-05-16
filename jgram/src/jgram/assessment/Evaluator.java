package jgram.assessment;

/**
 * Intent: Represent an assignment evaluator that implements an evaluate method
 * to evaluate and determine the assignment's total grade. 
 *
 * Postcondition1 (Result): The evaluate method returns a Result object that 
 * represents the grade for the assignment.
 */
public interface Evaluator {
	Result evaluate();
}
