package jgram.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

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
	 * Intent: Return Path object that represent assignment directory 
	 * that contains test files on the file system.
	 * @return
	 */
	public static Path returnAssignmentDir(String filename) {
		
		String filePath = new File("").getAbsolutePath();
		filePath += "/src/jgram/tests/resources/" + filename;
		Path resourceDir = Paths.get(filePath).getParent();
		
		return resourceDir;
	}

	/**
	 * Intent: Return Path object that represent assignment test file on the
	 * file system.
	 * @return
	 */
	public static Path returnPath(String filename) {
		
		String filePath = new File("").getAbsolutePath();
		filePath += "/src/jgram/tests/resources/" + filename;
		Path resourceDocument = Paths.get(filePath);
		
		return resourceDocument;
	}

}
