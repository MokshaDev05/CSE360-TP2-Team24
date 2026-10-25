package testingAutomation;

/*******
 * <p> Title: TP1TestRunner Class. </p>
 * 
 * <p> Description: Runs every semi-automated test suite in one go and prints a combined
 * summary at the end, so that the whole set of checks can be demonstrated from a single
 * starting point.</p>
 * 
 * <p> The individual suites can still be run on their own.  This class exists so that the
 * screencast does not need to start six separate programs, and so that a single figure can
 * be quoted for how many checks pass.</p>
 * 
 * <p> Copyright: CSE 360 TP1 Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,
 * Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-22  Initial version for Team Project Phase 1
 */
public class TP1TestRunner {

	/**********
	 * <p> Method: main </p>
	 * 
	 * <p> Description: Runs each suite in turn, then reports the totals.  The deliberately
	 * wrong test cases inherited from the supplied password test class are counted
	 * separately, because they are expected to fail and would otherwise make a correct run
	 * look broken.</p>
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {

		System.out.println("############################################################");
		System.out.println("  TP1 Test Runner");
		System.out.println("  Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,");
		System.out.println("        Moksha Dev");
		System.out.println("############################################################");

		PasswordTestingAutomation.main(args);
		UserNameTestingAutomation.main(args);
		EmailAndNameTestingAutomation.main(args);
		ScreenValidationTestingAutomation.main(args);
		OneTimePasswordTestingAutomation.main(args);

		int totalPassed = PasswordTestingAutomation.numPassed
				+ UserNameTestingAutomation.numPassed
				+ EmailAndNameTestingAutomation.numPassed
				+ ScreenValidationTestingAutomation.numPassed
				+ OneTimePasswordTestingAutomation.numPassed;

		int totalFailed = PasswordTestingAutomation.numFailed
				+ UserNameTestingAutomation.numFailed
				+ EmailAndNameTestingAutomation.numFailed
				+ ScreenValidationTestingAutomation.numFailed
				+ OneTimePasswordTestingAutomation.numFailed;

		// Cases 3, 4, and 5 of the password suite were supplied with the course and assert
		// the wrong outcome on purpose.  They are expected to fail.
		final int expectedFailures = 3;

		System.out.println();
		System.out.println("############################################################");
		System.out.println("  Combined result");
		System.out.println("############################################################");
		System.out.println("  Password suite          passed " + PasswordTestingAutomation.numPassed
				+ ", failed " + PasswordTestingAutomation.numFailed);
		System.out.println("  User name suite         passed " + UserNameTestingAutomation.numPassed
				+ ", failed " + UserNameTestingAutomation.numFailed);
		System.out.println("  Email and name suite    passed "
				+ EmailAndNameTestingAutomation.numPassed
				+ ", failed " + EmailAndNameTestingAutomation.numFailed);
		System.out.println("  Screen validation suite passed "
				+ ScreenValidationTestingAutomation.numPassed
				+ ", failed " + ScreenValidationTestingAutomation.numFailed);
		System.out.println("  One time password suite passed "
				+ OneTimePasswordTestingAutomation.numPassed
				+ ", failed " + OneTimePasswordTestingAutomation.numFailed);
		System.out.println("  ------------------------------------------------------");
		System.out.println("  Total tests  " + (totalPassed + totalFailed));
		System.out.println("  Total passed " + totalPassed);
		System.out.println("  Total failed " + totalFailed
				+ "   (" + expectedFailures + " of these are expected)");

		if (totalFailed == expectedFailures)
			System.out.println("\n  RESULT: every test behaved as expected.");
		else
			System.out.println("\n  RESULT: " + (totalFailed - expectedFailures)
					+ " unexpected failure(s). Look above for the cases marked Failure.");
	}
}
