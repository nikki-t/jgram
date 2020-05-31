package jgram.security;

/**
 * Intent: Represents a secret that is used to encode and decode the results of a 
 * graded assignment.
 *
 * Postcondition1 (Secret is created): Secret object is created and contains
 * a String secret, an id, an issuer and a subject.
 */
public class Secret {
	
	// Class constant(s)
	private static final String ID = "1";
	private static final String ISSUER = "BU-MET";
	private static final String SUBJECT = "JGRAM";
	
	// Instance variable(s)
	private String secretString;
	
	// Constructor(s)
	public Secret(String userSecret) {
		secretString = userSecret;
	}
	
	// Accessor
	public String getID() {
		return ID;
	}
	
	public String getIssuer() {
		return ISSUER;
	}
	
	public String getSecretString() {
		return secretString;
	}
	
	public String getSubject() {
		return SUBJECT;
	}
	
	// Mutator(s)
	public void setSecretString(String userSecret) {
		secretString = userSecret;
	}
	

}
