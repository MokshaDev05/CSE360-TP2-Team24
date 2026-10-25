package guiLessonEditor;

import guiLessonsList.ModelLessonsList;
import guiTools.ValidationAlert;
import validation.LessonInputPolicy.Problem;

/*******
 * <p> Title: ControllerLessonEditor Class. </p>
 *
 * <p> Description: The Java/FX-based lesson editor controller.  It reacts to the Save and Cancel
 * buttons.  Save reads the five fields, hands them to ModelLessonEditor, and either reports the
 * first problem through the shared ValidationAlert or moves on to the saved lesson. </p>
 *
 * <p> As in the supplied controllers, this is a collection of protected static methods called by
 * the View, and is never instantiated. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-16  Initial version for Team Project Phase 2
 */
public class ControllerLessonEditor {

	/**
	 * Default constructor is not used.
	 */
	public ControllerLessonEditor() {
	}


	/**********
	 * <p> Method: performSave() </p>
	 *
	 * <p> Description: Saves the form as a new lesson (CS-1) or as changes to the lesson being
	 * edited (CS-7).  If anything is refused (CS-9, CS-10, CS-11) the reason is shown and the
	 * form is left exactly as the contributor typed it, so they can correct the one field that
	 * was wrong rather than starting again.  On success the contributor is taken to the detail
	 * page of the saved lesson, which shows the identifier the lesson was given (CS-1). </p>
	 */
	protected static void performSave() {
		String title = ViewLessonEditor.text_Title.getText();
		String category = ViewLessonEditor.text_Category.getText();
		String keywords = ViewLessonEditor.text_Keywords.getText();
		String description = ViewLessonEditor.text_Description.getText();
		String resolution = ViewLessonEditor.text_Resolution.getText();
		String userName = ViewLessonEditor.theUser.getUserName();

		Problem problem;
		if (ViewLessonEditor.theLesson == null)
			problem = ModelLessonEditor.saveNewLesson(
					applicationMain.FoundationsMain.lessonsDatabase,
					ModelLessonsList.getAllLessons(), userName,
					title, category, keywords, description, resolution);
		else
			problem = ModelLessonEditor.saveChanges(
					applicationMain.FoundationsMain.lessonsDatabase,
					ModelLessonsList.getAllLessons(), ViewLessonEditor.theLesson, userName,
					title, category, keywords, description, resolution);

		if (problem != null) {
			ValidationAlert.show(problem.title(), problem.detail());
			return;
		}

		System.out.println("** Lesson saved: " + ModelLessonEditor.getSavedLesson().toDisplayLine());
		guiLessonDetail.ViewLessonDetail.displayLessonDetail(ViewLessonEditor.theStage,
				ViewLessonEditor.theUser, ModelLessonEditor.getSavedLesson());
	}


	/**********
	 * <p> Method: performCancel() </p>
	 *
	 * <p> Description: Leaves the editor without saving.  When editing, the contributor goes back
	 * to the lesson they were editing; when recording a new lesson, back to the list.  Nothing in
	 * the form is kept, because nothing was confirmed. </p>
	 */
	protected static void performCancel() {
		if (ViewLessonEditor.theLesson == null)
			guiLessonsList.ViewLessonsList.refreshList(ViewLessonEditor.theStage,
					ViewLessonEditor.theUser);
		else
			guiLessonDetail.ViewLessonDetail.displayLessonDetail(ViewLessonEditor.theStage,
					ViewLessonEditor.theUser, ViewLessonEditor.theLesson);
	}
}
