package testingAutomation;

import validation.PasswordRecognizer;

/*******
 * <p> Title: PasswordTestingAutomation Class. </p>
 * 
 * <p> Description: A set of semi-automated tests for the password recognizer, written in
 * the same shape as the PasswordEvaluationTestingAutomation class supplied with the course.
 * Each test states its input and whether that input should be accepted, calls the same
 * recognizer the screens call, and reports whether the outcome matched.</p>
 * 
 * <p> The five tests supplied with the course are kept at the front, including the four
 * that are deliberately written wrongly, because they demonstrate what a test that asserts
 * the wrong thing looks like when it runs.</p>
 * 
 * <p> Copyright: Lynn Robert Carter (c) 2022.  TP1 additions by CSE 360 Team: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 * 
 * @author Lynn Robert Carter
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2022-02-25  A set of semi-automated test cases
 * @version 2.00  2024-09-22  Updated for use at ASU
 * @version 3.00  2026-09-22  Moved onto validation.PasswordRecognizer and extended with the
 *                            cases required for Team Project Phase 1
 */
public class PasswordTestingAutomation {

	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/**********
	 * <p> Method: main </p>
	 * 
	 * <p> Description: Prints a header, runs every test case, then prints a summary.</p>
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("______________________________________");
		System.out.println("\nPassword Testing Automation");

		/************** The five test cases supplied with the course **************/

		// A correctly written test of a password that should be accepted
		performTestCase(1, "Aa!15678", true);

		// A correctly written test of a password that should be rejected
		performTestCase(2, "A!", false);

		// Written wrongly on purpose: this password is valid, but the test claims it is not
		performTestCase(3, "Aa!15678", false);

		// Written wrongly on purpose: this password is invalid, but the test claims it is not
		performTestCase(4, "A!", true);

		// Written wrongly on purpose: an empty password can never be valid
		performTestCase(5, "", true);

		/************** Test cases added for Team Project Phase 1 **************/

		// Every requirement met at exactly the minimum length
		performTestCase(6, "Abcdef1!", true);

		// Everything met except the length
		performTestCase(7, "Abc1!", false);

		// One requirement missing in each of the next four cases
		performTestCase(8, "abcdef1!", false);		// no upper case letter
		performTestCase(9, "ABCDEF1!", false);		// no lower case letter
		performTestCase(10, "Abcdefg!", false);		// no digit
		performTestCase(11, "Abcdefg1", false);		// no special character

		// A longer password that still satisfies everything
		performTestCase(12, "Aa1!Aa1!", true);

		// A space is not in any accepted group, so it is an invalid character
		performTestCase(13, "Aa1! Aa1", false);

		// A letter outside A to Z and a to z is also an invalid character
		performTestCase(14, "Aa1!\u00e9xtra", false);

		// The first and last characters of the accepted special set
		performTestCase(15, "~Aa1bbbb", true);
		performTestCase(16, "/Aa1bbbb", true);

		// The requirement the supplied state diagram does not test.  Before the maximum
		// length rule was added this input was accepted, because nothing rejected an input
		// for being long.  It is the reason the rule exists.
		performTestCase(17, "Aa1!" + "b".repeat(61), false);

		// One character below the limit, to show the limit itself is not off by one
		performTestCase(18, "Aa1!" + "b".repeat(60), true);

		/************** The summary **************/
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
		System.out.println();
		System.out.println("Note: test cases 3, 4, and 5 are written wrongly on purpose and are");
		System.out.println("expected to appear as failures.  Every other case should pass.");
	}

	/**********
	 * <p> Method: performTestCase </p>
	 * 
	 * <p> Description: Runs one test case.  The input is passed to the same recognizer the
	 * screens use, the returned message is interpreted, and the result is reported and
	 * counted.</p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param inputText    the password to examine
	 * @param expectedPass true when this input is supposed to be accepted
	 */
	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {

		System.out.println("____________________________________________________________________________\n");
		System.out.println("Test case: " + testCase);
		System.out.println("Input: \"" + shorten(inputText) + "\"");
		System.out.println("Input length: " + inputText.length());
		System.out.println("Expected: " + (expectedPass ? "accepted" : "rejected"));
		System.out.println("______________");

		String resultText = PasswordRecognizer.evaluatePassword(inputText);
		boolean wasAccepted = resultText.isEmpty();

		System.out.println();

		if (wasAccepted == expectedPass) {
			System.out.println("***Success*** The password <" + shorten(inputText) + "> was "
					+ (wasAccepted ? "accepted" : "rejected") + ", which is what this test expected.");
			if (!wasAccepted)
				System.out.println("Error message: " + resultText);
			numPassed++;
		} else {
			System.out.println("***Failure*** The password <" + shorten(inputText) + "> was "
					+ (wasAccepted ? "accepted" : "rejected") + ", but this test expected it to be "
					+ (expectedPass ? "accepted" : "rejected") + ".");
			if (!wasAccepted)
				System.out.println("Error message: " + resultText);
			numFailed++;
		}

		displayEvaluation();
	}

	/**********
	 * <p> Method: displayEvaluation </p>
	 * 
	 * <p> Description: Prints which of the requirements the last examined password met.
	 * This is the same list the screen shows the user while they type.</p>
	 */
	private static void displayEvaluation() {
		System.out.println("At least one upper case letter - "
				+ satisfied(PasswordRecognizer.foundUpperCase));
		System.out.println("At least one lower case letter - "
				+ satisfied(PasswordRecognizer.foundLowerCase));
		System.out.println("At least one digit - "
				+ satisfied(PasswordRecognizer.foundNumericDigit));
		System.out.println("At least one special character - "
				+ satisfied(PasswordRecognizer.foundSpecialChar));
		System.out.println("At least 8 characters - "
				+ satisfied(PasswordRecognizer.foundLongEnough));
		System.out.println("No more than " + validation.InputLimits.MAX_PASSWORD
				+ " characters - " + satisfied(!PasswordRecognizer.foundTooLong));
	}

	/**********
	 * <p> Method: satisfied </p>
	 * 
	 * @param flag the requirement flag to describe
	 * 
	 * @return the words used in the report for a met or unmet requirement
	 */
	private static String satisfied(boolean flag) {
		return flag ? "Satisfied" : "Not Satisfied";
	}

	/**********
	 * <p> Method: shorten </p>
	 * 
	 * <p> Description: Keeps the report readable when a test uses a deliberately long
	 * input, by showing only the start of it.</p>
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
