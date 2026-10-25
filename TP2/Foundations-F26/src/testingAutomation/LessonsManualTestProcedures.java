package testingAutomation;

import java.util.Scanner;

/*******
 * <p> Title: LessonsManualTestProcedures Class. </p>
 *
 * <p> Description: The manual tests for the Contributor user stories.  They cover what the
 * semi-automated suites cannot: what a window shows and how it responds to a person.  The
 * suites prove the decisions are right (LessonsModelTestingAutomation checks every decision
 * the three lesson Models make); these procedures prove the windows act on those decisions. </p>
 *
 * <p> Running this class prints each procedure in turn and asks the tester to type P (passed),
 * F (failed), or S (skipped) after carrying it out in the running application.  The totals are
 * printed at the end, in the same Pass/Failure style as the other suites, so a manual run can
 * be shown in the TestingScreencast next to the automated ones.  When no console input is
 * available, the procedures are printed and every one is counted as skipped. </p>
 *
 * <p> Set up before the first procedure: start FoundationsMain with an empty database, create
 * the admin account, then invite and create two Role1 accounts, "contribA" and "contribB".
 * MAN-01 to MAN-07 must be run in order, because later procedures use the lessons earlier
 * ones create. </p>
 *
 * <p> Each procedure is one method whose Javadoc gives the requirement, the steps, and the
 * expected result.  The steps the method prints are the same text. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-25  Initial version for Team Project Phase 2
 */
public class LessonsManualTestProcedures {

	/** Count of procedures the tester marked as passed. */
	public static int numPassed = 0;

	/** Count of procedures the tester marked as failed. */
	public static int numFailed = 0;

	/** Count of procedures the tester skipped, or that could not be asked. */
	public static int numSkipped = 0;

	// The console the tester answers on.
	private static Scanner input;


	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public LessonsManualTestProcedures() {
	}


	/**********
	 * <p> Method: main </p>
	 *
	 * <p> Description: Prints each procedure, records the tester's answer, and prints the
	 * totals. </p>
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		numPassed = 0;
		numFailed = 0;
		numSkipped = 0;
		input = new Scanner(System.in);

		System.out.println("------------------------------------------------------------");
		System.out.println("  Lessons Manual Test Procedures (run in the application)");
		System.out.println("------------------------------------------------------------");

		man01EmptyListMessage();
		man02CreateSetsContributor();
		man03RefusedFieldIsNamed();
		man04DeleteAsksFirst();
		man05OtherUserCannotChange();
		man06DeleteSeenByOtherUser();
		man07RestartKeepsLessons();

		System.out.println("  -> passed " + numPassed + ", failed " + numFailed
				+ ", skipped " + numSkipped);
	}


	/**
	 * <p> MAN-01 (CS-2, negative).  An empty list is explained in words on screen. </p>
	 * <p> Steps: log in as contribA; press Lessons Learned. </p>
	 * <p> Expected: the list is empty and the status line under it reads "No lessons have been
	 * recorded yet. Press New Lesson to record the first one." </p>
	 */
	private static void man01EmptyListMessage() {
		procedure("MAN-01 empty list explained on screen (negative, CS-2)",
				"Log in as contribA and press Lessons Learned.",
				"The status line reads: No lessons have been recorded yet. Press New Lesson "
				+ "to record the first one.");
	}

	/**
	 * <p> MAN-02 (CS-1, positive).  A lesson is created from the editor, and the contributor is
	 * taken from the login rather than typed. </p>
	 * <p> Steps: press New Lesson; check the page shows "Contributor: contribA" as a label with
	 * no text field for it; enter title "H2 file lock", category "Database", description "Only
	 * one process can open the file."; press Save Lesson. </p>
	 * <p> Expected: the list shows the new lesson at the top, "(by contribA)", and the status
	 * line reads "Showing 1 of 1 lesson." </p>
	 */
	private static void man02CreateSetsContributor() {
		procedure("MAN-02 new lesson saved with contributor from login (positive, CS-1)",
				"Press New Lesson. Confirm 'Contributor: contribA' is a label, not a field. "
				+ "Enter title 'H2 file lock', category 'Database', description 'Only one "
				+ "process can open the file.' and press Save Lesson.",
				"The lesson is first in the list, shown '(by contribA)', and the status line "
				+ "reads: Showing 1 of 1 lesson.");
	}

	/**
	 * <p> MAN-03 (CS-10, negative).  A refused field is named in an alert and nothing is
	 * saved. </p>
	 * <p> Steps: press New Lesson; leave Title empty, fill Category and Description; press Save
	 * Lesson. </p>
	 * <p> Expected: a "Please Check Your Input" alert headed "The title is required" appears;
	 * after closing it the
	 * editor still shows the typed values; after Cancel the list still shows 1 lesson. </p>
	 */
	private static void man03RefusedFieldIsNamed() {
		procedure("MAN-03 empty title named in an alert, nothing saved (negative, CS-10)",
				"Press New Lesson. Leave Title empty, fill Category and Description, press "
				+ "Save Lesson. Close the alert, then press Cancel.",
				"A 'Please Check Your Input' alert headed 'The title is required' appears; "
				+ "the typed values are still in the "
				+ "editor; after Cancel the list still shows 1 lesson.");
	}

	/**
	 * <p> MAN-04 (CS-8, positive and negative).  Delete asks for confirmation, Cancel keeps the
	 * lesson, and OK removes it. </p>
	 * <p> Steps: create a second lesson "Delete me"; open it; press Delete Lesson; press Cancel
	 * in the dialog; press Delete Lesson again; press OK. </p>
	 * <p> Expected: a dialog headed "Delete lesson #n?" appears both times; after Cancel the
	 * lesson is still shown; after OK an information dialog reads "It has been removed from the
	 * list for everyone." and the list no longer holds "Delete me". </p>
	 */
	private static void man04DeleteAsksFirst() {
		procedure("MAN-04 delete asks first; Cancel keeps, OK removes (positive+negative, CS-8)",
				"Create a lesson 'Delete me' and open it. Press Delete Lesson, then Cancel. "
				+ "Press Delete Lesson again, then OK.",
				"'Delete lesson #n?' appears both times. After Cancel the lesson remains. "
				+ "After OK: 'It has been removed from the list for everyone.' and "
				+ "'Delete me' is gone from the list.");
	}

	/**
	 * <p> MAN-05 (CS-9, negative).  Another user can read a lesson but cannot edit or delete
	 * it, and the page says why. </p>
	 * <p> Steps: log out; log in as contribB; press Lessons Learned; open "H2 file lock". </p>
	 * <p> Expected: every field is shown; Edit Lesson and Delete Lesson are disabled; the note
	 * reads "This lesson was contributed by contribA. Only they can edit or delete it." </p>
	 */
	private static void man05OtherUserCannotChange() {
		procedure("MAN-05 other user sees lesson but cannot edit or delete (negative, CS-9)",
				"Log out, log in as contribB, press Lessons Learned, open 'H2 file lock'.",
				"Fields are shown; Edit Lesson and Delete Lesson are disabled; the note reads: "
				+ "This lesson was contributed by contribA. Only they can edit or delete it.");
	}

	/**
	 * <p> MAN-06 (CS-8, positive).  A deletion by one user is seen by every other user. </p>
	 * <p> Steps: still as contribB, return to the list and search "Delete me". </p>
	 * <p> Expected: no lesson is shown and the status line reads "No lessons match "Delete
	 * me". Press Show All to see every lesson." </p>
	 */
	private static void man06DeleteSeenByOtherUser() {
		procedure("MAN-06 lesson deleted by contribA is gone for contribB (positive, CS-8)",
				"As contribB, go back to the list, type 'Delete me' and press Search.",
				"No lesson is shown; the status line reads: No lessons match \"Delete me\". "
				+ "Press Show All to see every lesson.");
	}

	/**
	 * <p> MAN-07 (CS-12, positive).  Lessons survive quitting and restarting the
	 * application. </p>
	 * <p> Steps: press Quit; start FoundationsMain again; log in as contribA; press Lessons
	 * Learned; open "H2 file lock". </p>
	 * <p> Expected: the lesson is listed and opens with the title, category, description,
	 * contributor, and creation date entered in MAN-02. </p>
	 */
	private static void man07RestartKeepsLessons() {
		procedure("MAN-07 lessons still there after quit and restart (positive, CS-12)",
				"Press Quit. Start the application again, log in as contribA, press Lessons "
				+ "Learned and open 'H2 file lock'.",
				"The lesson is listed and shows the values entered in MAN-02 and its "
				+ "creation date.");
	}


	/**
	 * <p> Method: procedure </p>
	 *
	 * <p> Description: Prints one procedure and records the tester's answer.  Anything other
	 * than P or F, including no answer, counts as skipped, so a procedure is only counted as
	 * passed when the tester said so. </p>
	 *
	 * @param label    the procedure id, what it checks, and the requirement
	 * @param steps    what the tester does
	 * @param expected what the tester should see
	 */
	private static void procedure(String label, String steps, String expected) {
		System.out.println();
		System.out.println("  " + label);
		System.out.println("    Steps:    " + steps);
		System.out.println("    Expected: " + expected);
		System.out.print("    Result (P = pass, F = fail, S = skip): ");

		String answer = "";
		if (input.hasNextLine())
			answer = input.nextLine().trim().toUpperCase();

		if (answer.startsWith("P")) {
			System.out.println("  Pass:    " + label);
			numPassed++;
		} else if (answer.startsWith("F")) {
			System.out.println("  Failure: " + label);
			numFailed++;
		} else {
			System.out.println("  Skipped: " + label);
			numSkipped++;
		}
	}
}
