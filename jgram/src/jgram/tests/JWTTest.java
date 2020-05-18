package jgram.tests;

import jgram.assessment.Document;
import jgram.security.JWT;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JWTTest {
	
	/**
	 * Test that the JWT class correctly encodes and then can decode a hash
	 * string.
	 */
	@Test
	void testEncodeDecode() {
		
		// Simluate document comments
		String[] comment1 = {"0", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=85, FEEDBACK=[Throws ArrayIndexOutOfBoundsException; "
				+ "watch out for the Boolean condition that controls the for "
				+ "loop’s execution. This for loop executes one more time than "
				+ "you would want it to because of the greater than or equal to "
				+ "sign.])"};
		String[] comment2 = {"1", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=100, FEEDBACK=[Excellent work.])"};
		String[] comment3 = {"2", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=4, "
				+ "GRADE=90, FEEDBACK=[Per the prompt, the method should "
				+ "return the first element in the array; use the break "
				+ "keyword to exit the for loop once the element is found.])"};
		String[] comment4 = {"3", "Nikki Tebaldi", "GRADEMAPPING(A+=97, "
				+ "A=95, A-=93, B+=87, B=85, B-=83, C=77, F=67)"};
		String[][] documentComments = {comment1, comment2, comment3, comment4};
		
		// Create a new document and parse it for comments, a grade mapping,
		// checkpoints, and a result
		Document document1 = new Document(documentComments);
		document1.parseComments();
		document1.parseGradeMapping();
		document1.parseCheckpoints();
		document1.calculateResult();
		
		// Create a new JWT object to encode document1
		JWT jwt = new JWT("secret");
		jwt.encode("1", "BU-MET", "JRAM", document1);
		
		// Create a new document for comparison and set its hash string to
		// document1's hash string
		Document document2 = new Document();
		document2.setHashString(document1.getHashString());
		
		// Create a new JWT object and decode document2
		JWT jwt2 = new JWT("secret");
		jwt2.decode(document2);
		
		// Compare document1's encoded result with document2's decoded result
		assertTrue(document1.getResult().equals(document2.getResult()));		
		assertEquals(document1.getGradeMapping(), document2.getGradeMapping());
		
	}

}
