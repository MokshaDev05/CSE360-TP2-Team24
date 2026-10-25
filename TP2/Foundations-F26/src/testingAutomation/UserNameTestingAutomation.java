package testingAutomation;

import validation.InputLimits;
import validation.UserNameRecognizer;

/*******
 * <p> Title: UserNameTestingAutomation Class. </p>
 * 
 * <p> Description: Semi-automated tests for the username recognizer the team agreed on
 * after comparing the HW1 submissions.  The rules being tested are that the first
 * character has to be alphabetic, that the body may contain letters and digits, that a
 * full stop, a hyphen, or an ampersand may separate parts of the name but may not be
 * doubled or left at the end, and that the whole name has to be between four and sixteen
 * characters.</p>
 * 
 * <p> Note that the ampersand is an accepted separator and the underscore is not.  That
 * combination is unusual, so there is a test for each of them.</p>
 * 
 * <p> Copyright: CSE 360 TP1 Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,
 * Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-22  Initial version for Team Project Phase 1
 */
public class UserNameTestingAutomation {

	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/**********
	 * <p> Method: main </p>
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("______________________________________");
		System.out.println("\nUser Name Testing Automation");

		// Nothing typed at all
		performTestCase(1, "", false);

		// Shorter than the four character minimum
		performTestCase(2, "ab", false);

		// An ordinary name made only of letters
		performTestCase(3, "Arush", true);

		// Letters followed by digits
		performTestCase(4, "Arush01", true);

		// A digit at the front is rejected, which is the rule the team changed in HW1
		performTestCase(5, "1abcd", false);

		// Each of the three accepted separators
		performTestCase(6, "one.two", true);
		performTestCase(7, "one-two", true);
		performTestCase(8, "one&two", true);

		// The underscore is not one of them
		performTestCase(9, "one_two", false);

		// A separator may not be doubled
		performTestCase(10, "one..two", false);

		// A separator may not be the last character
		performTestCase(11, "onetwo.", false);

		// A separator may not be the first character either, because the first character
		// has to be alphabetic
		performTestCase(12, ".onetwo", false);

		// Exactly on the sixteen character limit, then one over it
		performTestCase(13, "abcdefghijklmnop", true);
		performTestCase(14, "abcdefghijklmnopq", false);

		// A space is not an accepted character
		performTestCase(15, "one two", false);

		// Far longer than any field should accept, which the length gate stops before the
		// recognizer is ever called
		performTestCase(16, "a".repeat(300), false);

		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}

	/**********
	 * <p> Method: performTestCase </p>
	 * 
	 * <p> Description: Runs one test case.  The length gate runs first, exactly as it does
	 * on the screens, and only then is the recognizer called.</p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param inputText    the username to examine
	 * @param expectedPass true when this input is supposed to be accepted
	 */
	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {

		System.out.println("____________________________________________________________________________\n");
		System.out.println("Test case: " + testCase);
		System.out.println("Input: \"" + shorten(inputText) + "\"");
		System.out.println("Input length: " + inputText.length());
		System.out.println("Expected: " + (expectedPass ? "accepted" : "rejected"));

		// The length gate first, for the reason given in InputLimits.
		String resultText = InputLimits.checkLength(inputText, InputLimits.MAX_USERNAME,
				"Username");
		if (resultText.isEmpty())
			resultText = UserNameRecognizer.checkForValidUserName(inputText);

		boolean wasAccepted = resultText.isEmpty();

		if (wasAccepted == expectedPass) {
			System.out.println("\n***Success*** The username <" + shorten(inputText) + "> was "
					+ (wasAccepted ? "accepted" : "rejected") + ", which is what this test expected.");
			if (!wasAccepted)
				System.out.println("Message: " + resultText.trim());
			numPassed++;
		} else {
			System.out.println("\n***Failure*** The username <" + shorten(inputText) + "> was "
					+ (wasAccepted ? "accepted" : "rejected") + ", but this test expected it to be "
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
