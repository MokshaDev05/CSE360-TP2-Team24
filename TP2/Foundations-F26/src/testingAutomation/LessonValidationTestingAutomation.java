package testingAutomation;

import validation.LessonInputPolicy;
import validation.LessonInputPolicy.Problem;

/*******
 * <p> Title: LessonValidationTestingAutomation Class. </p>
 *
 * <p> Description: Semi-automated tests for {@link validation.LessonInputPolicy}, the rules
 * that LessonsLearned.create and LessonsLearned.update apply before anything is written.  The
 * suite covers two Contributor requirements: </p>
 * <ul>
 *   <li>CS-10: title, category, description, and contributor are required; resolution and
 *       keywords are optional.  An empty or whitespace-only required value is refused with a
 *       message that names the field.</li>
 *   <li>CS-11: title 80, category 30, description 2000, resolution 2000, keywords 200
 *       characters, at most 10 keywords of at most 30 characters each.  A value over its limit
 *       is refused with a message that names the field.</li>
 * </ul>
 *
 * <p> Each test is one private method, named after its test id, whose Javadoc states the
 * requirement, the input used, and the condition for a Pass.  Boundary values are tested on
 * both sides: the largest accepted value is a positive test and one character more is a
 * negative test.  A negative test passes only when the refusal carries the expected problem
 * title, so a refusal for the wrong reason is reported as a Failure. </p>
 *
 * <p> How to read the output: one line per test, "Pass:" or "Failure:", followed by the test
 * id, what was checked, and the requirement.  The last line gives the totals for the suite.
 * Every id appears in "Requirements to Tests Map.pdf". </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-14  Initial version for Team Project Phase 2
 * @version 1.10  2026-10-25  One method per test; negative tests check the problem title
 */
public class LessonValidationTestingAutomation {

	/** Count of tests that produced the expected result in this suite. */
	public static int numPassed = 0;

	/** Count of tests that did not produce the expected result in this suite. */
	public static int numFailed = 0;

	// A realistic value for each field, so that a test changing one field leaves the others
	// valid and any refusal can only come from the field under test.
	private static final String TITLE = "H2 file lock prevents a second connection";
	private static final String CATEGORY = "Database";
	private static final String DESCRIPTION =
			"The H2 embedded engine locks the database file to a single JVM process.";
	private static final String RESOLUTION =
			"Append ;AUTO_SERVER=TRUE to the JDBC URL to allow a second connection.";
	private static final String KEYWORDS = "H2, JDBC, lock";
	private static final String CONTRIBUTOR = "arushugal";


	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public LessonValidationTestingAutomation() {
	}


	/**********
	 * <p> Method: main </p>
	 *
	 * <p> Description: Resets the counters, runs every validation test in id order, and prints
	 * the suite totals.  TP2TestRunner calls this method, and it can also be run on its own. </p>
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		numPassed = 0;
		numFailed = 0;

		System.out.println("------------------------------------------------------------");
		System.out.println("  Lesson Validation Tests (CS-10, CS-11)");
		System.out.println("------------------------------------------------------------");

		val01TitleAccepted();
		val02BlankTitleRefused();
		val03WhitespaceTitleRefused();
		val04TitleAt80Accepted();
		val05TitleAt81Refused();
		val06CategoryAccepted();
		val07BlankCategoryRefused();
		val08CategoryAt30Accepted();
		val09CategoryAt31Refused();
		val10DescriptionAccepted();
		val11BlankDescriptionRefused();
		val12DescriptionAt300Accepted();
		val13DescriptionAt2000Accepted();
		val14DescriptionAt2001Refused();
		val15EmptyResolutionAccepted();
		val16ResolutionAt2000Accepted();
		val17ResolutionAt2001Refused();
		val18EmptyKeywordsAccepted();
		val19ValidKeywordsAccepted();
		val20ElevenKeywordsRefused();
		val21LongKeywordRefused();
		val22ContributorAccepted();
		val23BlankContributorRefused();
		val24FullLessonAccepted();
		val25FullLessonWithBlankTitleRefused();

		System.out.println("  -> passed " + numPassed + ", failed " + numFailed);
	}


	/*-*******************************************************************************************
	 *
	 * Title
	 *
	 */

	/**
	 * <p> VAL-01 (CS-10, positive).  A normal title is accepted. </p>
	 * <p> How: validateTitle is given a 41-character title.  It returns null only when the
	 * title is present and within 80 characters. </p>
	 * <p> Pass when: validateTitle returns null. </p>
	 */
	private static void val01TitleAccepted() {
		check("VAL-01 title accepted (positive, CS-10)",
				LessonInputPolicy.validateTitle(TITLE) == null);
	}

	/**
	 * <p> VAL-02 (CS-10, negative).  An empty title is refused. </p>
	 * <p> How: validateTitle is given "". </p>
	 * <p> Pass when: the problem returned is "The title is required". </p>
	 */
	private static void val02BlankTitleRefused() {
		check("VAL-02 blank title refused (negative, CS-10)",
				refusedWith(LessonInputPolicy.validateTitle(""), "The title is required"));
	}

	/**
	 * <p> VAL-03 (CS-10, negative).  A title of spaces only is refused. </p>
	 * <p> How: validateTitle is given three spaces.  This catches a check that tests for an
	 * empty string but forgets to trim, which would accept a title the list shows as blank. </p>
	 * <p> Pass when: the problem returned is "The title is required". </p>
	 */
	private static void val03WhitespaceTitleRefused() {
		check("VAL-03 whitespace-only title refused (negative, CS-10)",
				refusedWith(LessonInputPolicy.validateTitle("   "), "The title is required"));
	}

	/**
	 * <p> VAL-04 (CS-11, positive).  A title of exactly 80 characters is accepted. </p>
	 * <p> How: validateTitle is given 80 copies of "x", the largest value the limit allows. </p>
	 * <p> Pass when: validateTitle returns null. </p>
	 */
	private static void val04TitleAt80Accepted() {
		check("VAL-04 title at 80 chars accepted (positive, CS-11)",
				LessonInputPolicy.validateTitle("x".repeat(80)) == null);
	}

	/**
	 * <p> VAL-05 (CS-11, negative).  A title of 81 characters is refused. </p>
	 * <p> How: validateTitle is given 81 copies of "x", one past the limit. </p>
	 * <p> Pass when: the problem returned is "The title is too long". </p>
	 */
	private static void val05TitleAt81Refused() {
		check("VAL-05 title at 81 chars refused (negative, CS-11)",
				refusedWith(LessonInputPolicy.validateTitle("x".repeat(81)),
						"The title is too long"));
	}


	/*-*******************************************************************************************
	 *
	 * Category
	 *
	 */

	/**
	 * <p> VAL-06 (CS-10, positive).  A normal category is accepted. </p>
	 * <p> How: validateCategory is given "Database". </p>
	 * <p> Pass when: validateCategory returns null. </p>
	 */
	private static void val06CategoryAccepted() {
		check("VAL-06 category accepted (positive, CS-10)",
				LessonInputPolicy.validateCategory(CATEGORY) == null);
	}

	/**
	 * <p> VAL-07 (CS-10, negative).  An empty category is refused. </p>
	 * <p> How: validateCategory is given "". </p>
	 * <p> Pass when: the problem returned is "The category is required". </p>
	 */
	private static void val07BlankCategoryRefused() {
		check("VAL-07 blank category refused (negative, CS-10)",
				refusedWith(LessonInputPolicy.validateCategory(""), "The category is required"));
	}

	/**
	 * <p> VAL-08 (CS-11, positive).  A category of exactly 30 characters is accepted. </p>
	 * <p> How: validateCategory is given 30 copies of "x". </p>
	 * <p> Pass when: validateCategory returns null. </p>
	 */
	private static void val08CategoryAt30Accepted() {
		check("VAL-08 category at 30 chars accepted (positive, CS-11)",
				LessonInputPolicy.validateCategory("x".repeat(30)) == null);
	}

	/**
	 * <p> VAL-09 (CS-11, negative).  A category of 31 characters is refused. </p>
	 * <p> How: validateCategory is given 31 copies of "x". </p>
	 * <p> Pass when: the problem returned is "The category is too long". </p>
	 */
	private static void val09CategoryAt31Refused() {
		check("VAL-09 category at 31 chars refused (negative, CS-11)",
				refusedWith(LessonInputPolicy.validateCategory("x".repeat(31)),
						"The category is too long"));
	}


	/*-*******************************************************************************************
	 *
	 * Description
	 *
	 */

	/**
	 * <p> VAL-10 (CS-10, positive).  A normal description is accepted. </p>
	 * <p> How: validateDescription is given a one-sentence description. </p>
	 * <p> Pass when: validateDescription returns null. </p>
	 */
	private static void val10DescriptionAccepted() {
		check("VAL-10 description accepted (positive, CS-10)",
				LessonInputPolicy.validateDescription(DESCRIPTION) == null);
	}

	/**
	 * <p> VAL-11 (CS-10, negative).  An empty description is refused. </p>
	 * <p> How: validateDescription is given "". </p>
	 * <p> Pass when: the problem returned is "The description is required". </p>
	 */
	private static void val11BlankDescriptionRefused() {
		check("VAL-11 blank description refused (negative, CS-10)",
				refusedWith(LessonInputPolicy.validateDescription(""),
						"The description is required"));
	}

	/**
	 * <p> VAL-12 (CS-11, positive).  A 300-character description is accepted. </p>
	 * <p> How: validateDescription is given 300 copies of "x".  This is the regression test for
	 * the HW2 defect: the HW2 code used InputLimits.checkLength, whose 256-character ceiling
	 * refused every description from 257 to 2000 characters.  LessonInputPolicy now uses
	 * checkLongText for the two long fields. </p>
	 * <p> Pass when: validateDescription returns null. </p>
	 */
	private static void val12DescriptionAt300Accepted() {
		check("VAL-12 description at 300 chars accepted (positive, CS-11, HW2 defect)",
				LessonInputPolicy.validateDescription("x".repeat(300)) == null);
	}

	/**
	 * <p> VAL-13 (CS-11, positive).  A description of exactly 2000 characters is accepted. </p>
	 * <p> How: validateDescription is given 2000 copies of "x". </p>
	 * <p> Pass when: validateDescription returns null. </p>
	 */
	private static void val13DescriptionAt2000Accepted() {
		check("VAL-13 description at 2000 chars accepted (positive, CS-11)",
				LessonInputPolicy.validateDescription("x".repeat(2000)) == null);
	}

	/**
	 * <p> VAL-14 (CS-11, negative).  A description of 2001 characters is refused. </p>
	 * <p> How: validateDescription is given 2001 copies of "x". </p>
	 * <p> Pass when: the problem returned is "The description is too long". </p>
	 */
	private static void val14DescriptionAt2001Refused() {
		check("VAL-14 description at 2001 chars refused (negative, CS-11)",
				refusedWith(LessonInputPolicy.validateDescription("x".repeat(2001)),
						"The description is too long"));
	}


	/*-*******************************************************************************************
	 *
	 * Resolution (optional)
	 *
	 */

	/**
	 * <p> VAL-15 (CS-10, positive).  An empty resolution is accepted, because resolution is an
	 * optional field: a lesson can be recorded before the problem is solved. </p>
	 * <p> How: validateResolution is given "". </p>
	 * <p> Pass when: validateResolution returns null. </p>
	 */
	private static void val15EmptyResolutionAccepted() {
		check("VAL-15 empty resolution accepted (positive, CS-10)",
				LessonInputPolicy.validateResolution("") == null);
	}

	/**
	 * <p> VAL-16 (CS-11, positive).  A resolution of exactly 2000 characters is accepted. </p>
	 * <p> How: validateResolution is given 2000 copies of "x". </p>
	 * <p> Pass when: validateResolution returns null. </p>
	 */
	private static void val16ResolutionAt2000Accepted() {
		check("VAL-16 resolution at 2000 chars accepted (positive, CS-11)",
				LessonInputPolicy.validateResolution("x".repeat(2000)) == null);
	}

	/**
	 * <p> VAL-17 (CS-11, negative).  A resolution of 2001 characters is refused. </p>
	 * <p> How: validateResolution is given 2001 copies of "x". </p>
	 * <p> Pass when: the problem returned is "The resolution is too long". </p>
	 */
	private static void val17ResolutionAt2001Refused() {
		check("VAL-17 resolution at 2001 chars refused (negative, CS-11)",
				refusedWith(LessonInputPolicy.validateResolution("x".repeat(2001)),
						"The resolution is too long"));
	}


	/*-*******************************************************************************************
	 *
	 * Keywords (optional)
	 *
	 */

	/**
	 * <p> VAL-18 (CS-10, positive).  Empty keywords are accepted, because keywords are
	 * optional. </p>
	 * <p> How: validateKeywords is given "". </p>
	 * <p> Pass when: validateKeywords returns null. </p>
	 */
	private static void val18EmptyKeywordsAccepted() {
		check("VAL-18 empty keywords accepted (positive, CS-10)",
				LessonInputPolicy.validateKeywords("") == null);
	}

	/**
	 * <p> VAL-19 (CS-11, positive).  Four short keywords are accepted. </p>
	 * <p> How: validateKeywords is given "H2, database, JDBC, lock": under 200 characters, under
	 * 10 keywords, and each keyword under 30 characters. </p>
	 * <p> Pass when: validateKeywords returns null. </p>
	 */
	private static void val19ValidKeywordsAccepted() {
		check("VAL-19 valid keywords accepted (positive, CS-11)",
				LessonInputPolicy.validateKeywords("H2, database, JDBC, lock") == null);
	}

	/**
	 * <p> VAL-20 (CS-11, negative).  Eleven keywords are refused. </p>
	 * <p> How: validateKeywords is given "a,b,c,d,e,f,g,h,i,j,k", one more than the limit of
	 * 10.  The string is only 21 characters, so the refusal can only come from the count. </p>
	 * <p> Pass when: the problem returned is "There are too many keywords". </p>
	 */
	private static void val20ElevenKeywordsRefused() {
		check("VAL-20 eleven keywords refused (negative, CS-11)",
				refusedWith(LessonInputPolicy.validateKeywords("a,b,c,d,e,f,g,h,i,j,k"),
						"There are too many keywords"));
	}

	/**
	 * <p> VAL-21 (CS-11, negative).  A single keyword of 31 characters is refused. </p>
	 * <p> How: validateKeywords is given one keyword of 31 copies of "x". </p>
	 * <p> Pass when: the problem returned is "A keyword is too long". </p>
	 */
	private static void val21LongKeywordRefused() {
		check("VAL-21 keyword over 30 chars refused (negative, CS-11)",
				refusedWith(LessonInputPolicy.validateKeywords("x".repeat(31)),
						"A keyword is too long"));
	}


	/*-*******************************************************************************************
	 *
	 * Contributor
	 *
	 */

	/**
	 * <p> VAL-22 (CS-10, positive).  A username is accepted as the contributor. </p>
	 * <p> How: validateContributor is given "arushugal". </p>
	 * <p> Pass when: validateContributor returns null. </p>
	 */
	private static void val22ContributorAccepted() {
		check("VAL-22 contributor accepted (positive, CS-10)",
				LessonInputPolicy.validateContributor(CONTRIBUTOR) == null);
	}

	/**
	 * <p> VAL-23 (CS-10, negative).  An empty contributor is refused, so a lesson can never be
	 * stored without an owner for the ownership rule (CS-9) to compare against. </p>
	 * <p> How: validateContributor is given "". </p>
	 * <p> Pass when: the problem returned is "The contributor is required". </p>
	 */
	private static void val23BlankContributorRefused() {
		check("VAL-23 blank contributor refused (negative, CS-10)",
				refusedWith(LessonInputPolicy.validateContributor(""),
						"The contributor is required"));
	}


	/*-*******************************************************************************************
	 *
	 * Whole lesson
	 *
	 */

	/**
	 * <p> VAL-24 (CS-10 and CS-11, positive).  A complete, valid lesson is accepted by
	 * validateLesson, the method LessonsLearned.validate calls. </p>
	 * <p> How: validateLesson is given a valid value for all six fields. </p>
	 * <p> Pass when: validateLesson returns null. </p>
	 */
	private static void val24FullLessonAccepted() {
		check("VAL-24 full valid lesson accepted (positive, CS-10, CS-11)",
				LessonInputPolicy.validateLesson(TITLE, CATEGORY, DESCRIPTION, RESOLUTION,
						KEYWORDS, CONTRIBUTOR) == null);
	}

	/**
	 * <p> VAL-25 (CS-10, negative).  validateLesson refuses a lesson whose only fault is an
	 * empty title, which shows the per-field rule is applied when the whole lesson is checked. </p>
	 * <p> How: validateLesson is given the VAL-24 values with the title replaced by "". </p>
	 * <p> Pass when: the problem returned is "The title is required". </p>
	 */
	private static void val25FullLessonWithBlankTitleRefused() {
		check("VAL-25 lesson with blank title refused by validateLesson (negative, CS-10)",
				refusedWith(LessonInputPolicy.validateLesson("", CATEGORY, DESCRIPTION,
						RESOLUTION, KEYWORDS, CONTRIBUTOR), "The title is required"));
	}


	/*-*******************************************************************************************
	 *
	 * Helpers
	 *
	 */

	/**
	 * <p> Method: refusedWith </p>
	 *
	 * <p> Description: True when a problem was returned and its title is the one expected.
	 * Checking the title, not just that something was refused, means a value refused for the
	 * wrong reason is reported as a Failure. </p>
	 *
	 * @param problem       what the validation method returned
	 * @param expectedTitle the problem title the requirement calls for
	 *
	 * @return true when the refusal is the expected one
	 */
	private static boolean refusedWith(Problem problem, String expectedTitle) {
		return problem != null && expectedTitle.equals(problem.title());
	}

	/**
	 * <p> Method: check </p>
	 *
	 * <p> Description: Records one test outcome.  Prints "Pass:" or "Failure:" followed by the
	 * label, and updates {@link #numPassed} or {@link #numFailed}.  The format matches the TP1
	 * suites, so every suite reads the same way in the TestingScreencast. </p>
	 *
	 * @param label  the test id, what was checked, and the requirement
	 * @param result true when the behaviour matched the requirement
	 */
	private static void check(String label, boolean result) {
		if (result) {
			System.out.println("  Pass:    " + label);
			numPassed++;
		} else {
			System.out.println("  Failure: " + label);
			numFailed++;
		}
	}
}
