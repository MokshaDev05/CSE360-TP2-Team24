package guiLessonDetail;

import java.util.Optional;

import guiLessonsList.ModelLessonsList;
import guiTools.ValidationAlert;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import validation.LessonInputPolicy.Problem;

/*******
 * <p> Title: ControllerLessonDetail Class. </p>
 *
 * <p> Description: The Java/FX-based lesson detail controller.  It reacts to the Edit, Delete,
 * and Back buttons.  Delete asks the contributor to confirm before anything is removed (CS-8),
 * because a deleted lesson cannot be recovered. </p>
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
public class ControllerLessonDetail {

	/**
	 * Default constructor is not used.
	 */
	public ControllerLessonDetail() {
	}


	/**********
	 * <p> Method: performEdit() </p>
	 *
	 * <p> Description: Opens the editor with this lesson's current values (CS-7). </p>
	 */
	protected static void performEdit() {
		guiLessonEditor.ViewLessonEditor.displayLessonEditor(ViewLessonDetail.theStage,
				ViewLessonDetail.theUser, ViewLessonDetail.theLesson);
	}


	/**********
	 * <p> Method: performDelete() </p>
	 *
	 * <p> Description: Deletes this lesson after the contributor confirms (CS-8).  The
	 * confirmation names the lesson by identifier and title, so a contributor who opened the wrong
	 * lesson can see that before it is too late.  The confirmation's default button is Cancel, so
	 * pressing Enter by habit does not delete anything. </p>
	 */
	protected static void performDelete() {
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("Delete Lesson");
		confirm.setHeaderText("Delete lesson #" + ViewLessonDetail.theLesson.getId() + "?");
		confirm.setContentText("\"" + ViewLessonDetail.theLesson.getTitle()
				+ "\" will be removed for everyone. This cannot be undone.");
		ButtonType delete = new ButtonType("Delete");
		ButtonType cancel = ButtonType.CANCEL;
		confirm.getButtonTypes().setAll(delete, cancel);
		((javafx.scene.control.Button) confirm.getDialogPane().lookupButton(delete))
				.setDefaultButton(false);
		((javafx.scene.control.Button) confirm.getDialogPane().lookupButton(cancel))
				.setDefaultButton(true);

		Optional<ButtonType> answer = confirm.showAndWait();
		if (answer.isEmpty() || answer.get() != delete) {
			System.out.println("** Delete cancelled for lesson #"
					+ ViewLessonDetail.theLesson.getId());
			return;
		}

		int id = ViewLessonDetail.theLesson.getId();
		Problem problem = ModelLessonDetail.deleteLesson(
				applicationMain.FoundationsMain.lessonsDatabase, ModelLessonsList.getAllLessons(),
				ViewLessonDetail.theLesson, ViewLessonDetail.theUser.getUserName());
		if (problem != null) {
			ValidationAlert.show(problem.title(), problem.detail());
			return;
		}

		System.out.println("** Lesson #" + id + " deleted");
		Alert done = new Alert(AlertType.INFORMATION);
		done.setTitle("Lesson Deleted");
		done.setHeaderText("Lesson #" + id + " was deleted.");
		done.setContentText("It has been removed from the list for everyone.");
		done.showAndWait();

		guiLessonsList.ViewLessonsList.refreshList(ViewLessonDetail.theStage,
				ViewLessonDetail.theUser);
	}


	/**********
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: Returns to the list with the search and filters the user had set. </p>
	 */
	protected static void performBack() {
		guiLessonsList.ViewLessonsList.refreshList(ViewLessonDetail.theStage,
				ViewLessonDetail.theUser);
	}
}
