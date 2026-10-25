package guiLessonsList;

import java.util.List;

import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import guiTools.ValidationAlert;

/*******
 * <p> Title: ControllerLessonsList Class. </p>
 *
 * <p> Description: The Java/FX-based Lessons Learned list page controller.  It reacts to the
 * search box, the Search, Show All, New Lesson, Open, Back, and Quit buttons, the category filter,
 * and the "only my lessons" box.  The decisions about which lessons to show and what to say about
 * them are made by ModelLessonsList; this class reads the controls, asks the Model, and puts the
 * answer on screen. </p>
 *
 * <p> As in the supplied controllers, this is not a class that gets instantiated.  It is a
 * collection of protected static methods called by the View, which is why each is protected and
 * none is public. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-16  Initial version for Team Project Phase 2
 */
public class ControllerLessonsList {

	/**
	 * Default constructor is not used.
	 */
	public ControllerLessonsList() {
	}


	/**********
	 * <p> Method: reloadLessons() </p>
	 *
	 * <p> Description: Reads every stored lesson into the Model (CS-2, CS-12).  Reading from the
	 * database, rather than keeping whatever was in memory, is what shows lessons recorded in an
	 * earlier run of the application. </p>
	 */
	protected static void reloadLessons() {
		ModelLessonsList.setAllLessons(
				LessonsLearnedList.readAll(applicationMain.FoundationsMain.lessonsDatabase));
	}


	/**********
	 * <p> Method: fillCategories() </p>
	 *
	 * <p> Description: Fills the category filter with "All categories" followed by every
	 * category that a stored lesson uses (CS-5).  The choice the user had made is kept if that
	 * category still exists, so returning from the editor does not reset the filter. </p>
	 */
	protected static void fillCategories() {
		String previous = ViewLessonsList.combo_Category.getValue();
		List<String> categories = ModelLessonsList.getAllLessons().getCategories();

		ViewLessonsList.combo_Category.getItems().clear();
		ViewLessonsList.combo_Category.getItems().add(ModelLessonsList.ALL_CATEGORIES);
		ViewLessonsList.combo_Category.getItems().addAll(categories);

		if (previous != null && ViewLessonsList.combo_Category.getItems().contains(previous))
			ViewLessonsList.combo_Category.setValue(previous);
		else
			ViewLessonsList.combo_Category.setValue(ModelLessonsList.ALL_CATEGORIES);
	}


	/**********
	 * <p> Method: applyFilters() </p>
	 *
	 * <p> Description: Applies the search term, the category, and the "only my lessons" choice
	 * together and shows the result with its status line (CS-2, CS-4, CS-5, CS-6).  All three
	 * controls call this one method, so the list always reflects every choice on screen rather
	 * than only the last one changed. </p>
	 */
	protected static void applyFilters() {
		String term = ViewLessonsList.text_Search.getText();

		// The search box is a text field, so it is length checked like every other field in the
		// application.  A search term longer than any title could never match a title, and a
		// very long paste is refused here before it is compared against every lesson.
		String lengthProblem = validation.InputLimits.checkLength(term,
				validation.LessonInputPolicy.MAX_TITLE, "The search term");
		if (!lengthProblem.isEmpty()) {
			ValidationAlert.show("The search term is too long", lengthProblem);
			return;
		}

		String category = ViewLessonsList.combo_Category.getValue();
		boolean onlyMine = ViewLessonsList.check_OnlyMine.isSelected();

		LessonsLearnedList shown = ModelLessonsList.refreshShownLessons(term, category, onlyMine,
				ViewLessonsList.theUser.getUserName());

		ViewLessonsList.list_Lessons.getItems().setAll(shown.toDisplayLines());
		ViewLessonsList.label_Status.setText(ModelLessonsList.statusMessage(shown.size(),
				ModelLessonsList.getAllLessons().size(), term, category, onlyMine));
	}


	/**********
	 * <p> Method: showAll() </p>
	 *
	 * <p> Description: Clears the search, the category, and the "only my lessons" box, and shows
	 * every lesson (CS-2).  This is the way out of an empty search result that the status line
	 * points the user to. </p>
	 */
	protected static void showAll() {
		ViewLessonsList.text_Search.setText("");
		ViewLessonsList.combo_Category.setValue(ModelLessonsList.ALL_CATEGORIES);
		ViewLessonsList.check_OnlyMine.setSelected(false);
		applyFilters();
	}


	/**********
	 * <p> Method: newLesson() </p>
	 *
	 * <p> Description: Opens the lesson editor with an empty form, to record a new lesson (CS-1).
	 * </p>
	 */
	protected static void newLesson() {
		guiLessonEditor.ViewLessonEditor.displayLessonEditor(ViewLessonsList.theStage,
				ViewLessonsList.theUser, null);
	}


	/**********
	 * <p> Method: openSelectedLesson() </p>
	 *
	 * <p> Description: Opens the detail page for the lesson on the selected row (CS-3).  If no row
	 * is selected the user is told so, rather than the button appearing to do nothing. </p>
	 */
	protected static void openSelectedLesson() {
		int row = ViewLessonsList.list_Lessons.getSelectionModel().getSelectedIndex();
		LessonsLearned lesson = ModelLessonsList.lessonAt(row);
		if (lesson == null) {
			ValidationAlert.show("No lesson is selected",
					"Click a lesson in the list first, then press Open Selected Lesson.");
			return;
		}
		guiLessonDetail.ViewLessonDetail.displayLessonDetail(ViewLessonsList.theStage,
				ViewLessonsList.theUser, lesson);
	}


	/**********
	 * <p> Method: goBackHome() </p>
	 *
	 * <p> Description: Returns to the home page of the role the user is playing.  The role is
	 * read from FoundationsMain.activeHomePage, which the home pages set, so this works the same
	 * way the supplied account update page returns home. </p>
	 */
	protected static void goBackHome() {
		switch (applicationMain.FoundationsMain.activeHomePage) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewLessonsList.theStage,
					ViewLessonsList.theUser);
			break;
		case 3:
			guiRole2.ViewRole2Home.displayRole2Home(ViewLessonsList.theStage,
					ViewLessonsList.theUser);
			break;
		default:
			guiRole1.ViewRole1Home.displayRole1Home(ViewLessonsList.theStage,
					ViewLessonsList.theUser);
		}
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the application.  Every lesson is already stored, so there is
	 * nothing to save first. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
