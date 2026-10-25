package guiUserLogin;

import database.Database;
import entityClasses.User;
import guiTools.ValidationAlert;
import javafx.stage.Stage;
import validation.ScreenInputPolicy;

/*******
 * <p> Title: ControllerUserLogin Class. </p>
 * 
 * <p> Description: The Java/FX-based User Login Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This controller determines if the log in is valid.  If so set up the link to the database, 
 * determines how many roles this user is authorized to play, and the calls one the of the array of
 * role home pages if there is only one role.  If there are more than one role, it setup up and
 * calls the multiple roles dispatch page for the user to determine which role the user wants to
 * play.
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

public class ControllerUserLogin {
	
	/*-********************************************************************************************

	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerUserLogin() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	private static Stage theStage;	
	
	/**********
	 * <p> Method: public doLogin() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Login button. This
	 * method checks the username and password to see if they are valid.  If so, it then logs that
	 * user in my determining which role to use.
	 * 
	 * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 * 
	 */	
	protected static void doLogin(Stage ts) {
		theStage = ts;
		String username = ViewUserLogin.text_Username.getText();
		String password = ViewUserLogin.text_Password.getText();
    	boolean loginResult = false;
    	
		// TP1 addition: check both values before either of them is used to query the database.
		// Until this was added, whatever was typed here went straight into a database lookup
		// without ever being examined, which made the login page the one screen in the
		// application that did not check its own input.
		if (!validateLoginInput(username, password))
			return;
		
		// Fetch the user and verify the username
     	if (theDatabase.getUserAccountDetails(username) == false) {
     		// Don't provide too much information.  Don't say the username is invalid or the
     		// password is invalid.  Just say the pair is invalid.
    		ViewUserLogin.alertUsernamePasswordError.setContentText(
    				"Incorrect username/password. Try again!");
    		ViewUserLogin.alertUsernamePasswordError.showAndWait();
    		return;
    	}
		// System.out.println("*** Username is valid");
		
		// Check to see that the login password matches the account password
    	String actualPassword = theDatabase.getCurrentPassword();
    	
    	if (password.compareTo(actualPassword) != 0) {
    		ViewUserLogin.alertUsernamePasswordError.setContentText(
    				"Incorrect username/password. Try again!");
    		ViewUserLogin.alertUsernamePasswordError.showAndWait();
    		return;
    	}
		// System.out.println("*** Password is valid for this user");
		
		// Story AD-2: if this account is signing in with a one time password, a replacement has to
		// be chosen before anything else happens.  This sits after the password has been verified
		// and before any role is worked out, so the requirement cannot be stepped around by having
		// several roles or by any particular home page.  An account without the mark set skips
		// this block entirely and behaves exactly as it did before the story was implemented.
		if (theDatabase.isPasswordChangeRequired(username)) {
			String replacement = guiTools.ForcedPasswordChangeDialog.askForNewPassword(username);
			
			if (replacement == null) {
				// The dialog was closed without a replacement being supplied.  The sign in is
				// refused, leaving the account exactly as the administrator set it, so the
				// temporary password still has to be replaced on the next attempt.
				System.out.println("*** Sign in stopped: " + username
						+ " must choose a new password before continuing.");
				ViewUserLogin.alertUsernamePasswordError.setContentText(
						"You must choose a new password before you can continue. "
						+ "Please sign in again.");
				ViewUserLogin.alertUsernamePasswordError.showAndWait();
				return;
			}
			
			// Save the replacement and clear the mark, in that order, so a failure to save cannot
			// leave the account with the mark cleared and the old temporary password still in use.
			theDatabase.updatePassword(username, replacement);
			theDatabase.setForcePasswordChange(username, false);
			password = replacement;
			
			System.out.println("*** " + username + " chose a new password and may continue.");
			
			// The account details held in the database object were read with the old password, so
			// they are read again before the User below is built from them.
			theDatabase.getUserAccountDetails(username);
		}
		
		// Establish this user's details
    	User user = new User(username, password, theDatabase.getCurrentFirstName(), 
    			theDatabase.getCurrentMiddleName(), theDatabase.getCurrentLastName(), 
    			theDatabase.getCurrentPreferredFirstName(), theDatabase.getCurrentEmailAddress(), 
    			theDatabase.getCurrentAdminRole(), 
    			theDatabase.getCurrentNewRole1(), theDatabase.getCurrentNewRole2());
    	
    	// See which home page dispatch to use
		int numberOfRoles = theDatabase.getNumberOfRoles(user);		
		// System.out.println("*** The number of roles: "+ numberOfRoles);
		if (numberOfRoles == 1) {
			// Single Account Home Page - The user has no choice here
			
			// Admin role
			if (user.getAdminRole()) {
				loginResult = theDatabase.loginAdmin(user);
				if (loginResult) {
					guiAdminHome.ViewAdminHome.displayAdminHome(theStage, user);
				}
			} else if (user.getNewRole1()) {
				loginResult = theDatabase.loginRole1(user);
				if (loginResult) {
					guiRole1.ViewRole1Home.displayRole1Home(theStage, user);
				}
			} else if (user.getNewRole2()) {
				loginResult = theDatabase.loginRole2(user);
				if (loginResult) {
					guiRole2.ViewRole2Home.displayRole2Home(theStage, user);
				}
				// Other roles
			} else {
				System.out.println("***** UserLogin goToUserHome request has an invalid role");
			}
		} else if (numberOfRoles > 1) {
			// Multiple Account Home Page - The user chooses which role to play
			// System.out.println("*** Going to displayMultipleRoleDispatch");
			guiMultipleRoleDispatch.ViewMultipleRoleDispatch.
				displayMultipleRoleDispatch(theStage, user);
		}
	}
	
		
	/**********
	 * <p> Method: private validateLoginInput(String username, String password) </p>
	 * 
	 * <p> Description: Checks the username and the password typed on the login page before either
	 * is used to look anything up.
	 * 
	 * Only the length is checked, and that is deliberate.  An account created before a rule
	 * changed still has to be able to sign in, so applying the current username rules here would
	 * lock out existing users, which is a worse failure than the one it would prevent.  The
	 * supplied code also answers a failed login with a single "Incorrect username/password"
	 * message so that an attacker cannot learn which half of the pair was wrong, and reporting
	 * that a username is the wrong shape would undo that.
	 * 
	 * Length is a safety check rather than a rules check, and it belongs here because without it
	 * these values reached a database query unexamined.  The sequence itself lives in
	 * ScreenInputPolicy, which is the same code the automated tests drive, so this screen and its
	 * tests cannot disagree about what it does.</p>
	 * 
	 * @param username the username the user typed
	 * @param password the password the user typed
	 * 
	 * @return true when both values are an acceptable length and the login may proceed
	 */
	private static boolean validateLoginInput(String username, String password) {
		ScreenInputPolicy.Problem problem = ScreenInputPolicy.validateLogin(username, password);
		if (problem != null) {
			ValidationAlert.show(problem.title(), problem.detail());
			return false;
		}
		return true;
	}

	
	/**********
	 * <p> Method: setup() </p>
	 * 
	 * <p> Description: This method is called to reset the page and then populate it with new
	 * content for the new user.</p>
	 * 
	 */
	protected static void doSetupAccount(Stage theStage, String invitationCode) {
		
		// TP1 addition: the invitation code is checked here, at the point it is typed, so that an
		// over long value never reaches the database query that turns a code into a role.  Before
		// this, whatever was typed into the invitation field was passed straight through.
		ScreenInputPolicy.Problem problem =
				ScreenInputPolicy.validateInvitationCode(invitationCode);
		if (problem != null) {
			ValidationAlert.show(problem.title(), problem.detail());
			return;
		}
		
		guiNewAccount.ViewNewAccount.displayNewAccount(theStage, invitationCode);
	}

	
	/**********
	 * <p> Method: public performQuit() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Quit button.  Doing
	 * this terminates the execution of the application.  All important data must be stored in the
	 * database, so there is no cleanup required.  (This is important so we can minimize the impact
	 * of crashed.)
	 * 
	 */	
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	

}
