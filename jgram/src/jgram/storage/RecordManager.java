package jgram.storage;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Intent: To manage Record objects.
 * 
 * Postcondition1 (Record written)
 * Postcondition3 (Record retrieved)
 */
public class RecordManager {
	
	// Instance variable(s)
	private Path directory;
	private List<Record> recordList;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	// Constructor(s)
	public RecordManager(Path inputDirectory) {
		directory = inputDirectory;
		recordList = new ArrayList<>();
	}
	
	/**
	 * Intent: Create an object input stream to read record from.
	 * 
	 * Postcondition1 (Object stream created): An object stream is created 
	 * that reads the file 'jgram.dat'.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void createInputStream() throws FileNotFoundException, IOException {
		
		// Post1 Object stream created
		String filename = directory.toString() + "/jgram.dat";
		try {
			inputStream = new ObjectInputStream(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Could not locate DAT record file."
					+ "\n\tPlease re-grade assignment(s).");
		}
	}
	
	/**
	 * Intent: Create an object output stream to write record to.
	 * 
	 * Postcondition1 (GRADED sub-directory): The GRADED sub-directory
	 * is created if it does not exist.
	 * Postcondition2 (Object stream created): An object stream is created 
	 * inside of the 'GRADED' sub-directory and is named 'jgram.dat'.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void createOutputStream() throws FileNotFoundException, IOException {
		
		// Post1 GRADED sub-directory
		String filename = directory.toString() + "/GRADED/jgram.dat";
		Path filePath = Paths.get(filename);
		Path gradedDir = filePath.getParent();
		if (!(Files.isDirectory(gradedDir))) {
			Files.createDirectories(gradedDir);
		}
		
		// Post2 Object stream created
		try {
			outputStream = new ObjectOutputStream(
					new FileOutputStream(filename));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Could not locate record file."
					+ "\n\t Please re-grade assignment(s).");
		}
	}
	
	/**
	 * Intent: Populate recordList with Record objects from jgram.dat file.
	 * 
	 * Precondition1 (ObjectInputStream has been created): An object input 
	 * stream has already been created and contains the file to read 
	 * Record objects from.
	 * 
	 * Postcondition1 (Record list created): A list of Record objects has been
	 * created and is stored in recordList instance variable.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void createRecordListFromFile() throws ClassNotFoundException, 
			IOException {
		
		try {
			// Post 1 Record list created
			while (true) {
					recordList.add( (Record) inputStream.readObject());
			}
		
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("Could not create a record.");
		
		} catch (EOFException e) {
			// Expected exception leaving blank as indicates end of file
		}
	}
	
	public ObjectInputStream getInputStream() {
		return inputStream;
	}
	
	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}
	
	public List<Record> getRecordList() {
		return recordList;
	}
	
	/**
	 * Intent: Search for and retrieve Record object by assignment name.
	 * 
	 * Postcondition1 (Search for record): The record list is searched for and
	 * if a matching record is found it is returned otherwise null is returned.
	 * 
	 * @param assignmentName
	 * @return Record object or null
	 */
	public Record retrieveRecord(String assignmentName) {
		
		Record foundRecord = null;
		
		// Post1 Search for record
		for (Record record: recordList) {
			
			if (record.getAssignmentName().equals(assignmentName)) {
				foundRecord = record;
				break;
			}
			
		}
		
		return foundRecord;
		
	}
	
	/**
	 * Intent: Post1 Record created: A Record object is created and written to
	 * the jgram.dat file.
	 * 
	 * Postcondition1 (Record is written): Record is written by object output
	 * stream.
	 * 
	 * @param id
	 * @param assignmentName
	 * @param hashString
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void writeRecord(int id ,String assignmentName, String hashString) 
			throws IOException {
		
		// Post1 Record is written
		outputStream.writeObject(new Record(id, assignmentName, hashString));
		
	}

}
