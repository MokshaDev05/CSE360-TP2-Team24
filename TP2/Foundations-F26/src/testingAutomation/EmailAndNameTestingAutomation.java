package testingAutomation;

import validation.EmailRecognizer;
import validation.InputLimits;
import validation.NameRecognizer;

/*******
 * <p> Title: EmailAndNameTestingAutomation Class. </p>
 * 
 * <p> Description: Semi-automated tests for the two remaining recognizers, the one that
 * checks email addresses and the one that checks the four name fields, together with tests
 * of the length limits themselves.</p>
 * 
 * <p> The email tests cover the mistakes people actually make rather than trying to
 * enumerate every address the standards permit.  The name tests cover both the required
 * fields and the optional ones, because an empty value is correct for one and wrong for the
 * other.</p>
 * 
 * <p> Copyright: CSE 360 TP1 Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,
 * Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-22  Initial version for Team Project Phase 1
 */
public class EmailAndNameTestingAutomation {

	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/**********
	 * <p> Method: main </p>
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("______________________________________");
		System.out.println("\nEmail, Name, and Length Testing Automation");

		/************** Email addresses **************/
		System.out.println("\n--- Email address tests ---");

		performEmailTest(1, "user@example.com", true);		// an ordinary address
		performEmailTest(2, "first.last@sub.example.com", true);	// dots either side
		performEmailTest(3, "", false);						// nothing typed
		performEmailTest(4, "userexample.com", false);		// no at sign
		performEmailTest(5, "user@@example.com", false);		// two at signs
		performEmailTest(6, "@example.com", false);			// nothing before the at sign
		performEmailTest(7, "user@", false);					// nothing after the at sign
		performEmailTest(8, "user@example", false);			// no dot in the domain
		performEmailTest(9, "user@example.", false);			// nothing after the last dot
		performEmailTest(10, "user@.example.com", false);	// the domain starts with a dot
		performEmailTest(11, "user@example..com", false);	// two dots together
		performEmailTest(12, "user name@example.com", false);	// a space in the address
		performEmailTest(13, "user@example.c", false);		// the ending is too short
		performEmailTest(14, "user@example.c0m", false);		// a digit in the ending
		performEmailTest(15, "a@b.co", true);				// short but properly formed
		performEmailTest(16, "u".repeat(250) + "@example.com", false);	// past the length limit

		/************** Names **************/
		System.out.println("\n--- Name tests ---");

		performNameTest(17, "Arush", "First name", true, true);
		performNameTest(18, "Anirudh B", "First name", true, true);		// a space is allowed
		performNameTest(19, "Mary-Jane", "First name", true, true);		// a hyphen is allowed
		performNameTest(20, "O'Brien", "Last name", true, true);			// an apostrophe is allowed
		performNameTest(21, "", "First name", true, false);				// required and empty
		performNameTest(22, "", "Middle name", false, true);				// optional and empty
		performNameTest(23, "Ar4sh", "First name", true, false);			// a digit
		performNameTest(24, "-Arush", "First name", true, false);			// starts with a hyphen
		performNameTest(25, "   ", "First name", true, false);			// only spaces
		performNameTest(26, "Arush!", "First name", true, false);			// punctuation that is not allowed
		performNameTest(27, "a".repeat(51), "Last name", true, false);	// past the length limit
		performNameTest(28, "a".repeat(50), "Last name", true, true);		// exactly on the limit

		/************** The length limits themselves **************/
		System.out.println("\n--- Length limit tests ---");

		performLengthTest(29, "a".repeat(16), InputLimits.MAX_USERNAME, "Username", true);
		performLengthTest(30, "a".repeat(17), InputLimits.MAX_USERNAME, "Username", false);
		performLengthTest(31, "a".repeat(64), InputLimits.MAX_PASSWORD, "Password", true);
		performLengthTest(32, "a".repeat(65), InputLimits.MAX_PASSWORD, "Password", false);
		performLengthTest(33, "a".repeat(32), InputLimits.MAX_INVITATION, "Invitation code", true);
		performLengthTest(34, "a".repeat(33), InputLimits.MAX_INVITATION, "Invitation code", false);
		performLengthTest(35, "a".repeat(5000), InputLimits.MAX_PASSWORD, "Password", false);

		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}

	/**********
	 * <p> Method: performEmailTest </p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param inputText    the address to examine
	 * @param expectedPass true when this input is supposed to be accepted
	 */
	private static void performEmailTest(int testCase, String inputText, boolean expectedPass) {
		String resultText = EmailRecognizer.checkForValidEmailAddress(inputText);
		report(testCase, "email address", inputText, resultText, expectedPass);
	}

	/**********
	 * <p> Method: performNameTest </p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param inputText    the name to examine
	 * @param fieldName    the field the name belongs to
	 * @param required     true when the field may not be left empty
	 * @param expectedPass true when this input is supposed to be accepted
	 */
	private static void performNameTest(int testCase, String inputText, String fieldName,
			boolean required, boolean expectedPass) {
		String resultText = NameRecognizer.checkForValidName(inputText, fieldName, required);
		report(testCase, fieldName + (required ? " (required)" : " (optional)"), inputText,
				resultText, expectedPass);
	}

	/**********
	 * <p> Method: performLengthTest </p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param inputText    the text to measure
	 * @param maxLength    the limit being tested
	 * @param fieldName    the field the limit belongs to
	 * @param expectedPass true when this input is supposed to be accepted
	 */
	private static void performLengthTest(int testCase, String inputText, int maxLength,
			String fieldName, boolean expectedPass) {
		String resultText = InputLimits.checkLength(inputText, maxLength, fieldName);
		report(testCase, fieldName + " length", inputText, resultText, expectedPass);
	}

	/**********
	 * <p> Method: report </p>
	 * 
	 * <p> Description: Interprets the message a recognizer returned and reports whether the
	 * outcome was the one the test expected.</p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param what         a description of what was being checked
	 * @param inputText    the input that was examined
	 * @param resultText   the message the recognizer returned
	 * @param expectedPass true when this input is supposed to be accepted
	 */
	private static void report(int testCase, String what, String inputText, String resultText,
			boolean expectedPass) {
		boolean wasAccepted = resultText.isEmpty();

		System.out.println("____________________________________________________________________________");
		System.out.println("Test case: " + testCase + "   Checking: " + what);
		System.out.println("Input: \"" + shorten(inputText) + "\"   Length: " + inputText.length());
		System.out.println("Expected: " + (expectedPass ? "accepted" : "rejected"));

		if (wasAccepted == expectedPass) {
			System.out.println("***Success*** It was " + (wasAccepted ? "accepted" : "rejected")
					+ ", which is what this test expected.");
			if (!wasAccepted)
				System.out.println("Message: " + resultText.trim());
			numPassed++;
		} else {
			System.out.println("***Failure*** It was " + (wasAccepted ? "accepted" : "rejected")
					+ ", but this test expected it to be "
					+ (expectedPass ? "accepted" : "rejected") + ".");
			if (!wasAccepted)
				System.out.println("Message: " + resultText.trim());
			numFailed++;
		}
	}

	/**********
	 * <p> Method: shorten </p>
	 * 
	 * @param input the text to shorten
	 * 
	 * @return the input, or its first twenty characters followed by a note
	 */
	private static String shorten(String input) {
		if (input.length() <= 20)
			return input;
		return input.substring(0, 20) + "... (" + input.length() + " characters)";
	}
}
