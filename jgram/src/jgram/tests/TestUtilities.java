package jgram.tests;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import jgram.security.Secret;
import jgram.task.EvaluationTask;
import jgram.task.TamperTestTask;
import jgram.task.Task;

public class TestUtilities {
	
	/**
	 * Extract rows from last table in Word document assignment.
	 * @param gradedFile
	 * @param fileInputStream
	 * @param gradedDoc
	 * @return
	 * @throws IOException
	 */
	public static List<XWPFTableRow> createRowsFromTable(File gradedFile) 
					throws IOException {
		
		// Open XWPF document
		FileInputStream fileInputStream = null;
		XWPFDocument gradedDoc = null;
		
		// Open file as XWPFDocument file
		fileInputStream = new FileInputStream(gradedFile.getAbsolutePath());
		gradedDoc = new XWPFDocument(fileInputStream);
		
		// Get last table in document
		List<XWPFTable> tables = gradedDoc.getTables();
		XWPFTable lastTable = tables.get(tables.size() - 1);
		
		// Get rows of last table
		List<XWPFTableRow> rows = lastTable.getRows();
		
		fileInputStream.close();
		gradedDoc.close();
		
		return rows;
	}

	/**
	 * Intent: Return Path object that represent assignment test file on the
	 * file system.
	 * @return
	 */
	public static Path returnAssignmentPath(String filename) {
		
		String filePath = new File("").getAbsolutePath();
		filePath += "/src/jgram/tests/resources/" + filename;
		Path resourceDocument = Paths.get(filePath);
		
		return resourceDocument;
	}
	
	/**
	 * Intent: Create a mock of Task.createFileList and runs the task's 
	 * performTask method using a test assignment as input.
	 * @param task
	 * @param path
	 * @param taskString
	 * @throws IOException
	 */
	public static void runPerformTask(Task task, Path path, String taskString) 
			throws IOException {
		
		// Mock createFileList
		// Test if task as EvaluationTask object
		if (task instanceof EvaluationTask) {
			task = (EvaluationTask) task;
			task = spy(EvaluationTask.class);
		} else {
			// Task is TamperTestTask object
			task = (TamperTestTask) task;
			task = spy(TamperTestTask.class);
		}
			
		doNothing().when(task).createFileList(taskString);
		
		// Simulate output from Task.createFileList method
		ArrayList<Path> fileList = new ArrayList<>();
		fileList.add(path);
		
		// Run performTask
		Secret secret = new Secret("secret");
		task.setSecret(secret);
		task.setFileList(fileList);
		task.performTask();

	}

}
