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
	
	// Private class variable(s)
	private static ObjectOutputStream outputStream;
	private static ObjectInputStream inputStream;
	
	// Instance variable(s)
	private Path directory;
	private List<Record> recordList;
	
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
			throw new FileNotFoundException("Could not locate DAT record file"
					+ " in GRADED sub-directory.\n\tPlease re-grade "
					+ "assignment(s).");
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
	
	public static void main(String[] args) {
		
		Path directory = Paths.get("/Users/ntadmin/Desktop/tests/run/", "GRADED");
		RecordManager outputManager = new RecordManager(directory);
		
		try {
			outputManager.createOutputStream();
			
			outputManager.writeRecord(1, "GRADED_assignment_sample 1.docx", "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTkxMTI4MzM0LCJzdWIiOiJKR1JBTSIsImlzcyI6IkJVLU1FVCIsIjEtV2VpZ2h0IjozLCIxLUdyYWRlIjo4NSwiMS1GZWVkYmFjayI6IlRocm93cyBBcnJheUluZGV4T3V0T2ZCb3VuZHNFeGNlcHRpb247IHdhdGNoIG91dCBmb3IgdGhlIEJvb2xlYW4gY29uZGl0aW9uIHRoYXQgY29udHJvbHMgdGhlIGZvciBsb29w4oCZcyBleGVjdXRpb24uIFRoaXMgZm9yIGxvb3AgZXhlY3V0ZXMgb25lIG1vcmUgdGltZSB0aGFuIHlvdSB3b3VsZCB3YW50IGl0IHRvIGJlY2F1c2Ugb2YgdGhlIGdyZWF0ZXIgdGhhbiBvciBlcXVhbCB0byBzaWduLiIsIjItV2VpZ2h0IjozLCIyLUdyYWRlIjoxMDAsIjItRmVlZGJhY2siOiJFeGNlbGxlbnQgd29yay4gUGxlYXNlIG1ha2Ugc3VyZSB0byBpbmNsdWRlIGNvbW1lbnRzIG5leHQgdGltZS4iLCIzLVdlaWdodCI6NCwiMy1HcmFkZSI6OTAsIjMtRmVlZGJhY2siOiJQZXIgdGhlIHByb21wdCwgdGhlIG1ldGhvZCBzaG91bGQgcmV0dXJuIHRoZSBmaXJzdCBlbGVtZW50IGluIHRoZSBhcnJheTsgdXNlIHRoZSBicmVhayBrZXl3b3JkIHRvIGV4aXQgdGhlIGZvciBsb29wIG9uY2UgdGhlIGVsZW1lbnQgaXMgZm91bmQuIiwiQ1BJbmRleGVzIjoiWzEsIDIsIDNdIiwiR3JhZGVNYXBwaW5nIjoiQSsgPSA5N1xuQSAgPSA5NVxuQS0gPSA5M1xuQisgPSA4N1xuQiAgPSA4NVxuQi0gPSA4M1xuQyAgPSA3N1xuRiAgPSA2N1xuIiwiVG90YWxHcmFkZSI6OTEuNX0.eN5wZIQFjhYk5J2tImXFi_I2xQnDuXbpD58cZFWUYsY");
			outputManager.writeRecord(2, "GRADED_assignment_sample 2.docx", "fyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTkxMTI4MzM0LCJzdWIiOiJKR1JBTSIsImlzcyI6IkJVLU1FVCIsIjEtV2VpZ2h0IjozLCIxLUdyYWRlIjo4NSwiMS1GZWVkYmFjayI6IlRocm93cyBBcnJheUluZGV4T3V0T2ZCb3VuZHNFeGNlcHRpb247IHdhdGNoIG91dCBmb3IgdGhlIEJvb2xlYW4gY29uZGl0aW9uIHRoYXQgY29udHJvbHMgdGhlIGZvciBsb29w4oCZcyBleGVjdXRpb24uIFRoaXMgZm9yIGxvb3AgZXhlY3V0ZXMgb25lIG1vcmUgdGltZSB0aGFuIHlvdSB3b3VsZCB3YW50IGl0IHRvIGJlY2F1c2Ugb2YgdGhlIGdyZWF0ZXIgdGhhbiBvciBlcXVhbCB0byBzaWduLiIsIjItV2VpZ2h0IjozLCIyLUdyYWRlIjoxMDAsIjItRmVlZGJhY2siOiJFeGNlbGxlbnQgd29yay4gUGxlYXNlIG1ha2Ugc3VyZSB0byBpbmNsdWRlIGNvbW1lbnRzIG5leHQgdGltZS4iLCIzLVdlaWdodCI6NCwiMy1HcmFkZSI6OTAsIjMtRmVlZGJhY2siOiJQZXIgdGhlIHByb21wdCwgdGhlIG1ldGhvZCBzaG91bGQgcmV0dXJuIHRoZSBmaXJzdCBlbGVtZW50IGluIHRoZSBhcnJheTsgdXNlIHRoZSBicmVhayBrZXl3b3JkIHRvIGV4aXQgdGhlIGZvciBsb29wIG9uY2UgdGhlIGVsZW1lbnQgaXMgZm91bmQuIiwiQ1BJbmRleGVzIjoiWzEsIDIsIDNdIiwiR3JhZGVNYXBwaW5nIjoiQSsgPSA5N1xuQSAgPSA5NVxuQS0gPSA5M1xuQisgPSA4N1xuQiAgPSA4NVxuQi0gPSA4M1xuQyAgPSA3N1xuRiAgPSA2N1xuIiwiVG90YWxHcmFkZSI6OTEuNX0.eN5wZIQFjhYk5J2tImXFi_I2xQnDuXbpD58cZFWUYsY");
			outputManager.writeRecord(3, "GRADED_assignment_sample 3.docx", "");
			//gyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTkxMTI4MzM0LCJzdWIiOiJKR1JBTSIsImlzcyI6IkJVLU1FVCIsIjEtV2VpZ2h0IjozLCIxLUdyYWRlIjo4NSwiMS1GZWVkYmFjayI6IlRocm93cyBBcnJheUluZGV4T3V0T2ZCb3VuZHNFeGNlcHRpb247IHdhdGNoIG91dCBmb3IgdGhlIEJvb2xlYW4gY29uZGl0aW9uIHRoYXQgY29udHJvbHMgdGhlIGZvciBsb29w4oCZcyBleGVjdXRpb24uIFRoaXMgZm9yIGxvb3AgZXhlY3V0ZXMgb25lIG1vcmUgdGltZSB0aGFuIHlvdSB3b3VsZCB3YW50IGl0IHRvIGJlY2F1c2Ugb2YgdGhlIGdyZWF0ZXIgdGhhbiBvciBlcXVhbCB0byBzaWduLiIsIjItV2VpZ2h0IjozLCIyLUdyYWRlIjoxMDAsIjItRmVlZGJhY2siOiJFeGNlbGxlbnQgd29yay4gUGxlYXNlIG1ha2Ugc3VyZSB0byBpbmNsdWRlIGNvbW1lbnRzIG5leHQgdGltZS4iLCIzLVdlaWdodCI6NCwiMy1HcmFkZSI6OTAsIjMtRmVlZGJhY2siOiJQZXIgdGhlIHByb21wdCwgdGhlIG1ldGhvZCBzaG91bGQgcmV0dXJuIHRoZSBmaXJzdCBlbGVtZW50IGluIHRoZSBhcnJheTsgdXNlIHRoZSBicmVhayBrZXl3b3JkIHRvIGV4aXQgdGhlIGZvciBsb29wIG9uY2UgdGhlIGVsZW1lbnQgaXMgZm91bmQuIiwiQ1BJbmRleGVzIjoiWzEsIDIsIDNdIiwiR3JhZGVNYXBwaW5nIjoiQSsgPSA5N1xuQSAgPSA5NVxuQS0gPSA5M1xuQisgPSA4N1xuQiAgPSA4NVxuQi0gPSA4M1xuQyAgPSA3N1xuRiAgPSA2N1xuIiwiVG90YWxHcmFkZSI6OTEuNX0.eN5wZIQFjhYk5J2tImXFi_I2xQnDuXbpD58cZFWUYsY

			System.out.println("Records written.");
		} catch (FileNotFoundException e) {
			e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Path directory = Paths.get("/Users/ntadmin/Desktop/tests/run/");
		RecordManager inputManager = new RecordManager(directory);
		
		try {
			inputManager.createInputStream();
			inputManager.createRecordListFromFile();
		
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Record record = inputManager.retrieveRecord("GRADED_assignment_sample 3.docx");
		System.out.println(record.getHashString());
		
		/*
		for (Record record: inputManager.getRecordList()) {
			System.out.println(record);
		}
		*/
			
	}

}
