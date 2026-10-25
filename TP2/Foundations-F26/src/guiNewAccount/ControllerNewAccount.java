package guiNewAccount;

import java.sql.SQLException;

import database.Database;
import entityClasses.User;
import guiTools.ValidationAlert;
import validation.PasswordRecognizer;
import validation.ScreenInputPolicy;

/*******
 * <p> Title: ControllerNewAccount Class. </p>
 * 
 * <p> Description: The Java/FX-based New Account Page.  This class provides the controller actions
 * to allow the user to establish a new account after responding to an invitation and the use of a
 * one time code.
 * 
 * The controller deals with the user pressing the "User Step" button widget being click.  If also
 * supports the user click on the "Quit" button widget.
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
 *  
 */

public class ControllerNewAccount {
	
	/*-********************************************************************************************

	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerNewAccount() {
	}
	
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**********
	 * <p> Method: public doCreateUser() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the User Setup
	 * button.  This method checks the input fields to see that they are valid.  If so, it then
	 * creates the account by adding information to the database.
	 * 
	 * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 * 
	 */	
	protected static void doCreateUser() {
		
		// Fetch the username and password. (We use the first of the two here, but we will validate
		// that the two password fields are the same before we do anything with it.)
		String username = ViewNewAccount.text_Username.getText();
		String password = ViewNewAccount.text_Password1.getText();
		
		// TP1 addition: check everything the user typed before any of it is used.  The checks
		// run in the order the user reads the form, so the first thing they are told about is
		// the first thing that is wrong.  Nothing below this block runs until every field is
		// acceptable, which is what keeps unchecked input away from the database.
		if (!validateNewAccountInput(username, password))
			return;
		
		// Display key information to the log
		System.out.println("** Account for Username: " + username + "; theInvitationCode: "+
				ViewNewAccount.theInvitationCode + "; email address: " + 
				ViewNewAccount.emailAddress + "; Role: " + ViewNewAccount.theRole);
		
		// Initialize local variables that will be created during this process
		int roleCode = 0;
		User user = null;

		// The two passwords were compared as part of the validation above, so by this point they
		// are known to be identical and both known to be usable.
		
		// Set up the role and the User object based on the information provided in the invitation
		if (ViewNewAccount.theRole.compareTo("Admin") == 0) {
			roleCode = 1;
			user = new User(username, password, "", "", "", "", "", true, false, false);
		} else if (ViewNewAccount.theRole.compareTo("Role1") == 0) {
			roleCode = 2;
			user = new User(username, password, "", "", "", "", "", false, true, false);
		} else if (ViewNewAccount.theRole.compareTo("Role2") == 0) {
			roleCode = 3;
			user = new User(username, password, "", "", "", "", "", false, false, true);
		} else {
			System.out.println(
					"**** Trying to create a New Account for a role that does not exist!");
			System.exit(0);
		}
		
		// Unlike the FirstAdmin, we know the email address, so set that into the user as well.
		user.setEmailAddress(ViewNewAccount.emailAddress);

		// Inform the system about which role will be played
		applicationMain.FoundationsMain.activeHomePage = roleCode;
		
		// Create the account based on user and proceed to the user account update page
		try {
			// Create a new User object with the pre-set role and register in the database
			theDatabase.register(user);
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		
		// The account has been set, so remove the invitation from the system.
		//
		// TP1 correction: this previously read the code from a TextField on this page that was
		// never placed on the page and never populated, so it always supplied an empty string.
		// The database method only deletes a row when it finds a matching code, so nothing was
		// ever deleted and no error was reported.  The effect was that a used invitation code
		// stayed valid and could be used again to create further accounts.  The value that
		// actually opened this page is theInvitationCode, so that is what is removed.
		theDatabase.removeInvitationAfterUse(ViewNewAccount.theInvitationCode);
		
		// Set the database so it has this user and the current user
		theDatabase.getUserAccountDetails(username);

		// Navigate to the Welcome Login Page
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewNewAccount.theStage, user);
	}

	
	/**********
	 * <p> Method: protected passwordTyped() </p>
	 * 
	 * <p> Description: Called by the View every time the contents of the first password field
	 * change, so the list of requirements on the page can be brought up to date as the user types
	 * rather than only when the button is pressed.
	 * 
	 * This is the behaviour of the supplied PasswordEvaluationTestbed application, and the reason
	 * for it is that a user who is told what is still missing while they are typing can correct it
	 * immediately, whereas a user who is only told after pressing a button has to work out which
	 * of several requirements they failed.
	 * 
	 * The display is driven by the same recognizer the button uses.  The recognizer is called once
	 * here and the flags it leaves behind are read to build the list, so the live display and the
	 * final decision are produced by the same code and cannot disagree.</p>
	 * 
	 */
	protected static void passwordTyped() {
		String password = ViewNewAccount.text_Password1.getText();
		
		// Calling the recognizer sets the flags that describe this input.  The returned message is
		// not shown here, because a message under every keystroke is noise; the requirement list
		// and the invalid character note carry the information instead.
		PasswordRecognizer.evaluatePassword(password);
		
		ViewNewAccount.showPasswordRequirements(password);
	}

	
	/**********
	 * <p> Method: private validateNewAccountInput(String username, String password) </p>
	 * 
	 * <p> Description: Checks every value on the New Account page before any of it is used.
	 * 
	 * The sequence itself lives in ScreenInputPolicy, so that this screen and the automated tests
	 * run the same checks in the same order.  It is applied in two parts because the database has
	 * to be consulted in the middle: the invitation code and the username are checked first, the
	 * database is then asked whether that username is already taken, and the two password fields
	 * are checked afterwards.  Asking the database first would mean querying it with a value
	 * already known to be unusable.</p>
	 * 
	 * @param username the username the user typed
	 * @param password the first of the two passwords the user typed
	 * 
	 * @return true when every field is acceptable and the account may be created
	 */
	private static boolean validateNewAccountInput(String username, String password) {
		
		String secondPassword = ViewNewAccount.text_Password2.getText();
		
		// TP1 correction: the invitation code checked here is theInvitationCode, the value this
		// page was actually opened with.  It previously read a TextField that was never added to
		// the page, so the check ran against an empty string every time and could never fail.
		String invitationCode = ViewNewAccount.theInvitationCode;
		
		// Part one: everything that can be decided without the database.
		ScreenInputPolicy.Problem problem =
				ScreenInputPolicy.validateNewAccountBeforeLookup(invitationCode, username);
		if (problem != null) {
			ValidationAlert.show(problem.title(), problem.detail());
			return false;
		}
		
		// A username that is already taken is refused here rather than being allowed to fail
		// inside the database, where the message would mean nothing to the user.
		if (theDatabase.doesUserExist(username)) {
			ValidationAlert.show("That username is already taken",
					"Someone already has the username " + username + ". Please choose another.");
			return false;
		}
		
		// Part two: both password fields, including the confirmation box, and the comparison.
		problem = ScreenInputPolicy.validateNewAccountAfterLookup(password, secondPassword);
		if (problem != null) {
			ValidationAlert.show(problem.title(), problem.detail());
			
			// Both boxes are cleared so the user retypes them rather than editing something that
			// was already refused, and the requirement list is reset to match the empty field.
			ViewNewAccount.text_Password1.setText("");
			ViewNewAccount.text_Password2.setText("");
			ViewNewAccount.showPasswordRequirements("");
			return false;
		}
		
		return true;
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
