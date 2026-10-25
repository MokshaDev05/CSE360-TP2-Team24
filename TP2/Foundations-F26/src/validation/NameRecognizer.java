package validation;

/*******
 * <p> Title: NameRecognizer Class. </p>
 * 
 * <p> Description: Decides whether a person's name is acceptable for one of the four name
 * fields the application stores: first name, middle name, last name, and preferred first
 * name.</p>
 * 
 * <p> Names are the one place where being strict does real harm, so the rules here are
 * deliberately permissive.  Letters, spaces, hyphens, apostrophes, and full stops are all
 * allowed, because real names contain them.  What is rejected is input that cannot be part
 * of any name: digits, control characters, and punctuation that only appears when someone
 * is testing what the field will swallow.</p>
 * 
 * <p> Two of the four fields, middle name and preferred first name, may be left empty.
 * The caller says which case applies through the required parameter, so that one class
 * serves all four fields.</p>
 * 
 * <p> Copyright: CSE 360 TP1 Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,
 * Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-22  Initial version for Team Project Phase 1
 */
public class NameRecognizer {

	/** The message describing what went wrong, or an empty string when nothing did. */
	public static String nameErrorMessage = "";

	/** Where the problem was found, or -1 when there was no problem. */
	public static int nameIndexofError = -1;

	/**********
	 * <p> Method: checkForValidName </p>
	 * 
	 * <p> Description: Examines one name and reports the first problem found.</p>
	 * 
	 * @param input     the name to examine
	 * @param fieldName the name of the field, used in the error message, for example
	 *                  "First name"
	 * @param required  true when the field may not be left empty
	 * 
	 * @return an empty string when the name is acceptable, otherwise a message naming what
	 *         is wrong
	 */
	public static String checkForValidName(String input, String fieldName, boolean required) {
		nameErrorMessage = "";
		nameIndexofError = -1;

		if (InputLimits.isEmpty(input)) {
			if (required) {
				nameIndexofError = 0;
				nameErrorMessage = "*** ERROR *** " + fieldName + " is required.";
				return nameErrorMessage;
			}
			// An optional field that was left empty is perfectly acceptable.
			return "";
		}

		// Length is checked before the contents, for the reason given in InputLimits.
		String lengthProblem = InputLimits.checkLength(input, InputLimits.MAX_NAME, fieldName);
		if (!lengthProblem.isEmpty()) {
			nameIndexofError = InputLimits.MAX_NAME;
			nameErrorMessage = "*** ERROR *** " + lengthProblem;
			return nameErrorMessage;
		}

		// A name that is only spaces looks filled in but holds nothing.
		if (input.trim().isEmpty()) {
			nameIndexofError = 0;
			nameErrorMessage = "*** ERROR *** " + fieldName + " cannot be only spaces.";
			return nameErrorMessage;
		}

		// A name has to begin with a letter.  Beginning with a hyphen or an apostrophe is
		// always a typing mistake.
		char firstChar = input.charAt(0);
		if (!isLetter(firstChar)) {
			nameIndexofError = 0;
			nameErrorMessage = "*** ERROR *** " + fieldName + " must start with a letter.";
			return nameErrorMessage;
		}

		for (int index = 0; index < input.length(); index++) {
			char currentChar = input.charAt(index);

			if (isLetter(currentChar))
				continue;

			// Digits are called out separately because it is the mistake people make most
			// often, and a general message would leave them guessing.
			if (currentChar >= '0' && currentChar <= '9') {
				nameIndexofError = index;
				nameErrorMessage = "*** ERROR *** " + fieldName + " may not contain digits.";
				return nameErrorMessage;
			}

			if (currentChar == ' ' || currentChar == '-' || currentChar == '\''
					|| currentChar == '.')
				continue;

			nameIndexofError = index;
			nameErrorMessage = "*** ERROR *** " + fieldName + " may only contain letters, "
					+ "spaces, hyphens, apostrophes, and full stops.";
			return nameErrorMessage;
		}

		return "";
	}

	/**********
	 * <p> Method: isLetter </p>
	 * 
	 * <p> Description: Reports whether a character is one of the twenty six letters in
	 * either case.  The test is written out rather than delegated so that it matches the
	 * character by character style used by the recognizers supplied with the course.</p>
	 * 
	 * @param currentChar the character to examine
	 * 
	 * @return true when the character is A to Z or a to z
	 */
	private static boolean isLetter(char currentChar) {
		return (currentChar >= 'A' && currentChar <= 'Z')
				|| (currentChar >= 'a' && currentChar <= 'z');
	}

	/**********
	 * <p> Method: isValid </p>
	 * 
	 * <p> Description: A convenience wrapper for callers that only need a yes or no.</p>
	 * 
	 * @param input     the name to examine
	 * @param fieldName the name of the field, used in the error message
	 * @param required  true when the field may not be left empty
	 * 
	 * @return true when the name is acceptable
	 */
	public static boolean isValid(String input, String fieldName, boolean required) {
		return checkForValidName(input, fieldName, required).isEmpty();
	}
}
