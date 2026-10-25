package guiTools;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import validation.ScreenInputPolicy;

/*******
 * <p> Title: ForcedPasswordChangeDialog Class. </p>
 * 
 * <p> Description: The step that completes story AD-2.  When an administrator has issued a one
 * time password, the account is marked as needing a new one, and this dialog is what makes that
 * requirement real: the user who signs in with the temporary password has to choose a replacement
 * here before they reach any home page.</p>
 * 
 * <p> A modal dialog is used rather than a whole new screen because that is already how this
 * application collects a single new value.  The account update page shows each value as a label
 * and opens a dialog when a button is pressed, so a dialog is the established pattern here and
 * adding a fourth screen package would be more machinery for no benefit.</p>
 * 
 * <p> Two things about the way it behaves are deliberate.  It cannot be dismissed by pressing
 * Cancel or the window close button, because a user who could dismiss it would be back to having
 * a working temporary password and the requirement would mean nothing.  And it keeps reappearing
 * until an acceptable password is supplied, so an unusable entry is corrected rather than ending
 * the attempt.  The only way out is to supply a valid password or to quit the application, which
 * leaves the account exactly as the administrator set it.</p>
 * 
 * <p> The replacement is checked with the same rules used when an account is created, by calling
 * the same validation the New Account screen calls.  It is not a second copy of those rules.</p>
 * 
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP1 additions by CSE 360 Team: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-23  Initial version for story AD-2
 */
public class ForcedPasswordChangeDialog {

	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public ForcedPasswordChangeDialog() {
	}

	/**********
	 * <p> Method: askForNewPassword </p>
	 * 
	 * <p> Description: Asks the user for a replacement password and keeps asking until one is
	 * acceptable.
	 * 
	 * The dialog reports why an entry was refused using the same shared alert every screen uses,
	 * so the wording and the behaviour match the rest of the application.</p>
	 * 
	 * @param userName the account that is signing in, shown so it is obvious whose password this
	 *                 is about
	 * 
	 * @return the accepted replacement password, or null if the user closed the dialog without
	 *         supplying one, in which case the caller must not let the sign-in continue
	 */
	public static String askForNewPassword(String userName) {

		while (true) {
			Dialog<ButtonType> dialog = new Dialog<>();
			dialog.setTitle("Choose a new password");
			dialog.setHeaderText("The password you signed in with is temporary.\n"
					+ "Choose a new password for " + userName + " to continue.");

			// Only one button, and it is the primary action.  There is deliberately no Cancel:
			// being able to cancel would leave the temporary password working and make the
			// requirement meaningless.
			ButtonType setIt = new ButtonType("Set Password", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(setIt);

			// Labels sit above their fields rather than inside them as prompt text, which is the
			// pattern recorded in User Experience.pdf.
			Label newLabel = new Label("New password");
			Label againLabel = new Label("New password again");
			newLabel.setFont(Font.font(Theme.FONT, FontWeight.SEMI_BOLD, 13));
			againLabel.setFont(Font.font(Theme.FONT, FontWeight.SEMI_BOLD, 13));

			PasswordField newPassword = new PasswordField();
			PasswordField confirmation = new PasswordField();
			newPassword.setFont(Font.font(Theme.FONT, 14));
			confirmation.setFont(Font.font(Theme.FONT, 14));
			newPassword.setPrefColumnCount(24);
			confirmation.setPrefColumnCount(24);

			Label requirements = new Label(
					"At least 8 characters and no more than 64, with an upper case letter, "
					+ "a lower case letter, a digit, and a special character.");
			requirements.setFont(Font.font(Theme.FONT, 12));
			requirements.setWrapText(true);
			requirements.setMaxWidth(320);

			GridPane layout = new GridPane();
			layout.setHgap(10);
			layout.setVgap(6);
			layout.setPadding(new Insets(16, 20, 8, 20));
			layout.add(newLabel, 0, 0);
			layout.add(newPassword, 0, 1);
			layout.add(againLabel, 0, 2);
			layout.add(confirmation, 0, 3);
			layout.add(requirements, 0, 4);
			dialog.getDialogPane().setContent(layout);

			newPassword.requestFocus();

			Optional<ButtonType> answer = dialog.showAndWait();

			// The window close button can still dismiss a dialog, so that case is handled: the
			// caller is told nothing was set and must refuse the sign-in.
			if (answer.isEmpty() || answer.get() != setIt)
				return null;

			String typed = newPassword.getText();
			String again = confirmation.getText();

			// The same checks the New Account screen applies, in the same order.
			ScreenInputPolicy.Problem problem =
					ScreenInputPolicy.validatePasswordChange(typed, again);

			if (problem == null)
				return typed;

			ValidationAlert.show(problem.title(), problem.detail());
			// and round again, so the user corrects the entry rather than being thrown out
		}
	}
}
