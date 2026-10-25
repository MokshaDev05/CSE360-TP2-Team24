package guiLessonDetail;

import database.LessonsLearnedDatabase;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import validation.LessonInputPolicy.Problem;

/*******
 * <p> Title: ModelLessonDetail Class. </p>
 *
 * <p> Description: The lesson detail page Model.  It decides whether the logged in contributor
 * may edit or delete the lesson on screen (CS-9), what the page says about that, and carries out
 * the delete (CS-8).  As with the other lesson Models it contains no JavaFX, so the testing
 * automation can check the ownership rule and the delete without opening a window. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-16  Initial version for Team Project Phase 2
 */
public class ModelLessonDetail {

	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public ModelLessonDetail() {
	}


	/*****
	 * <p> Method: boolean canChange(LessonsLearned lesson, String userName) </p>
	 *
	 * <p> Description: True when the user is the lesson's contributor (CS-9).  The page uses this
	 * to enable or disable the Edit and Delete buttons.  It is the same test the editor and the
	 * delete apply, LessonsLearned.isOwnedBy, so the buttons and the rule cannot disagree. </p>
	 *
	 * @param lesson   the lesson on screen
	 * @param userName the logged in username
	 *
	 * @return true when the user may edit or delete the lesson
	 */
	public static boolean canChange(LessonsLearned lesson, String userName) {
		return lesson != null && lesson.isOwnedBy(userName);
	}


	/*****
	 * <p> Method: String ownershipNote(LessonsLearned lesson, String userName) </p>
	 *
	 * <p> Description: The line under the buttons that explains why Edit and Delete are or are
	 * not available.  A disabled button with no explanation looks like a fault, so the page says
	 * whose lesson it is. </p>
	 *
	 * @param lesson   the lesson on screen
	 * @param userName the logged in username
	 *
	 * @return the explanation to show
	 */
	public static String ownershipNote(LessonsLearned lesson, String userName) {
		if (canChange(lesson, userName))
			return "You contributed this lesson, so you can edit or delete it.";
		return "This lesson was contributed by " + lesson.getContributor()
				+ ". Only they can edit or delete it.";
	}


	/*****
	 * <p> Method: Problem deleteLesson(LessonsLearnedDatabase db, LessonsLearnedList all,
	 *      LessonsLearned lesson, String userName) </p>
	 *
	 * <p> Description: Removes the lesson from the database and from the shared list (CS-8),
	 * after checking that the user is its contributor (CS-9).  The Controller has already asked
	 * the user to confirm.  The ownership check is repeated here even though the Delete button is
	 * disabled for anyone else, so that no other route to this method can bypass it. </p>
	 *
	 * @param db       the open lessons database
	 * @param all      the shared list of every lesson
	 * @param lesson   the lesson to remove
	 * @param userName the logged in username
	 *
	 * @return null when the lesson was removed, otherwise the problem that stopped it
	 */
	public static Problem deleteLesson(LessonsLearnedDatabase db, LessonsLearnedList all,
			LessonsLearned lesson, String userName) {
		if (lesson == null)
			return new Problem("There is no lesson to delete", "Open a lesson first.");

		if (!canChange(lesson, userName))
			return new Problem("Only the contributor can delete this lesson",
					"This lesson was contributed by " + lesson.getContributor()
					+ ". You can read it, but only they can delete it.");

		if (!all.deleteLesson(db, lesson.getId()))
			return new Problem("The lesson could not be deleted",
					"No stored lesson has the identifier " + lesson.getId()
					+ ". It may already have been deleted.");
		return null;
	}


	/*****
	 * <p> Method: String reviewLine(LessonsLearned lesson) </p>
	 *
	 * <p> Description: The read only line that shows the TP3 attributes.  They are shown now so
	 * a contributor can see whether staff have reviewed their lesson once that epic exists, but
	 * they cannot be changed from this page. </p>
	 *
	 * @param lesson the lesson on screen
	 *
	 * @return the review status and helpful count as one line
	 */
	public static String reviewLine(LessonsLearned lesson) {
		String line = lesson.getReviewStatus() + ", marked helpful by " + lesson.getHelpfulCount()
				+ (lesson.getHelpfulCount() == 1 ? " reader" : " readers");
		if (lesson.getReviewerComment() != null && !lesson.getReviewerComment().isEmpty())
			line += ". Reviewer: " + lesson.getReviewerComment();
		return line;
	}


	/*****
	 * <p> Method: String showOrDash(String value) </p>
	 *
	 * <p> Description: Shows an empty optional value as a dash, so the page shows that the field
	 * is empty rather than leaving a gap that looks like a display fault. </p>
	 *
	 * @param value the value to show
	 *
	 * @return the value, or a dash when it is empty
	 */
	public static String showOrDash(String value) {
		return (value == null || value.trim().isEmpty()) ? "-" : value;
	}
}
