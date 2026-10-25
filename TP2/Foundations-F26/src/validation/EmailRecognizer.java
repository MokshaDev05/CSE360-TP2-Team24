package validation;

/*******
 * <p> Title: EmailRecognizer Class. </p>
 * 
 * <p> Description: Decides whether an email address is shaped well enough to be worth
 * using.  The address is checked before the Admin screen generates an invitation code for
 * it, because a code sent to an address that cannot receive mail is a code that is simply
 * lost.</p>
 * 
 * <p> The intent here is to catch mistakes, not to prove that a mailbox exists.  Only
 * delivery can prove that.  The rules applied are the ones that catch the errors people
 * actually make: leaving the field blank, forgetting the at sign, typing it twice,
 * leaving one side of it empty, or omitting the dot in the domain.</p>
 * 
 * <p> Copyright: CSE 360 TP1 Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,
 * Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-22  Initial version for Team Project Phase 1
 */
public class EmailRecognizer {

	/** The message describing what went wrong, or an empty string when nothing did. */
	public static String emailErrorMessage = "";

	/** Where the problem was found, or -1 when there was no problem. */
	public static int emailIndexofError = -1;

	/**********
	 * <p> Method: checkForValidEmailAddress </p>
	 * 
	 * <p> Description: Examines one email address and reports the first problem found.</p>
	 * 
	 * @param input the address to examine
	 * 
	 * @return an empty string when the address is acceptable, otherwise a message naming
	 *         what is wrong
	 */
	public static String checkForValidEmailAddress(String input) {
		emailErrorMessage = "";
		emailIndexofError = -1;

		if (InputLimits.isEmpty(input)) {
			emailIndexofError = 0;
			emailErrorMessage = "*** ERROR *** An email address is required.";
			return emailErrorMessage;
		}

		// Length is checked before the shape, for the reason given in InputLimits.
		String lengthProblem = InputLimits.checkLength(input, InputLimits.MAX_EMAIL,
				"Email address");
		if (!lengthProblem.isEmpty()) {
			emailIndexofError = InputLimits.MAX_EMAIL;
			emailErrorMessage = "*** ERROR *** " + lengthProblem;
			return emailErrorMessage;
		}

		// Spaces are the most common paste error, so they are named specifically rather
		// than being swept up by a general message.
		int spaceAt = input.indexOf(' ');
		if (spaceAt >= 0) {
			emailIndexofError = spaceAt;
			emailErrorMessage = "*** ERROR *** An email address may not contain a space.";
			return emailErrorMessage;
		}

		int firstAt = input.indexOf('@');
		int lastAt = input.lastIndexOf('@');

		if (firstAt < 0) {
			emailIndexofError = input.length();
			emailErrorMessage = "*** ERROR *** An email address must contain an at sign.";
			return emailErrorMessage;
		}

		if (firstAt != lastAt) {
			emailIndexofError = lastAt;
			emailErrorMessage = "*** ERROR *** An email address must contain only one at sign.";
			return emailErrorMessage;
		}

		String localPart = input.substring(0, firstAt);
		String domainPart = input.substring(firstAt + 1);

		if (localPart.isEmpty()) {
			emailIndexofError = 0;
			emailErrorMessage = "*** ERROR *** There must be something before the at sign.";
			return emailErrorMessage;
		}

		if (domainPart.isEmpty()) {
			emailIndexofError = firstAt + 1;
			emailErrorMessage = "*** ERROR *** There must be something after the at sign.";
			return emailErrorMessage;
		}

		int dotInDomain = domainPart.indexOf('.');
		if (dotInDomain < 0) {
			emailIndexofError = firstAt + 1;
			emailErrorMessage = "*** ERROR *** The part after the at sign must contain a dot, "
					+ "for example example.com.";
			return emailErrorMessage;
		}

		// A dot at either end of the domain, or two dots together, leaves an empty label,
		// which no real domain has.
		if (domainPart.startsWith(".") || domainPart.endsWith(".")
				|| domainPart.contains("..")) {
			emailIndexofError = firstAt + 1 + dotInDomain;
			emailErrorMessage = "*** ERROR *** The part after the at sign is not a valid "
					+ "domain name.";
			return emailErrorMessage;
		}

		// There must be something after the final dot, and it must be letters.
		String topLevel = domainPart.substring(domainPart.lastIndexOf('.') + 1);
		if (topLevel.length() < 2) {
			emailIndexofError = input.length() - topLevel.length();
			emailErrorMessage = "*** ERROR *** The ending after the last dot is too short.";
			return emailErrorMessage;
		}
		for (int index = 0; index < topLevel.length(); index++) {
			char currentChar = topLevel.charAt(index);
			if (!((currentChar >= 'A' && currentChar <= 'Z')
					|| (currentChar >= 'a' && currentChar <= 'z'))) {
				emailIndexofError = input.length() - topLevel.length() + index;
				emailErrorMessage = "*** ERROR *** The ending after the last dot must be "
						+ "letters only.";
				return emailErrorMessage;
			}
		}

		return "";
	}

	/**********
	 * <p> Method: isValid </p>
	 * 
	 * <p> Description: A convenience wrapper for callers that only need a yes or no.</p>
	 * 
	 * @param input the address to examine
	 * 
	 * @return true when the address is acceptable
	 */
	public static boolean isValid(String input) {
		return checkForValidEmailAddress(input).isEmpty();
	}
}
