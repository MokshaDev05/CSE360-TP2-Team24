package testingAutomation;

import validation.InputLimits;
import validation.ScreenInputPolicy;

/*******
 * <p> Title: ScreenValidationTestingAutomation Class. </p>
 * 
 * <p> Description: Semi-automated tests for the validation policy belonging to each screen.</p>
 * 
 * <p> The other three suites test the recognizers one rule at a time.  This one tests
 * ScreenInputPolicy, which holds the sequence of checks a given screen applies and the order it
 * applies them in.  Each case supplies the combination of values a user could type into a named
 * screen and states whether that screen's policy should accept it.</p>
 * 
 * <p> What these tests establish, and what they do not.  They establish that the policy for a
 * screen is correct: that the right checks run, that they run in the right order, and that the
 * boundaries sit where they are supposed to.  They do <b>not</b> establish that a particular
 * button or controller calls that policy, because they call ScreenInputPolicy directly rather
 * than driving the JavaFX user interface.  Exercising the buttons would need a UI test framework,
 * which is outside the scope of this phase, so the wiring between a controller and its policy
 * method is confirmed by reading the controllers and by the manual checks listed in
 * TP1 Test Cases.pdf.</p>
 * 
 * <p> This suite exists because of a defect found late in the sprint.  The recognizers were all
 * correct and all covered, but nothing stated what the login page was supposed to do, so nothing
 * noticed that it did nothing at all: input reached a database query without being examined.
 * Making each screen's intended sequence executable is what closes that gap, and it reduces what
 * has to be checked by hand to one question per screen.</p>
 * 
 * <p> The cases concentrate on the boundaries and on the fields that were missed.  For every field
 * there is a case exactly on the limit, which must be accepted, and a case one character past it,
 * which must be refused; a boundary tested only from one side does not show whether it is off by
 * one.  There is also a very long input for each field, which is the standard way an application
 * is probed for a crash.</p>
 * 
 * <p> Copyright: CSE 360 TP1 Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,
 * Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-22  Initial version for Team Project Phase 1
 */
public class ScreenValidationTestingAutomation {

	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/** A password that satisfies every requirement, used wherever a usable password is needed. */
	private static final String GOOD_PASSWORD = "Abcdef1!";

	/** A username that satisfies every requirement, used wherever a usable username is needed. */
	private static final String GOOD_USERNAME = "one.two";

	/** An invitation code of an acceptable length. */
	private static final String GOOD_INVITATION = "ABC123";

	/**********
	 * <p> Method: main </p>
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("______________________________________");
		System.out.println("\nScreen Validation Testing Automation");

		/*-****************************************************************************************
		 * The login page.  Only the length is checked here, deliberately, so that a rule change
		 * cannot lock out an account that already exists.  Before this suite was written, none of
		 * these three fields was checked at all.
		 */

		// The username, exactly on the limit and one character past it
		performLoginTest(1, "a".repeat(InputLimits.MAX_USERNAME), GOOD_PASSWORD, true);
		performLoginTest(2, "a".repeat(InputLimits.MAX_USERNAME + 1), GOOD_PASSWORD, false);

		// The standard probe: a very long string pasted into the username
		performLoginTest(3, "a".repeat(5000), GOOD_PASSWORD, false);

		// The password, exactly on the limit and one character past it
		performLoginTest(4, GOOD_USERNAME, "a".repeat(InputLimits.MAX_PASSWORD), true);
		performLoginTest(5, GOOD_USERNAME, "a".repeat(InputLimits.MAX_PASSWORD + 1), false);

		// The same probe in the password field
		performLoginTest(6, GOOD_USERNAME, "a".repeat(5000), false);

		/*-****************************************************************************************
		 * The invitation code, typed on the login page and checked again on arrival at the New
		 * Account page.
		 */

		// Exactly on the limit, then one character past it
		performInvitationTest(7, "a".repeat(InputLimits.MAX_INVITATION), true);
		performInvitationTest(8, "a".repeat(InputLimits.MAX_INVITATION + 1), false);

		// The probe, and an empty code, which cannot identify an invitation
		performInvitationTest(9, "a".repeat(5000), false);
		performInvitationTest(10, "", false);

		/*-****************************************************************************************
		 * The New Account page.  The confirmation password box is included because it was not
		 * length checked at all until this suite was written; it was only ever compared.
		 */

		// Everything acceptable
		performNewAccountTest(11, GOOD_INVITATION, GOOD_USERNAME, GOOD_PASSWORD, GOOD_PASSWORD,
				true);

		// The username one character past its limit
		performNewAccountTest(12, GOOD_INVITATION, "a".repeat(InputLimits.MAX_USERNAME + 1),
				GOOD_PASSWORD, GOOD_PASSWORD, false);

		// An invitation code past its limit reaches this page from the login page
		performNewAccountTest(13, "a".repeat(InputLimits.MAX_INVITATION + 1), GOOD_USERNAME,
				GOOD_PASSWORD, GOOD_PASSWORD, false);

		// The confirmation box one character past the password limit, matching the first box
		performNewAccountTest(14, GOOD_INVITATION, GOOD_USERNAME,
				"a".repeat(InputLimits.MAX_PASSWORD + 1),
				"a".repeat(InputLimits.MAX_PASSWORD + 1), false);

		// A usable first password with a very long value pasted into the confirmation box only.
		// This is the case that the old code let through to a string comparison unexamined.
		performNewAccountTest(15, GOOD_INVITATION, GOOD_USERNAME, GOOD_PASSWORD,
				"a".repeat(5000), false);

		// Two usable passwords that simply differ
		performNewAccountTest(16, GOOD_INVITATION, GOOD_USERNAME, GOOD_PASSWORD, "Abcdef2!",
				false);

		/*-****************************************************************************************
		 * The first administrator page.  It has the same confirmation box problem as the New
		 * Account page and no invitation code.
		 */

		// Everything acceptable
		performFirstAdminTest(17, GOOD_USERNAME, GOOD_PASSWORD, GOOD_PASSWORD, true);

		// The confirmation box one character past the limit
		performFirstAdminTest(18, GOOD_USERNAME, "a".repeat(InputLimits.MAX_PASSWORD + 1),
				"a".repeat(InputLimits.MAX_PASSWORD + 1), false);

		// The probe in the confirmation box only
		performFirstAdminTest(19, GOOD_USERNAME, GOOD_PASSWORD, "a".repeat(5000), false);

		// Two usable passwords that differ
		performFirstAdminTest(20, GOOD_USERNAME, GOOD_PASSWORD, "Abcdef2!", false);

		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}

	/**********
	 * <p> Method: performLoginTest </p>
	 * 
	 * <p> Description: Runs one test of the login page's checks, calling the same method the login
	 * screen calls.</p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param username     the username a user would type
	 * @param password     the password a user would type
	 * @param expectedPass true when the login page should accept this pair
	 */
	private static void performLoginTest(int testCase, String username, String password,
			boolean expectedPass) {

		announce(testCase, "Login page", "username \"" + shorten(username) + "\" ("
				+ username.length() + " characters), password of " + password.length()
				+ " characters", expectedPass);

		report(ScreenInputPolicy.validateLogin(username, password), expectedPass);
	}

	/**********
	 * <p> Method: performInvitationTest </p>
	 * 
	 * <p> Description: Runs one test of the invitation code check, calling the same method both the
	 * login page and the New Account page call.</p>
	 * 
	 * @param testCase       the number shown in the report
	 * @param invitationCode the code a user would type
	 * @param expectedPass   true when the code should be accepted as a plausible code
	 */
	private static void performInvitationTest(int testCase, String invitationCode,
			boolean expectedPass) {

		announce(testCase, "Invitation code", "\"" + shorten(invitationCode) + "\" ("
				+ invitationCode.length() + " characters)", expectedPass);

		report(ScreenInputPolicy.validateInvitationCode(invitationCode), expectedPass);
	}

	/**********
	 * <p> Method: performNewAccountTest </p>
	 * 
	 * <p> Description: Runs one test of the New Account page's checks, calling the same sequence
	 * the screen applies.  The screen queries the database in the middle of that sequence to find
	 * out whether the username is taken; this test runs the sequence as though it were not.</p>
	 * 
	 * @param testCase       the number shown in the report
	 * @param invitationCode the code this page was opened with
	 * @param username       the username a user would type
	 * @param password1      the first password a user would type
	 * @param password2      the confirmation password a user would type
	 * @param expectedPass   true when the page should accept these values
	 */
	private static void performNewAccountTest(int testCase, String invitationCode, String username,
			String password1, String password2, boolean expectedPass) {

		announce(testCase, "New Account page", "invitation of " + invitationCode.length()
				+ " characters, username \"" + shorten(username) + "\", password of "
				+ password1.length() + " characters, confirmation of " + password2.length()
				+ " characters", expectedPass);

		report(ScreenInputPolicy.validateNewAccount(invitationCode, username, password1,
				password2), expectedPass);
	}

	/**********
	 * <p> Method: performFirstAdminTest </p>
	 * 
	 * <p> Description: Runs one test of the first administrator page's checks, calling the same
	 * method the screen calls.</p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param username     the username an administrator would type
	 * @param password1    the first password an administrator would type
	 * @param password2    the confirmation password an administrator would type
	 * @param expectedPass true when the page should accept these values
	 */
	private static void performFirstAdminTest(int testCase, String username, String password1,
			String password2, boolean expectedPass) {

		announce(testCase, "First Admin page", "username \"" + shorten(username)
				+ "\", password of " + password1.length() + " characters, confirmation of "
				+ password2.length() + " characters", expectedPass);

		report(ScreenInputPolicy.validateFirstAdmin(username, password1, password2), expectedPass);
	}

	/**********
	 * <p> Method: announce </p>
	 * 
	 * <p> Description: Prints what this test is about to do, so the report can be followed while it
	 * is running.</p>
	 * 
	 * @param testCase     the number shown in the report
	 * @param screenName   the screen whose checks are being exercised
	 * @param description  what is being supplied to that screen
	 * @param expectedPass true when the screen should accept the input
	 */
	private static void announce(int testCase, String screenName, String description,
			boolean expectedPass) {
		System.out.println("____________________________________________________________________________\n");
		System.out.println("Test case: " + testCase);
		System.out.println("Screen: " + screenName);
		System.out.println("Input: " + description);
		System.out.println("Expected: " + (expectedPass ? "accepted" : "rejected"));
	}

	/**********
	 * <p> Method: report </p>
	 * 
	 * <p> Description: Compares what the screen's checks decided with what the test expected, and
	 * counts the result.</p>
	 * 
	 * @param problem      what the checks returned; null means the input was accepted
	 * @param expectedPass true when the input was supposed to be accepted
	 */
	private static void report(ScreenInputPolicy.Problem problem, boolean expectedPass) {

		boolean wasAccepted = problem == null;

		if (wasAccepted == expectedPass) {
			System.out.println("\n***Success*** The input was "
					+ (wasAccepted ? "accepted" : "rejected")
					+ ", which is what this test expected.");
			if (!wasAccepted)
				System.out.println("Message: " + problem.title() + " - " + problem.detail());
			numPassed++;
		} else {
			System.out.println("\n***Failure*** The input was "
					+ (wasAccepted ? "accepted" : "rejected") + ", but this test expected it to be "
					+ (expectedPass ? "accepted" : "rejected") + ".");
			if (!wasAccepted)
				System.out.println("Message: " + problem.title() + " - " + problem.detail());
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
