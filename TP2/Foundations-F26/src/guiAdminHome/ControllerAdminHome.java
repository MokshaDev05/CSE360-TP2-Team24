package guiAdminHome;

import java.util.List;
import java.util.Optional;

import database.Database;
import guiTools.ValidationAlert;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Font;
import validation.EmailRecognizer;
import validation.OneTimePasswordGenerator;
import validation.InputLimits;
import validation.PasswordRecognizer;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerAdminHome() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Shows every invitation that has been sent and not yet used, and lets the
	 * administrator cancel one of them.
	 * 
	 * This replaces a stub that only reported that the function did not exist.  Cancelling
	 * matters because an invitation code is a way into the system: an invitation sent to the
	 * wrong address, or one that is simply no longer wanted, should not stay usable forever.
	 * 
	 * The code to cancel is typed in rather than picked from the list, which keeps the change
	 * inside the existing page rather than requiring a new screen. </p>
	 */
	protected static void manageInvitations () {
		List<String> invitations = theDatabase.getInvitationList();

		if (invitations.isEmpty()) {
			ViewAdminHome.alertNotImplemented.setTitle("Manage Invitations");
			ViewAdminHome.alertNotImplemented.setHeaderText("There are no outstanding invitations");
			ViewAdminHome.alertNotImplemented.setContentText(
					"Every invitation that was sent has already been used.");
			ViewAdminHome.alertNotImplemented.showAndWait();
			return;
		}

		// Build the list for display, and write the same list to the console so it is visible
		// while the screencast is being recorded.
		StringBuilder theList = new StringBuilder();
		theList.append(String.format("%-12s %-32s %s%n", "CODE", "EMAIL ADDRESS", "ROLE"));
		System.out.println("\nOutstanding invitations:");
		for (String oneInvitation : invitations) {
			theList.append(oneInvitation).append(System.lineSeparator());
			System.out.println("  " + oneInvitation);
		}

		// Offer the administrator the chance to cancel one of them.
		TextInputDialog cancelDialog = new TextInputDialog("");
		cancelDialog.setTitle("Manage Invitations");
		cancelDialog.setHeaderText(invitations.size() + " outstanding invitation(s)\n\n"
				+ theList.toString());
		cancelDialog.setContentText("To cancel one, type its code and press OK:");

		Optional<String> answer = cancelDialog.showAndWait();
		if (answer.isEmpty() || answer.get().isEmpty())
			return;					// Nothing was typed, so nothing is cancelled.

		String code = answer.get().trim();

		// The code is length checked before it is used, in the same way as every other input.
		String problem = InputLimits.checkLength(code, InputLimits.MAX_INVITATION,
				"Invitation code");
		if (!problem.isEmpty()) {
			ValidationAlert.show("That invitation code is too long", problem);
			return;
		}

		if (!theDatabase.doesInvitationExist(code)) {
			ValidationAlert.show("No such invitation",
					"There is no outstanding invitation with the code " + code + ".");
			return;
		}

		theDatabase.removeInvitationAfterUse(code);
		System.out.println("Invitation " + code + " was cancelled by the administrator.");

		// The count on the page has to reflect the change.
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: "
				+ theDatabase.getNumberOfInvitations());

		ViewAdminHome.alertNotImplemented.setTitle("Manage Invitations");
		ViewAdminHome.alertNotImplemented.setHeaderText("Invitation cancelled");
		ViewAdminHome.alertNotImplemented.setContentText("The invitation with code " + code
				+ " can no longer be used.");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Gives an existing account a temporary password so that a user who can no
	 * longer sign in has a way back in.
	 * 
	 * This replaces a stub that only reported that the function did not exist.
	 * 
	 * The password is generated here rather than typed by the administrator, for two reasons.  A
	 * generated value satisfies every password requirement by construction, and an administrator
	 * who invents the value tends to reuse the same one, which is exactly the habit a one time
	 * password is meant to avoid.
	 * 
	 * The account is also marked as requiring a password change, which is what makes this a one
	 * time password rather than simply a new one.  The next time the user signs in with it, they
	 * are required to choose a replacement before they reach any home page, and the mark is
	 * cleared once they have.  Recording that state needs a column the supplied userDB table does
	 * not have; it is added by an ADD COLUMN IF NOT EXISTS statement that runs on every start,
	 * which is safe for a database that already exists as well as for a new one. </p>
	 */
	protected static void setOnetimePassword () {
		TextInputDialog whichUserDialog = new TextInputDialog("");
		whichUserDialog.setTitle("Set a One-Time Password");
		whichUserDialog.setHeaderText("Give a user a temporary password");
		whichUserDialog.setContentText("Username:");

		Optional<String> answer = whichUserDialog.showAndWait();
		if (answer.isEmpty() || answer.get().isEmpty())
			return;

		String username = answer.get().trim();

		// Length is checked before the name is used to look anything up.
		String problem = InputLimits.checkLength(username, InputLimits.MAX_USERNAME, "Username");
		if (!problem.isEmpty()) {
			ValidationAlert.show("That username is too long", problem);
			return;
		}

		if (!theDatabase.doesUserExist(username)) {
			ValidationAlert.show("No such user",
					"There is no account with the username " + username + ".");
			return;
		}

		String temporaryPassword = OneTimePasswordGenerator.generate();

		// The generated value is put through the same recognizer every other password goes
		// through.  If the generator were ever changed and started producing something
		// unacceptable, this is where that would be caught rather than at the user's next sign in.
		String check = PasswordRecognizer.evaluatePassword(temporaryPassword);
		if (!check.isEmpty()) {
			System.out.println("*** ERROR *** The generated one-time password was refused: "
					+ check);
			ValidationAlert.show("The temporary password could not be created",
					"Please try again.");
			return;
		}

		theDatabase.updatePassword(username, temporaryPassword);

		// Story AD-2: record that this password is temporary, so that the next successful sign in
		// with it requires a replacement before the user reaches any home page.  Without this the
		// account would simply have a new working password and nothing would ever ask for it to be
		// changed.
		theDatabase.setForcePasswordChange(username, true);

		System.out.println("A one-time password was set for " + username
				+ " and a password change will be required at next sign in.");

		ViewAdminHome.alertNotImplemented.setTitle("One-Time Password");
		ViewAdminHome.alertNotImplemented.setHeaderText("Temporary password set for " + username);
		ViewAdminHome.alertNotImplemented.setContentText("The temporary password is:\n\n"
				+ temporaryPassword
				+ "\n\nGive this to the user. The next time they sign in with it they will be "
				+ "required to choose a new password before they can continue.");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}

	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Removes an account, after asking the administrator to confirm.
	 * 
	 * This replaces a stub that only reported that the function did not exist.
	 * 
	 * Two guards are applied.  The administrator is asked to confirm, because there is no way to
	 * undo the removal, and the last remaining administrator cannot be removed, because doing so
	 * would leave the system with nobody able to administer it and no way to repair that from
	 * inside the application. </p>
	 */
	protected static void deleteUser() {
		TextInputDialog whichUserDialog = new TextInputDialog("");
		whichUserDialog.setTitle("Delete a User");
		whichUserDialog.setHeaderText("Remove an account from the system");
		whichUserDialog.setContentText("Username to delete:");

		Optional<String> answer = whichUserDialog.showAndWait();
		if (answer.isEmpty() || answer.get().isEmpty())
			return;

		String username = answer.get().trim();

		// Length is checked before the name is used to look anything up.
		String problem = InputLimits.checkLength(username, InputLimits.MAX_USERNAME, "Username");
		if (!problem.isEmpty()) {
			ValidationAlert.show("That username is too long", problem);
			return;
		}

		if (!theDatabase.doesUserExist(username)) {
			ValidationAlert.show("No such user",
					"There is no account with the username " + username + ".");
			return;
		}

		// Removing the only administrator would leave nobody able to administer the system.
		theDatabase.getUserAccountDetails(username);
		if (theDatabase.getCurrentAdminRole() && theDatabase.getNumberOfAdmins() <= 1) {
			ValidationAlert.show("This account cannot be deleted",
					username + " is the only administrator. Give the Admin role to another "
					+ "account first, otherwise nobody would be able to administer the system.");
			return;
		}

		// There is no undo, so the administrator has to confirm.
		Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
		confirmDialog.setTitle("Delete a User");
		confirmDialog.setHeaderText("Delete the account " + username + "?");
		confirmDialog.setContentText("This cannot be undone. Press OK to delete the account, or "
				+ "Cancel to keep it.");

		Optional<ButtonType> confirmation = confirmDialog.showAndWait();
		if (confirmation.isEmpty() || confirmation.get() != ButtonType.OK) {
			System.out.println("The deletion of " + username + " was cancelled.");
			return;
		}

		if (theDatabase.deleteUser(username)) {
			System.out.println("The account " + username + " was deleted.");
			ViewAdminHome.alertNotImplemented.setTitle("Delete a User");
			ViewAdminHome.alertNotImplemented.setHeaderText("Account deleted");
			ViewAdminHome.alertNotImplemented.setContentText("The account " + username
					+ " has been removed.");
			ViewAdminHome.alertNotImplemented.showAndWait();
		} else {
			ValidationAlert.show("The account could not be deleted",
					"Nothing was removed. Please try again.");
		}
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Shows every account with its name, email address, and roles.
	 * 
	 * This replaces a stub that only reported that the function did not exist.
	 * 
	 * Passwords are deliberately left out of the list.  An administrator has no need to read
	 * them, and putting them on screen would mean anybody who happened to be looking at the
	 * display could read them as well. </p>
	 */
	protected static void listUsers() {
		List<String> users = theDatabase.getUserDetailsList();

		if (users.isEmpty()) {
			ViewAdminHome.alertNotImplemented.setTitle("List All Users");
			ViewAdminHome.alertNotImplemented.setHeaderText("There are no accounts");
			ViewAdminHome.alertNotImplemented.setContentText("The user database is empty.");
			ViewAdminHome.alertNotImplemented.showAndWait();
			return;
		}

		// Build the list for display, and write the same list to the console so it is visible
		// while the screencast is being recorded.
		StringBuilder theList = new StringBuilder();
		theList.append(String.format("%-18s %-24s %-28s %s%n", "USERNAME", "NAME",
				"EMAIL ADDRESS", "ROLES"));
		System.out.println("\nAll users:");
		for (String oneUser : users) {
			theList.append(oneUser).append(System.lineSeparator());
			System.out.println("  " + oneUser);
		}

		// A TextArea is used rather than the content text of an alert, because a long list needs
		// to scroll and a plain alert simply grows until it runs off the screen.
		TextArea theTextArea = new TextArea(theList.toString());
		theTextArea.setEditable(false);
		theTextArea.setWrapText(false);
		theTextArea.setFont(Font.font("Monospaced", 13));
		theTextArea.setPrefSize(720, 320);

		Alert listDialog = new Alert(AlertType.INFORMATION);
		listDialog.setTitle("List All Users");
		listDialog.setHeaderText(users.size() + " account(s) in the system");
		listDialog.getDialogPane().setContent(theTextArea);
		listDialog.getDialogPane().setPrefWidth(760);
		listDialog.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		// TP1 change: the supplied version only checked whether the field was empty, so any
		// text at all was accepted as an email address and an invitation code was generated
		// for it.  A code sent to an address that cannot receive mail is simply lost, and the
		// administrator has no way of knowing.  The address is now checked properly, and the
		// reason it was refused is shown.
		String problem = EmailRecognizer.checkForValidEmailAddress(emailAddress);
		if (!problem.isEmpty()) {
			ViewAdminHome.alertEmailError.setContentText(problem.replace("*** ERROR *** ", "")
					+ "\n\nCorrect the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
