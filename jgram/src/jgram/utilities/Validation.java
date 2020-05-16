package jgram.utilities;

/**
 * Intent: Provide various validation utility class methods that are used
 * by JGRAM objects.
 *
 */
public class Validation {
	
	/**
     * Intent: Validate whether a given string is numeric or not.
     *
     * @param strNum String to be validated
     * @return boolean
     */
    public static boolean isNumeric(String strNum) {
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

}
