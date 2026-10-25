package guiTools;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/*******
 * <p> Title: ValidationAlert Class. </p>
 * 
 * <p> Description: One place that shows the user why something they typed was not
 * accepted.</p>
 * 
 * <p> Every screen that checks input needs to tell the user the result, and it matters that
 * they all do it the same way.  If one screen shows a dialog, another writes to a label, and
 * a third only writes to the console, the application feels unfinished and some failures go
 * unnoticed.  Routing every message through this class keeps the wording, the title, and the
 * behaviour identical everywhere.</p>
 * 
 * <p> Copyright: CSE 360 TP1 Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,
 * Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-22  Initial version for Team Project Phase 1
 */
public class ValidationAlert {

	// One dialog is created and reused, in the same way the supplied screens reuse theirs.
	private static final Alert theAlert = new Alert(AlertType.INFORMATION);

	/**
	 * Default constructor is not used.
	 */
	public ValidationAlert() {
	}

	/**********
	 * <p> Method: show </p>
	 * 
	 * <p> Description: Shows one validation message and waits for the user to acknowledge
	 * it, so that they cannot continue without seeing it.  The message is also written to
	 * the console, which keeps a record of what was rejected while the screencast is being
	 * recorded.</p>
	 * 
	 * @param whatWasWrong the heading, a short description of the field that was refused
	 * @param details      the explanation, normally the message a recognizer returned
	 */
	public static void show(String whatWasWrong, String details) {
		System.out.println("*** Input rejected *** " + whatWasWrong + ": " + details.trim());

		theAlert.setTitle("Please Check Your Input");
		theAlert.setHeaderText(whatWasWrong);
		theAlert.setContentText(details.trim());
		theAlert.showAndWait();
	}
}
