package jgram.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jgram.assessment.Comment;
import jgram.assessment.Document;
import jgram.assessment.GradeMapping;
import jgram.assessment.Result;
import jgram.exceptions.InvalidCheckpointException;
import jgram.exceptions.InvalidCommentException;
import jgram.exceptions.InvalidGradeMappingException;
import jgram.security.JWT;
import jgram.security.Secret;

class JWTTest {
	
	/**
	 * Test that the JWT class correctly encodes and then can decode a hash
	 * string.
	 */
	@Test
	void testEncodeDecode() {
		
		// Simulate document comments
		Comment comment1 = new Comment("0", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=85, FEEDBACK=[Throws ArrayIndexOutOfBoundsException; "
				+ "watch out for the Boolean condition that controls the for "
				+ "loop’s execution. This for loop executes one more time than "
				+ "you would want it to because of the greater than or equal to "
				+ "sign.])");
		Comment comment2 = new Comment("1", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=3, "
				+ "GRADE=100, FEEDBACK=[Excellent work.])");
		Comment comment3 = new Comment("2", "Nikki Tebaldi", "CHECKPOINT(WEIGHT=4, "
				+ "GRADE=90, FEEDBACK=[Per the prompt, the method should "
				+ "return the first element in the array; use the break "
				+ "keyword to exit the for loop once the element is found.])");
		Comment comment4 = new Comment("3", "Nikki Tebaldi", "GRADEMAPPING(A+=97, "
				+ "A=95, A-=93, B+=87, B=85, B-=83, C=77, F=67)");
		List<Comment> documentComments = new ArrayList<>();
		documentComments.add(comment1);
		documentComments.add(comment2);
		documentComments.add(comment3);
		documentComments.add(comment4);
		
		try {
			
			// Create a new document and set assignment path
			Document document1 = new Document();
			Path path = TestUtilities
					.returnPath("/document/document-test.docx");
			document1.setAssignmentPath(path);
			
			// Set the comments list and a grade mapping
			document1.setCommentList(documentComments);
			GradeMapping gradeMapping = new GradeMapping();
			gradeMapping.setDefaultGradeMapping();
			document1.setGradeMapping(gradeMapping);
			
			// Parse the document for checkpoints and calculate total grade
			document1.parseCheckpoints();
			document1.calculateResult();
		
			// Create a new JWT object to encode document1
			Secret secret = new Secret("secret");
			JWT jwt = new JWT(secret);
			jwt.encode(document1);
			
			// Create a new document for comparison and set its hash string to
			// document1's hash string
			Document document2 = new Document();
			Result result2 = new Result(document1.getResult().getHashString());
			document2.setResult(result2);
			
			// Create a new JWT object and decode document2
			JWT jwt2 = new JWT(secret);
			jwt2.decode(document2);
		
			// Compare document1's encoded result with document2's decoded result
			assertTrue(document1.getResult().equals(document2.getResult()));		
			assertEquals(document1.getGradeMapping(), document2.getGradeMapping());
		
		} catch (InvalidGradeMappingException e) {
			fail("Invalid grade mapping detected.");
		
		} catch (InvalidCheckpointException e) {
			fail("Invalid checkpoints created.");
		
		} catch(InvalidCommentException e) {
			fail("Invalid comment detected.");
		}
		
	}

}
