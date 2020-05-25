package jgram.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jgram.assessment.Checkpoint;
import jgram.assessment.Document;
import jgram.assessment.GradeMapping;
import jgram.assessment.Result;
import jgram.exceptions.InvalidCheckpointException;
import jgram.exceptions.InvalidGradeMappingException;

/**
 * Intent: Encode and decode a Document's Result and GradeMapping using JSON Web 
 * Tokens.
 * 
 * Postcondition1 (Encode): The Result object and GradeMapping object is encoded
 * and a hash string containing the encoded Result and GradeMapping is returned.
 * Postcondition2 (Decode): The Result object and GradeMapping is decoded from 
 * a hash string and returned.
 */
public class JWT {
	
	// Class constant(s)
	private static final String CLAIM_WEIGHT_PREFIX = "-Weight";
	private static final String CLAIM_GRADE_PREFIX = "-Grade";
	private static final String CLAIM_FEEDBACK_PREFIX = "-Feedback";
	private static final String CLAIM_GRADE_MAPPING = "GradeMapping";
	private static final String CLAIM_TOTAL_CP = "TotalCP";
	private static final String CLAIM_TOTAL_GRADE = "TotalGrade";
	
	// Instance Variable(s)
	private SignatureAlgorithm signatureAlgorithm;
	private String secret;
	
	// Constructor(s)
	public JWT(String uSecret) {
		signatureAlgorithm = SignatureAlgorithm.HS256;
		secret = uSecret;
	}
	
	/**
	 * Intent: Result claims are built and added to the JwtBuilder object.
	 * @param builder
	 * @param result
	 */
	private void buildResultClaim(JwtBuilder builder, Result result) {
		
		// Add Checkpoints to claim
		HashMap<Integer, Checkpoint>  checkpointMap = result.getCheckpointMap();
		for (Map.Entry<Integer, Checkpoint> entry : checkpointMap.entrySet()) {
			
			Integer checkpointID = entry.getKey();
			Checkpoint checkpoint = entry.getValue();
			// Weight
			builder.claim(checkpointID + CLAIM_WEIGHT_PREFIX,
				checkpoint.getWeight());
			// Grade
			builder.claim(checkpointID + CLAIM_GRADE_PREFIX, 
				checkpoint.getGrade());
			// Feedback
			builder.claim(checkpointID + CLAIM_FEEDBACK_PREFIX, 
					checkpoint.getFeedback());
			
		}
	}
	
	/**
	 * Intent: Decode Document hash string to produce a Result object
	 * and GradeMapping object.
	 * 
	 * Precondition1 (Graded assignment): The assignment has already been graded
	 * by JGRAM and a hash string of the results have been created.
	 * 
	 * Postcondition1 (Create Result): A Result object is created from the hash 
	 * string and stored in the Document object.
	 * Postcondition2 (Create GradeMapping): A GradeMapping object is created
	 * from the hash string and stored in the Document object.
	 * @param document Document object
	 */
	public void decode(Document document) throws InvalidCheckpointException, 
			InvalidGradeMappingException {
		
		String jwt = document.getHashString();
		
		// Create parser to parse hash string and set secret as signing key
		Claims claims = Jwts.parser()
			.setSigningKey(DatatypeConverter.parseBase64Binary(secret))
			.parseClaimsJws(jwt)
			.getBody();
		
		// Post1
		document.setResult(decodeResult(claims));
		
		// Post2
		document.setGradeMapping(decodeGradeMapping(claims));
		
	}
	
	/**
	 * Intent: Decode and create GradeMapping object from hash string and
	 * store in Document object.
	 * 
	 * Precondition1 (Claims): Claims have already been parsed from a hash
	 * string.
	 * 
	 * Postcondition1 (GradeMapping): GradeMapping object has been created.
	 * Postcondition2 (Parse claims): Claims have been parsed for grade mapping
	 * and grades have been added to the GradeMapping object.
	 * Postcondition3 (Invalid grade mapping data): Invalid grade mapping data
	 * is detected and an InvalidGradeMappingException is thrown.
	 * Postcondition4 (GradeMapping returned): Complete GradeMapping object
	 * has been returned.
	 * @param document Document object
	 * @param claims Claims object
	 * @return GradeMapping object
	 */
	private GradeMapping decodeGradeMapping(Claims claims) 
			throws InvalidGradeMappingException {
		
		// Post1 GradeMapping
		GradeMapping gradeMapping = new GradeMapping();
		
		// Post2 Parse claims
		String gradeMapString = (String) claims.get(CLAIM_GRADE_MAPPING);
		String[] gradeLines = gradeMapString.split("\\r?\\n");
		
		for (String line : gradeLines) {
			
			try {
			
				String[] grade = line.split("=");
				
				String letter = grade[0].trim();
				int number = Integer.parseInt(grade[1].trim());
				
				gradeMapping.setGrade(letter, number);
			
			// Post3 Invalid grade mapping data
			} catch(ArrayIndexOutOfBoundsException | NumberFormatException 
					| InvalidGradeMappingException e) {
				
				throw new InvalidGradeMappingException();
			}
			
		}
		
		// Post4 GradeMapping returned
		return gradeMapping;
	}
	
	/**
	 * Intent: Decode and create a Result object stored in Document object.
	 * 
	 * Precondition1 (Claims): Claims have already been parsed from a hash
	 * string.
	 * 
	 * Postcondition1 (Checkpoints): Checkpoints have been parsed from claim and 
	 * stored in a Result object.
	 * Postcondition2 (Total grade): The total grade has been parsed from claim
	 * and stored in a Result object.
	 * @param claims Claims object
	 * @return result Result object
	 */
	private Result decodeResult(Claims claims) throws 
			InvalidCheckpointException {
		
		Result result = new Result();
		
		// Post1 Parse checkpoints
		int totalCheckpoints = (Integer) claims.get(CLAIM_TOTAL_CP);
		
		// Loop through checkpoints in claim
		for (int i = 1; i <= totalCheckpoints; i++) {
			// Weight
			int weight = (Integer) claims.get(i + CLAIM_WEIGHT_PREFIX);
			// Grade
			int grade = (Integer) claims.get(i + CLAIM_GRADE_PREFIX);
			// Feedback
			String feedback = (String) claims.get(i + CLAIM_FEEDBACK_PREFIX);
			// Add checkpoint to result
			Checkpoint checkpoint = new Checkpoint(weight, grade, feedback);
			result.addCheckpoint(checkpoint);
		}
		
		// Post2 Total grade
		float totalGrade = Float.parseFloat(claims.get(CLAIM_TOTAL_GRADE)
			.toString());
		result.setTotalGrade(totalGrade);
		
		return result;
		
	}
	
	/**
	 * Precondition1 (User data): User must enter an id, issuer, subject, and
	 * secret to encode the Result object and GradeMapping object of a 
	 * Document object.
	 * 
	 * Postcondition1 (Sign JWT): The JWT is signed with an encoded String 
	 * secret.
	 * Postcondition2 (Claims): JWT claims are set up and contain Result and 
	 * GradeMapping info alongside other data.
	 * Postcondition3 (Compact): The encoded string is created and set as the
	 * hash string for the Document object.
	 * 
	 * @param id ID of JWT
	 * @param issuer Name of JWT issuer
	 * @param subject JWT subject
	 * @param document Document object
	 */
	public void encode(String id, String issuer, String subject, 
		Document document) {
		
		Date date = new Date(System.currentTimeMillis());
		
		// Post1 Sign JWT
		byte[] secretKey = DatatypeConverter.parseBase64Binary(secret);
		Key signingKey = new SecretKeySpec(secretKey, 
				signatureAlgorithm.getJcaName());
		
		// Post2 Build JWT using registered claim names
		JwtBuilder builder = Jwts.builder().setId(id)
			.setIssuedAt(date)
			.setSubject(subject)
			.setIssuer(issuer)
			.signWith(signatureAlgorithm, signingKey);
		
		// Post2 Build JWT with custom claim data
		
		// Result
		Result result = document.getResult();
		buildResultClaim(builder, result);
		
		// GradeMapping
		GradeMapping gradeMapping = document.getGradeMapping();
		builder.claim(CLAIM_GRADE_MAPPING, gradeMapping.toString());
		
		// Total Number of Checkpoints
		builder.claim(CLAIM_TOTAL_CP, result.getCheckpointMap().size());
		
		// Total Grade
		builder.claim(CLAIM_TOTAL_GRADE, result.getTotalGrade());
		
		// Post3 Compact claims into a String and set hashString of Document
		document.setHashString(builder.compact());

	}
	
}
