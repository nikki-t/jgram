package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import jgram.assessment.Checkpoint;
import jgram.exceptions.InvalidCheckpointException;
import jgram.utilities.LinkedList;

public class LinkedListTest {
	
	/**
	 * Intent: Test the add method of the LinkedList class.
	 */
	@Test
	void testAdd() {
		
		// Create a LinkedList of Checkpoint objects
		LinkedList<Checkpoint> checkpointLinkedList = new LinkedList<>();
		
		// Create a checkpoint object to compare with linked list element
		Checkpoint checkpoint = null;
		try {
			// Add one Checkpoint to the list
			checkpointLinkedList.add(new Checkpoint(3, 85,"Okay job.", 1));
			
			// Create one Checkpoint object
			checkpoint = new Checkpoint(3, 85,"Okay job.", 1);
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoint data.");
		}
		
		// Assert that the Checkpoint object is equal to the Checkpoint object
		// that has been added to the list
		assertEquals(checkpoint, checkpointLinkedList.first());
		
	}
	
	/**
	 * Intent: Test the first method of the LinkedList class.
	 */
	@Test
	void testFirst() {
		
		// Create a LinkedList of Checkpoint objects
		LinkedList<Checkpoint> checkpointLinkedList = new LinkedList<>();
		
		// Create a checkpoint object to compare with linked list element
		Checkpoint checkpoint = null;
		try {
			// Add Checkpoints to the list
			checkpointLinkedList.add(new Checkpoint(3, 85,"Okay job.", 1));
			checkpointLinkedList.add(new Checkpoint(3, 100, "Excellent job.", 2));
			checkpointLinkedList.add(new Checkpoint(4, 90, "Good job,", 3));
			
			// Create one Checkpoint object
			checkpoint = new Checkpoint(3, 85,"Okay job.", 1);
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoint data.");
		}
		
		// Assert that the Checkpoint object is equal to the Checkpoint object
		// that has been added to the list
		assertEquals(checkpoint, checkpointLinkedList.first());
		
	}
	
	/*
	 * Intent: Test the hasNext method of the inner class ListIterator for the
	 *  LinkedList class.
	 */
	@Test
	void testHasNext() {
		
		// Create a LinkedList of Checkpoints
		LinkedList<Checkpoint> checkpointLinkedList = new LinkedList<>();
		try {
			checkpointLinkedList.add(new Checkpoint(3, 85,"Okay job.", 1));
			checkpointLinkedList.add(new Checkpoint(3, 100, "Excellent job.", 2));
			checkpointLinkedList.add(new Checkpoint(4, 90, "Good job,", 3));
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoint data.");
		}
		
		// Create an iterator for the LinkedList
		Iterator<Checkpoint> iterator = checkpointLinkedList.iterator();
		
		// Assert that the iterator does have a next element in the list
		assertTrue(iterator.hasNext());
		
	}
	
	/**
	 * Intent: Test the isEmpty method of the LinkedList class.
	 */
	@Test
	void testIsEmpty() {
		
		// Create a new linked list
		LinkedList<Checkpoint> checkpointLinkedList = new LinkedList<>();
		
		// Assert that the list is empty
		assertTrue(checkpointLinkedList.isEmpty());
		
	}
	
	/**
	 * Intent: Test the first method of the LinkedList class.
	 */
	@Test
	void testLast() {
		
		// Create a LinkedList of Checkpoint objects
		LinkedList<Checkpoint> checkpointLinkedList = new LinkedList<>();
		
		// Create a checkpoint object to compare with linked list element
		Checkpoint checkpoint = null;
		try {
			// Add Checkpoints to the list
			checkpointLinkedList.add(new Checkpoint(3, 85,"Okay job.", 1));
			checkpointLinkedList.add(new Checkpoint(3, 100, "Excellent job.", 2));
			checkpointLinkedList.add(new Checkpoint(4, 90, "Good job,", 3));
			
			// Create one Checkpoint object
			checkpoint = new Checkpoint(4, 90, "Good job,", 3);
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoint data.");
		}
		
		// Assert that the Checkpoint object is equal to the Checkpoint object
		// that has been added to the list
		assertEquals(checkpoint, checkpointLinkedList.last());
		
	}
	
	/**
	 * Intent: Test the next method of the inner class ListIterator for the
	 *  LinkedList class.
	 */
	@Test
	void testNext() {
		
		// Create a LinkedList of Checkpoints
		LinkedList<Checkpoint> checkpointLinkedList = new LinkedList<>();
		try {
			checkpointLinkedList.add(new Checkpoint(3, 85,"Okay job.", 1));
			checkpointLinkedList.add(new Checkpoint(3, 100, "Excellent job.", 2));
			checkpointLinkedList.add(new Checkpoint(4, 90, "Good job,", 3));
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoint data.");
		}
		
		// Create an iterator for the LinkedList
		Iterator<Checkpoint> iterator = checkpointLinkedList.iterator();
		
		// Iterate through the list keep tracking of the iterations
		int count = 0;
		while (iterator.hasNext()) {
			count++;
			iterator.next();
		}
		
		// Assert that the loop iterated 3 times
		assertEquals(3, count);
		
		// Assert that hasNext does not have another element
		assertFalse(iterator.hasNext());
		
	}
	
	/*
	 * Intent: Test the size method of the LinkedList class.
	 */
	@Test
	void testSize() {
		
		// Create a LinkedList of Checkpoint objects
		LinkedList<Checkpoint> checkpointLinkedList = new LinkedList<>();
		try {
			// Add Checkpoints to the list
			checkpointLinkedList.add(new Checkpoint(3, 85,"Okay job.", 1));
			checkpointLinkedList.add(new Checkpoint(3, 100, "Excellent job.", 2));
			checkpointLinkedList.add(new Checkpoint(4, 90, "Good job,", 3));

		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoint data.");
		}
		
		// Assert that the list contains 3 elements
		assertEquals(3, checkpointLinkedList.size());
		
	}
	
	/**
	 * Intent: Test toArray method of LinkedList class.
	 */
	@Test
	void testToArray() {
		
		// Create a LinkedList of Checkpoint objects
		LinkedList<Checkpoint> checkpointLinkedList = new LinkedList<>();
		try {
			// Add Checkpoints to the list
			checkpointLinkedList.add(new Checkpoint(3, 85,"Okay job.", 1));
			checkpointLinkedList.add(new Checkpoint(3, 100, "Excellent job.", 2));
			checkpointLinkedList.add(new Checkpoint(4, 90, "Good job,", 3));

		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoint data.");
		}
		
		// Create a Checkpoint array from LinkedList
		Checkpoint[] checkpointArray = checkpointLinkedList.toArray();
		
		// Assert first elements are the same
		assertEquals(checkpointLinkedList.first(), checkpointArray[0]);
		
		// Assert last elements are the same
		assertEquals(checkpointLinkedList.last(), 
				checkpointArray[checkpointArray.length - 1]);
		
		// Assert both structures are the same size
		assertEquals(checkpointLinkedList.size(), 
				checkpointArray.length);
		
	}
 }
