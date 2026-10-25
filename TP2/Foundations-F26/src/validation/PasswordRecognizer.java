package validation;

/*******
 * <p> Title: PasswordRecognizer Class. </p>
 * 
 * <p> Description: Decides whether a password satisfies the requirements described by the
 * state diagram supplied with the PasswordEvaluationTestbed-F26 application, and adds the
 * one requirement that diagram does not cover.</p>
 * 
 * <p> The rules taken from the supplied diagram are:</p>
 * <ul>
 *   <li>at least one upper case letter, A through Z;</li>
 *   <li>at least one lower case letter, a through z;</li>
 *   <li>at least one digit, 0 through 9;</li>
 *   <li>at least one special character drawn from the set shown in SPECIAL_CHARACTERS;</li>
 *   <li>at least eight characters in total;</li>
 *   <li>no character outside those four groups.</li>
 * </ul>
 * 
 * <p> The requirement the supplied diagram does not test is an upper limit on length.  The
 * diagram walks the input one character at a time and has no state that rejects an input
 * for being too long, so a password of any length is accepted as long as the other rules
 * are met.  That is the weakness described in InputLimits, so this class checks the
 * maximum length before it starts walking the input.  The check is placed first, and not
 * added as another state, because there is no reason to examine a string that is already
 * known to be unacceptable.</p>
 * 
 * <p> This class deliberately has no reference to JavaFX.  The testbed version of this
 * logic lived in a class that also updated labels and colours on screen, which meant the
 * rules could not be exercised without starting a window.  Keeping the rules separate lets
 * the same code be driven by the screens and by the automated tests.</p>
 * 
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP1 additions by CSE 360 Team: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 * 
 * @author Lynn Robert Carter
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-22  Rules lifted from passwordPopUpWindow.Model, JavaFX removed,
 *                            and the maximum length requirement added
 */
public class PasswordRecognizer {

	/**
	 * The exact set of special characters the supplied recognizer accepts.  It is copied
	 * character for character from the testbed so that a password accepted there is
	 * accepted here.
	 */
	public static final String SPECIAL_CHARACTERS = "~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/";

	/** The smallest acceptable password, taken from the supplied diagram. */
	public static final int MIN_LENGTH = 8;

	/*-********************************************************************************************
	 * 
	 * These values describe the result of the most recent call.  The screens read them so
	 * they can show the user which requirements are already met and point at the character
	 * that caused a problem.
	 * 
	 */

	/** The message describing what went wrong, or an empty string when nothing did. */
	public static String passwordErrorMessage = "";

	/** The input that was examined. */
	public static String passwordInput = "";

	/** Where the problem was found, or -1 when there was no problem. */
	public static int passwordIndexofError = -1;

	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;

	/** True when the input was rejected for exceeding the maximum length. */
	public static boolean foundTooLong = false;

	/**********
	 * <p> Method: evaluatePassword </p>
	 * 
	 * <p> Description: Examines one password and reports whether it satisfies every
	 * requirement.  The flags listed above are reset on entry and left describing this
	 * input on exit, so a caller may read them to build a live checklist on screen.</p>
	 * 
	 * @param input the password to examine
	 * 
	 * @return an empty string when the password is acceptable, otherwise a message that
	 *         names what is wrong
	 */
	public static String evaluatePassword(String input) {

		// Start from a clean slate so a previous call cannot influence this one.
		passwordErrorMessage = "";
		passwordIndexofError = 0;
		passwordInput = input == null ? "" : input;
		foundUpperCase = false;
		foundLowerCase = false;
		foundNumericDigit = false;
		foundSpecialChar = false;
		foundLongEnough = false;
		foundTooLong = false;

		if (input == null || input.length() <= 0) {
			passwordErrorMessage = "*** Error *** The password is empty!";
			return passwordErrorMessage;
		}

		// The requirement missing from the supplied diagram.  It is tested before the
		// input is walked, so an oversized string is never processed character by
		// character.
		String lengthProblem = InputLimits.checkLength(input, InputLimits.MAX_PASSWORD,
				"Password");
		if (!lengthProblem.isEmpty()) {
			foundTooLong = true;
			passwordIndexofError = InputLimits.MAX_PASSWORD;
			passwordErrorMessage = "*** Error *** " + lengthProblem;
			return passwordErrorMessage;
		}

		// Walk the input one character at a time, exactly as the supplied diagram does.
		// Each character must belong to one of the four accepted groups; belonging to a
		// group also records that the matching requirement has been met.
		for (int index = 0; index < input.length(); index++) {
			char currentChar = input.charAt(index);

			if (currentChar >= 'A' && currentChar <= 'Z')
				foundUpperCase = true;
			else if (currentChar >= 'a' && currentChar <= 'z')
				foundLowerCase = true;
			else if (currentChar >= '0' && currentChar <= '9')
				foundNumericDigit = true;
			else if (SPECIAL_CHARACTERS.indexOf(currentChar) >= 0)
				foundSpecialChar = true;
			else {
				// A character that belongs to none of the groups stops the examination at
				// once, and the index is kept so the screen can point straight at it.
				passwordIndexofError = index;
				passwordErrorMessage = "*** Error *** An invalid character has been found!";
				return passwordErrorMessage;
			}

			// The eighth character is at index seven, which is where the supplied diagram
			// considers the password long enough.
			if (index >= MIN_LENGTH - 1)
				foundLongEnough = true;
		}

		// Every character was acceptable.  What remains is to report any requirement that
		// never got satisfied, in the same wording the supplied testbed uses.
		String errMessage = "";
		if (!foundUpperCase)
			errMessage += "Upper case; ";
		if (!foundLowerCase)
			errMessage += "Lower case; ";
		if (!foundNumericDigit)
			errMessage += "Numeric digits; ";
		if (!foundSpecialChar)
			errMessage += "Special character; ";
		if (!foundLongEnough)
			errMessage += "Long Enough; ";

		if (errMessage.isEmpty()) {
			passwordIndexofError = -1;
			passwordErrorMessage = "";
			return passwordErrorMessage;
		}

		passwordIndexofError = input.length();
		passwordErrorMessage = errMessage + "conditions were not satisfied";
		return passwordErrorMessage;
	}

	/**********
	 * <p> Method: isValid </p>
	 * 
	 * <p> Description: A convenience wrapper for callers that only need a yes or no.</p>
	 * 
	 * @param input the password to examine
	 * 
	 * @return true when the password satisfies every requirement
	 */
	public static boolean isValid(String input) {
		return evaluatePassword(input).isEmpty();
	}
}
