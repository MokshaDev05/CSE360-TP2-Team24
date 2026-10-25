package guiUserUpdate;

import java.util.Optional;

import entityClasses.User;
import guiTools.ValidationAlert;
import javafx.stage.Stage;
import validation.EmailRecognizer;
import validation.InputLimits;
import validation.NameRecognizer;
import validation.PasswordRecognizer;
import validation.UserNameRecognizer;

public class ControllerUserUpdate {
	/*-********************************************************************************************

	The Controller for ViewUserUpdate 
	
	**********************************************************************************************/

	/**********
	 * <p> Title: ControllerUserUpdate Class</p>
	 * 
	 * <p> Description: This static class supports the actions initiated by the ViewUserUpdate
	 * class. In this case, there is just one method, no constructors, and no attributes.</p>
	 *
	 */

	/*-********************************************************************************************

	The User Interface Actions for this page
	
	**********************************************************************************************/

	
	/*-********************************************************************************************

	Methods added for Team Project Phase 1

	The supplied version of this page wrote whatever the user typed into a dialog straight into
	the database, with nothing checked.  It also had two buttons, Update Username and Update
	Password, that were drawn on the page but had no action attached to them at all, so pressing
	them did nothing.

	Every update now passes through one of the methods below.  Each one shows the dialog, refuses
	the value if a recognizer objects, and only then saves it and refreshes what is on screen.
	The pattern is identical in every case so that a reader who understands one understands all
	of them.

	**********************************************************************************************/

	/**********
	 * <p> Method: protected doUpdateUsername() </p>
	 * 
	 * <p> Description: Changes the signed in user's username.
	 * 
	 * This is the button that previously did nothing.  The new name has to satisfy the same rules
	 * that applied when the account was created, and it has to be free, because the database
	 * holds usernames as unique values.</p>
	 */
	protected static void doUpdateUsername() {
		Optional<String> answer = ViewUserUpdate.dialogUpdateUsername.showAndWait();
		if (answer.isEmpty())
			return;					// The user pressed Cancel, so nothing should change.

		String newUsername = answer.get();
		String currentUsername = ViewUserUpdate.theUser.getUserName();

		// Length first, then the rules the team agreed on in HW1.
		String problem = InputLimits.checkLength(newUsername, InputLimits.MAX_USERNAME,
				"Username");
		if (!problem.isEmpty()) {
			ValidationAlert.show("The username is too long", problem);
			return;
		}

		problem = UserNameRecognizer.checkForValidUserName(newUsername);
		if (!problem.isEmpty()) {
			ValidationAlert.show("That username cannot be used", problem);
			return;
		}

		// Changing a name to the one already held is not an error, but there is nothing to do.
		if (newUsername.equals(currentUsername))
			return;

		if (!ViewUserUpdate.theDatabase.updateUserName(currentUsername, newUsername)) {
			ValidationAlert.show("That username is already taken",
					"Someone already has the username " + newUsername
					+ ". Please choose another.");
			return;
		}

		// The name the rest of the application uses has to change with it.
		ViewUserUpdate.theUser.setUserName(newUsername);
		ViewUserUpdate.theDatabase.getUserAccountDetails(newUsername);
		ViewUserUpdate.label_CurrentUsername.setText(newUsername);
		System.out.println("Username changed from " + currentUsername + " to " + newUsername);
	}

	/**********
	 * <p> Method: protected doUpdatePassword() </p>
	 * 
	 * <p> Description: Changes the signed in user's password.
	 * 
	 * This is the second button that previously did nothing.  The new password has to satisfy
	 * every requirement from the supplied state diagram as well as the maximum length the team
	 * added.  The password itself is never written to the console; only the fact that it changed
	 * is recorded.</p>
	 */
	protected static void doUpdatePassword() {
		Optional<String> answer = ViewUserUpdate.dialogUpdatePassword.showAndWait();
		if (answer.isEmpty())
			return;					// The user pressed Cancel, so nothing should change.

		String newPassword = answer.get();

		String problem = InputLimits.checkLength(newPassword, InputLimits.MAX_PASSWORD,
				"Password");
		if (!problem.isEmpty()) {
			ValidationAlert.show("The password is too long", problem);
			return;
		}

		problem = PasswordRecognizer.evaluatePassword(newPassword);
		if (!problem.isEmpty()) {
			ValidationAlert.show("That password does not meet the requirements",
					problem + "\n\nA password needs an upper case letter, a lower case letter, "
					+ "a digit, a special character, and at least "
					+ PasswordRecognizer.MIN_LENGTH + " characters.");
			return;
		}

		String username = ViewUserUpdate.theUser.getUserName();
		ViewUserUpdate.theDatabase.updatePassword(username, newPassword);
		ViewUserUpdate.theUser.setPassword(newPassword);
		ViewUserUpdate.theDatabase.getUserAccountDetails(username);

		// The page shows the password as a row of stars rather than the password itself.
		ViewUserUpdate.label_CurrentPassword.setText("*".repeat(newPassword.length()));
		System.out.println("Password changed for " + username);
	}

	/**********
	 * <p> Method: protected doUpdateFirstName() </p>
	 * 
	 * <p> Description: Changes the user's first name, which is a required field.</p>
	 */
	protected static void doUpdateFirstName() {
		Optional<String> answer = ViewUserUpdate.dialogUpdateFirstName.showAndWait();
		if (answer.isEmpty())
			return;

		String newName = answer.get();
		String problem = NameRecognizer.checkForValidName(newName, "First name", true);
		if (!problem.isEmpty()) {
			ValidationAlert.show("That first name cannot be used", problem);
			return;
		}

		String username = ViewUserUpdate.theUser.getUserName();
		ViewUserUpdate.theDatabase.updateFirstName(username, newName);
		ViewUserUpdate.theDatabase.getUserAccountDetails(username);
		String saved = ViewUserUpdate.theDatabase.getCurrentFirstName();
		ViewUserUpdate.theUser.setFirstName(saved);
		ViewUserUpdate.label_CurrentFirstName.setText(displayable(saved));
	}

	/**********
	 * <p> Method: protected doUpdateMiddleName() </p>
	 * 
	 * <p> Description: Changes the user's middle name, which may be left empty.</p>
	 */
	protected static void doUpdateMiddleName() {
		Optional<String> answer = ViewUserUpdate.dialogUpdateMiddleName.showAndWait();
		if (answer.isEmpty())
			return;

		String newName = answer.get();
		String problem = NameRecognizer.checkForValidName(newName, "Middle name", false);
		if (!problem.isEmpty()) {
			ValidationAlert.show("That middle name cannot be used", problem);
			return;
		}

		String username = ViewUserUpdate.theUser.getUserName();
		ViewUserUpdate.theDatabase.updateMiddleName(username, newName);
		ViewUserUpdate.theDatabase.getUserAccountDetails(username);
		String saved = ViewUserUpdate.theDatabase.getCurrentMiddleName();
		ViewUserUpdate.theUser.setMiddleName(saved);
		ViewUserUpdate.label_CurrentMiddleName.setText(displayable(saved));
	}

	/**********
	 * <p> Method: protected doUpdateLastName() </p>
	 * 
	 * <p> Description: Changes the user's last name, which is a required field.</p>
	 */
	protected static void doUpdateLastName() {
		Optional<String> answer = ViewUserUpdate.dialogUpdateLastName.showAndWait();
		if (answer.isEmpty())
			return;

		String newName = answer.get();
		String problem = NameRecognizer.checkForValidName(newName, "Last name", true);
		if (!problem.isEmpty()) {
			ValidationAlert.show("That last name cannot be used", problem);
			return;
		}

		String username = ViewUserUpdate.theUser.getUserName();
		ViewUserUpdate.theDatabase.updateLastName(username, newName);
		ViewUserUpdate.theDatabase.getUserAccountDetails(username);
		String saved = ViewUserUpdate.theDatabase.getCurrentLastName();
		ViewUserUpdate.theUser.setLastName(saved);
		ViewUserUpdate.label_CurrentLastName.setText(displayable(saved));
	}

	/**********
	 * <p> Method: protected doUpdatePreferredFirstName() </p>
	 * 
	 * <p> Description: Changes the name the user prefers to be called, which may be left
	 * empty.</p>
	 */
	protected static void doUpdatePreferredFirstName() {
		Optional<String> answer = ViewUserUpdate.dialogUpdatePreferredFirstName.showAndWait();
		if (answer.isEmpty())
			return;

		String newName = answer.get();
		String problem = NameRecognizer.checkForValidName(newName, "Preferred first name", false);
		if (!problem.isEmpty()) {
			ValidationAlert.show("That preferred first name cannot be used", problem);
			return;
		}

		String username = ViewUserUpdate.theUser.getUserName();
		ViewUserUpdate.theDatabase.updatePreferredFirstName(username, newName);
		ViewUserUpdate.theDatabase.getUserAccountDetails(username);
		String saved = ViewUserUpdate.theDatabase.getCurrentPreferredFirstName();
		ViewUserUpdate.theUser.setPreferredFirstName(saved);
		ViewUserUpdate.label_CurrentPreferredFirstName.setText(displayable(saved));
	}

	/**********
	 * <p> Method: protected doUpdateEmailAddress() </p>
	 * 
	 * <p> Description: Changes the user's email address.
	 * 
	 * The address matters more than it appears to, because invitations and any future password
	 * reset depend on it.  An address that is quietly wrong is worse than one that is refused.</p>
	 */
	protected static void doUpdateEmailAddress() {
		Optional<String> answer = ViewUserUpdate.dialogUpdateEmailAddresss.showAndWait();
		if (answer.isEmpty())
			return;

		String newEmail = answer.get();
		String problem = EmailRecognizer.checkForValidEmailAddress(newEmail);
		if (!problem.isEmpty()) {
			ValidationAlert.show("That email address cannot be used", problem);
			return;
		}

		String username = ViewUserUpdate.theUser.getUserName();
		ViewUserUpdate.theDatabase.updateEmailAddress(username, newEmail);
		ViewUserUpdate.theDatabase.getUserAccountDetails(username);
		String saved = ViewUserUpdate.theDatabase.getCurrentEmailAddress();
		ViewUserUpdate.theUser.setEmailAddress(saved);
		ViewUserUpdate.label_CurrentEmailAddress.setText(displayable(saved));
	}

	/**********
	 * <p> Method: private displayable(String value) </p>
	 * 
	 * <p> Description: Returns the text to show on the page for a stored value, so that a field
	 * that has not been filled in reads as none rather than as the word null.</p>
	 * 
	 * @param value the value read back from the database
	 * 
	 * @return the value, or a placeholder when there is nothing to show
	 */
	private static String displayable(String value) {
		if (value == null || value.length() < 1)
			return "<none>";
		return value;
	}


	/**********
	 * <p> Method: public goToUserHomePage(Stage theStage, User theUser) </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the button to
	 * proceed to the user's home page.
	 * 
	 * @param theStage specifies the JavaFX Stage for next next GUI page and it's methods
	 * 
	 * @param theUser specifies the user so we go to the right page and so the right information
	 */
	protected static void goToUserHomePage(Stage theStage, User theUser) {
		
		// Get the roles the user selected during login
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		// Use that role to proceed to that role's home page
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			break;
		case 2:
			guiRole1.ViewRole1Home.displayRole1Home(theStage, theUser);
			break;
		case 3:
			guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
			break;
		default: 
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + 
					theRole);
			System.exit(0);
		}
 	}
}
