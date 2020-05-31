package jgram.utilities;

import java.lang.reflect.Array;
import java.util.Iterator;

import jgram.assessment.Checkpoint;
import jgram.exceptions.InvalidCheckpointException;


/**
 * Intent: An efficient implementation of a linked list that supports the 
 * operations needed by JGRAM objects which include adding an element, testing
 * if the list is empty and the use of an enhanced for loop.
 * 
 * Postcondition1 (LinkedList): The LinkedList class can contain objects of any
 * type and supports the mentioned operations.
 * Postcondition2 (Node): The Node class represents elements in the list.
 * Postcondition3 (ListIterator): The ListIterator class represents an iterator
 * of the linked list and supports the use of an enhanced for loop.
 *
 * @param <E>
 * 
 * References:
 * Goodrich, M. T., Tamassia, R., & Goldwasser, M. H. (2014). Data Structures & Algorithms Sixth Edition. Hoboken: John Wiley & Sobns, Inc.
 * Java | Implementing Iterator and Iterable Interface. (n.d.). Retrieved from GeeksForGeeks: https://www.geeksforgeeks.org/java-implementing-iterator-and-iterable-interface/
 * Reflection in Java. (n.d.). Retrieved from GeeksForGeeks: https://www.geeksforgeeks.org/reflection-in-java/

 */
// Post 1 LinkedList
public class LinkedList<E> implements Iterable<E> {
	
	// Instance Variable(s)
	private Node<E> head;    // Node at the front of the list
	private Node<E> tail;    // Node at the end of the list
	private int size = 0;		 // Number of nodes in the list
	
	// Constructor(s)
	public LinkedList() {};
	
	// Instance Method(s)
	/**
	 * Intent: Adds an element as a node to the end of the list.
	 * 
	 * Postcondition1 (Empty list): The list is empty and the element is added
	 * as a node and is referenced by the head node.
	 * Postcondition2 (Added to the end): The element is added to the end of 
	 * the list as a new node.
	 * Postcondition3 (Tail reference): The new node that contains the element
	 * is now referenced by the tail node.
	 * Postcondition4 (Size): The size of the list is increased by one.
	 * 
	 * @param element
	 */
	public void add(E element) {
		
		// Create a new node with element
		Node<E> newNode = new Node<E>(element, null);
		
		// Post1 Empty list
		if (isEmpty()) {
			head = newNode;
		
		} else {
			// Post2 Added to the end
			tail.setNext(newNode);
		}
		
		// Post3 Tail reference
		tail = newNode;

		// Post4 Size
		size++;
		
	}
	
	// Returns the first element in the list
	public E first() {
		return head.getElement();
	}
	
	// Determines if the list does not contain any nodes
	public boolean isEmpty() {
		return size == 0;
	}	
	
	/**
	 * Intent: Return an iterator for the linked list that allows iteration
	 * over list node elements.
	 */
	public Iterator<E> iterator() {
		return new ListIterator();
	}
	
	// Returns the last element in the list
	public E last() {
		return tail.getElement();
	}
	
	// Returns the current size of the list
	public int size() {
		return size;
	}
	
	/**
	 * Intent: Return an array of the linked list nodes.
	 * 
	 * Precondition1 (LinkedList exists): A LinkedList with elements has been 
	 * created.
	 * 
	 * Postcondition1 (New array is created): A new array has been created
	 * and cast to the generic type.
	 * Postcondition2 (Iterator): An iterator for the list has been created.
	 * Postcondition3 (Array filled): The array is filled with all of the node
	 * elements in the linked list.
	 * 
	 * @return E[] or null if linked list is empty
	 */
	@SuppressWarnings("unchecked")
	public E[] toArray() {
		
		// The list is empty
		if (isEmpty()) {
			return null;
		}
		
		// Post1 New array is created 
		E[] array = (E[]) Array.newInstance(first().getClass(), size());
		
		// Post2 Iterator
		Iterator<E> iterator = iterator();
		
		// Post3 Array filled
		int i = 0;
		while (iterator.hasNext()) {
			array[i] = iterator.next();
			i++;
		}
		
		return array;
		
		
	}
	
	/**
	 * Intent: Post2 Node: Represent a node which contains an element and a 
	 * reference to the next element in the linked list. 
	 *
	 * @param <E>
	 */
	private static class Node<E> {
		
		// Instance variable(s)
		private E element;		// List element
		private Node<E> next;	// Reference to next element in the list
		
		// Constructor(s)
		public Node(E inputElement, Node<E> inputNext) {
			element = inputElement;
			next = inputNext;
		}
		
		// Accessor(s)
		public E getElement() {
			return element;
		}
		
		public Node<E> getNext() {
			return next;
		}
		
		// Mutator(s)
		public void setNext(Node<E> inputNext) {
			next = inputNext;
		}
		
	}
	
	/**
	 * Intent: Post3 ListIterator: Represents an iterator that can iterate 
	 * through linked list.
	 * 
	 * The ListIterator class keeps a reference to the current node under
	 * consideration in the current iteration.
	 *
	 */
	private class ListIterator implements Iterator<E> {
		
		// Instance variable(s)
		Node<E> current = head;
		
		// Instance method(s)
		/**
		 * Intent: Determines if the list has another node to iterate over.
		 */
		public boolean hasNext() {
			return current != null;
		}
		
		
		/**
		 * Intent: Return next element in the list.
		 * 
		 * Post condition1 (Element is returned): The element stored with the 
		 * current node reference is returned to the calling method.
		 */
		public E next() {
			
			E element = current.getElement();
			current = current.getNext();
			
			return element;
			
		}
		
		/**
		 * Removing a node or element from the list is not needed for JGRAM and
		 * therefore is not implemented.
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	public static void main(String[] args) {
		
		// Create a checkpoint list
		LinkedList<Checkpoint> checkpointLinkedList = new LinkedList<>();
		
		try {
			// Add Checkpoints to the list
			checkpointLinkedList.add(new Checkpoint(3, 85,"Okay job.", 1));
			checkpointLinkedList.add(new Checkpoint(3, 100, "Excellent job.", 2));
			checkpointLinkedList.add(new Checkpoint(4, 90, "Good job.", 3));
			
			// Print out each checkpoint
			for (Checkpoint checkpoint : checkpointLinkedList) {
				System.out.println(checkpoint);
			}
		
		} catch (InvalidCheckpointException e) {
			System.out.println(e.getMessage());
		}
	}

}
