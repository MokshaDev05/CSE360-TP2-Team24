package testingAutomation;

import validation.InputLimits;
import validation.OneTimePasswordGenerator;
import validation.PasswordRecognizer;
import validation.ScreenInputPolicy;

/*******
 * <p> Title: OneTimePasswordTestingAutomation Class. </p>
 * 
 * <p> Description: Semi-automated tests for story AD-2, the one time password.</p>
 * 
 * <p> The story has two halves that can be tested without a window or a database, and those are
 * what this suite covers.  The first is the generator: an administrator hands out a password that
 * the application produced, and the claim that such a password always satisfies every requirement
 * is worth demonstrating rather than asserting, because it is generated at random and a generator
 * that is wrong one time in a thousand would otherwise be discovered by a locked out user.  The
 * second is the replacement: when the user is made to choose a new password, that password has to
 * be held to exactly the rules an original password is held to.</p>
 * 
 * <p> Each test below reports the requirement it comes from, the input it used, what was expected,
 * what actually happened, and whether it passed.</p>
 * 
 * <p> What this suite does <b>not</b> cover, and why.  Three steps of the story involve either the
 * database or the user interface: that the temporary password is stored against the right account,
 * that signing in with it forces the change dialog to appear, and that the flag is cleared
 * afterwards.  The supplied Database class has its file location fixed in a constant, so a test
 * that exercised it would write to the developer's own database rather than a temporary one, and
 * the dialog cannot be driven without a window.  Those steps are listed as manual checks in
 * TP1 Test Cases.pdf and were verified by running the application.</p>
 * 
 * <p> Copyright: CSE 360 TP1 Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,
 * Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-23  Initial version for story AD-2
 */
public class OneTimePasswordTestingAutomation {

	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/** How many generated passwords case 1 examines.  A single sample proves very little. */
	private static final int SAMPLE = 1000;

	/**********
	 * <p> Method: main </p>
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("______________________________________");
		System.out.println("\nOne Time Password Testing Automation");

		/*-****************************************************************************************
		 * The generator.  AD-2 says the button "generates a password that satisfies every rule".
		 */

		// 1. Every generated password is accepted by the same recognizer every other password
		//    goes through.  One sample would not show this, so a thousand are examined.
		String firstBadOne = null;
		for (int i = 0; i < SAMPLE && firstBadOne == null; i++) {
			String candidate = OneTimePasswordGenerator.generate();
			if (!PasswordRecognizer.evaluatePassword(candidate).isEmpty())
				firstBadOne = candidate;
		}
		report(1, "AD-2: the generated password satisfies every rule",
				SAMPLE + " freshly generated passwords",
				"every one accepted by PasswordRecognizer",
				firstBadOne == null ? "every one accepted"
						: "one was refused: \"" + firstBadOne + "\"",
				firstBadOne == null);

		// 2. The generated length is inside the limits the application enforces elsewhere.
		String sample = OneTimePasswordGenerator.generate();
		boolean lengthOk = sample.length() >= PasswordRecognizer.MIN_LENGTH
				&& sample.length() <= InputLimits.MAX_PASSWORD;
		report(2, "AD-2: the temporary password is a usable length",
				"one generated password, " + sample.length() + " characters",
				"between " + PasswordRecognizer.MIN_LENGTH + " and " + InputLimits.MAX_PASSWORD,
				sample.length() + " characters",
				lengthOk);

		// 3. Visually ambiguous characters are excluded, because a temporary password is normally
		//    read out or copied by hand.
		String ambiguous = "IlO01";
		String withAmbiguous = null;
		for (int i = 0; i < SAMPLE && withAmbiguous == null; i++) {
			String candidate = OneTimePasswordGenerator.generate();
			for (char c : ambiguous.toCharArray())
				if (candidate.indexOf(c) >= 0) { withAmbiguous = candidate; break; }
		}
		report(3, "AD-2: ambiguous characters are left out of a temporary password",
				SAMPLE + " generated passwords, looking for any of " + ambiguous,
				"none of those characters appears",
				withAmbiguous == null ? "none appeared"
						: "found in \"" + withAmbiguous + "\"",
				withAmbiguous == null);

		// 4. Two generated passwords are not the same, so an administrator issuing several does
		//    not hand out one value repeatedly.
		String a = OneTimePasswordGenerator.generate();
		String b = OneTimePasswordGenerator.generate();
		report(4, "AD-2: each generated password is different",
				"two generated passwords",
				"the two differ",
				a.equals(b) ? "both were \"" + a + "\"" : "they differ",
				!a.equals(b));

		/*-****************************************************************************************
		 * The replacement.  AD-2 says the user must choose a new password, and it has to be held
		 * to the same rules as any other password, including the maximum length.
		 */

		replacement(5, "a valid replacement is accepted", "Abcdef1!", "Abcdef1!", true);
		replacement(6, "a replacement that is too short is refused", "Abc1!", "Abc1!", false);
		replacement(7, "a replacement with no upper case letter is refused",
				"abcdef1!", "abcdef1!", false);
		replacement(8, "a replacement with no digit is refused", "Abcdefg!", "Abcdefg!", false);
		replacement(9, "a replacement with no special character is refused",
				"Abcdefg1", "Abcdefg1", false);
		replacement(10, "a replacement one character past the maximum is refused",
				"Aa1!" + "b".repeat(61), "Aa1!" + "b".repeat(61), false);
		replacement(11, "a replacement exactly on the maximum is accepted",
				"Aa1!" + "b".repeat(60), "Aa1!" + "b".repeat(60), true);
		replacement(12, "a very long pasted replacement is refused",
				"a".repeat(5000), "a".repeat(5000), false);
		replacement(13, "two replacements that do not match are refused",
				"Abcdef1!", "Abcdef2!", false);
		replacement(14, "an empty replacement is refused", "", "", false);

		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}

	/**********
	 * <p> Method: replacement </p>
	 * 
	 * <p> Description: Runs one test of the replacement password check, calling the same method the
	 * forced change dialog calls.</p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param requirement  the part of AD-2 being tested
	 * @param newPassword  the replacement a user would type
	 * @param confirmation the second copy a user would type
	 * @param expectedPass true when the replacement should be accepted
	 */
	private static void replacement(int testCase, String requirement, String newPassword,
			String confirmation, boolean expectedPass) {

		ScreenInputPolicy.Problem problem =
				ScreenInputPolicy.validatePasswordChange(newPassword, confirmation);
		boolean wasAccepted = problem == null;

		String input = "new \"" + shorten(newPassword) + "\", again \"" + shorten(confirmation)
				+ "\"";

		report(testCase, "AD-2: " + requirement, input,
				expectedPass ? "accepted" : "refused",
				wasAccepted ? "accepted" : "refused: " + problem.title(),
				wasAccepted == expectedPass);
	}

	/**********
	 * <p> Method: report </p>
	 * 
	 * <p> Description: Prints one test in the form the team agreed: the requirement it comes from,
	 * the input, what was expected, what actually happened, and the verdict.</p>
	 * 
	 * @param testCase    the number shown in the report
	 * @param requirement the requirement being tested
	 * @param input       what was supplied
	 * @param expected    what should have happened
	 * @param actual      what did happen
	 * @param passed      true when the two agree
	 */
	private static void report(int testCase, String requirement, String input, String expected,
			String actual, boolean passed) {

		System.out.println("____________________________________________________________________________\n");
		System.out.println("Test case:   " + testCase);
		System.out.println("Requirement: " + requirement);
		System.out.println("Input:       " + input);
		System.out.println("Expected:    " + expected);
		System.out.println("Actual:      " + actual);

		if (passed) {
			System.out.println("\n***Success*** PASS");
			numPassed++;
		} else {
			System.out.println("\n***Failure*** FAIL");
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
