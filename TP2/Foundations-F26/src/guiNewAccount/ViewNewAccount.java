package guiNewAccount;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiTools.Theme;
import guiTools.ValidationAlert;
import validation.InputLimits;
import validation.PasswordRecognizer;
import validation.ScreenInputPolicy;

/*******
 * <p> Title: ViewNewAccount Class. </p>
 * 
 * <p> Description: The ViewNewAccount Page is used to enable a potential user with an invitation
 * code to establish an account after they have specified an invitation code on the standard login
 * page. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-19 Initial version
 *  
 */

public class ViewNewAccount {
	
	/*-********************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// This is a simple GUI login Page, very similar to the FirstAdmin login page.  The only real
	// difference is in this case we also know an email address, since it was used to send the
	// invitation to the potential user.
	private static Label label_ApplicationTitle = 
			new Label("Foundation Application");
    protected static Label label_NewUserCreation = new Label("Create your account");
    protected static Label label_NewUserLine = new Label("Username");
    protected static TextField text_Username = new TextField();
    protected static PasswordField text_Password1 = new PasswordField();
    protected static PasswordField text_Password2 = new PasswordField();
    protected static Button button_UserSetup = new Button("User Setup");

	// TP1 note: a TextField named text_Invitation used to be declared here.  It was never given a
	// position, never added to the page, and never populated, so reading it always produced an
	// empty string.  Two pieces of logic read it: the invitation length check, which therefore
	// never examined anything, and the call that removes an invitation once it has been used,
	// which therefore never removed anything and left used codes valid for ever.  Both now use
	// theInvitationCode, declared below, which is the value this page is actually opened with.

	// The live password requirement display.  Each label states one requirement and is rewritten
	// on every keystroke to say whether that requirement is currently met.  This mirrors the
	// supplied PasswordEvaluationTestbed application, where seeing the list change while typing is
	// what makes a valid password easy to arrive at.
	// NU-7: labels above the two password boxes, replacing prompt text inside them.
	protected static Label label_Password1Caption = new Label("Password");
	protected static Label label_Password2Caption = new Label("Password again");

	protected static Label label_PasswordRequirementsTitle =
			new Label("Your password must have:");
	protected static Label label_RequireUpperCase = new Label();
	protected static Label label_RequireLowerCase = new Label();
	protected static Label label_RequireDigit = new Label();
	protected static Label label_RequireSpecial = new Label();
	protected static Label label_RequireLongEnough = new Label();
	protected static Label label_RequireNotTooLong = new Label();
	protected static Label label_RequireValidCharacters = new Label();

	// This alert is used should the invitation code be invalid
    protected static Alert alertInvitationCodeIsInvalid = new Alert(AlertType.INFORMATION);

	// This alert is used should the user enter two passwords that do not match
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

    protected static Button button_Quit = new Button("Quit");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewNewAccount theView;		// Is instantiation of the class needed?

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current logged in User
   
    protected static String theInvitationCode;	// The invitation code links to an email address
    											// and a role for this user
    protected static String emailAddress;		// Established here for use by the controller
    protected static String theRole;			// Established here for use by the controller
	public static Scene theNewAccountScene = null;	// Access to the User Update page's GUI Widgets
	

	/*-********************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayNewAccount(Stage ps, String ic) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the NewAccount page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param ic specifies the user's invitation code for this GUI and it's methods
	 * 
	 */
	public static void displayNewAccount(Stage ps, String ic) {
		// This is the only way some component of the system can cause a New User Account page to
		// appear.  The first time, the class is created and initialized.  Every subsequent call it
		// is reused with only the elements that differ being initialized.
		
		// Establish the references to the GUI and the current user
		theStage = ps;				// Save the reference to the Stage for the rest of this package
		theInvitationCode = ic;		// Establish the invitation code so it can be easily accessed
		
		if (theView == null) theView = new ViewNewAccount();
		
		// TP1 addition: the invitation code is checked here, on arrival, before it is used to look
		// anything up.  The login page checks it too, at the point it is typed, but this page is
		// reachable from more than one place and should not depend on its caller having done the
		// work.  The check has to come before getRoleGivenAnInvitationCode below, because that is
		// the first thing that takes the code to the database.
		ScreenInputPolicy.Problem problem =
				ScreenInputPolicy.validateInvitationCode(theInvitationCode);
		if (problem != null) {
			ValidationAlert.show(problem.title(), problem.detail());
			return;					// Stop before any database lookup takes place
		}
		
		text_Username.setText("");	// Clear the input fields so previously entered values do not
		text_Password1.setText("");	// appear for a new user
		text_Password2.setText("");
		
		// Reset the requirement list to match the now empty password field.
		showPasswordRequirements("");
		
		// Fetch the role for this user.  The code has been checked by this point.
		theRole = theDatabase.getRoleGivenAnInvitationCode(theInvitationCode);
		
		if (theRole.length() == 0) {// If there is an issue with the invitation code, display a
			alertInvitationCodeIsInvalid.showAndWait();	// dialog box saying that are when it it
			return;					// acknowledged, return so the proper code can be entered
		}
		
		// Get the email address associated with the invitation code
		emailAddress = theDatabase.getEmailAddressUsingCode(theInvitationCode);
		
    	// Place all of the established GUI elements into the pane
    	theRootPane.getChildren().clear();
    	theRootPane.getChildren().addAll(label_NewUserCreation, label_NewUserLine, text_Username,
    			text_Password1, text_Password2, button_UserSetup, button_Quit,
    			label_Password1Caption, label_Password2Caption,
    			label_PasswordRequirementsTitle, label_RequireUpperCase, label_RequireLowerCase,
    			label_RequireDigit, label_RequireSpecial, label_RequireLongEnough,
    			label_RequireNotTooLong, label_RequireValidCharacters);    	

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundation Code: New User Account Setup");	
        theStage.setScene(theNewAccountScene);
		theStage.show();
	}
	
	/**********
	 * <p> Constructor: ViewNewAccount() </p>
	 * 
	 * <p> Description: This constructor is called just once, the first time a new account needs to
	 * be created.  It establishes all of the common GUI widgets for the page so they are only
	 * created once and reused when needed.
	 * 
	 * The do
	 * 		
	 */
	private ViewNewAccount() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theNewAccountScene = new Scene(theRootPane, width, height);

		// Label the Panle with the name of the startup screen, centered at the top of the pane
		// NU-7: the same centred column, type scale and single filled button as the login screen.
		Theme.title(label_ApplicationTitle, 22);
		
    	// Label to display the welcome message for the new user
    	Theme.title(label_NewUserCreation, 26);
	
    	// Label to display the  message for the first user
    	Theme.fieldLabel(label_NewUserLine, 96);
		
		// Establish the text input operand asking for a username
		Theme.field(text_Username, 118);
		
		// Establish the text input operand field for the password
		Theme.fieldLabel(label_Password1Caption, 162);
		Theme.field(text_Password1, 184);
		
		// TP1 addition: the requirement list is brought up to date on every change to this field,
		// rather than only when the button is pressed.  A change listener is used rather than a
		// key handler so that a pasted value updates the list as well as a typed one.
		text_Password1.textProperty().addListener((_, _, _)
				-> {ControllerNewAccount.passwordTyped(); });
		
		// Establish the text input operand field to confirm the password
		Theme.fieldLabel(label_Password2Caption, 228);
		Theme.field(text_Password2, 250);
		
		// The live requirement list, positioned under the two password fields.
		Theme.helperText(label_PasswordRequirementsTitle, 294);
		
		Theme.requirementLine(label_RequireUpperCase, 316, false);
		Theme.requirementLine(label_RequireLowerCase, 336, false);
		Theme.requirementLine(label_RequireDigit, 356, false);
		Theme.requirementLine(label_RequireSpecial, 376, false);
		Theme.requirementLine(label_RequireLongEnough, 396, false);
		Theme.requirementLine(label_RequireNotTooLong, 416, false);
		Theme.requirementLine(label_RequireValidCharacters, 436, false);
		// The seven requirement lines are positioned by the theme so their spacing matches the
		// rest of the page and they cannot collide with the button below them.
		// Start the list in its "nothing typed yet" state.
		showPasswordRequirements("");
		
		// If the invitation code is wrong, this alert dialog will tell the user
		alertInvitationCodeIsInvalid.setTitle("Invalid Invitation Code");
		alertInvitationCodeIsInvalid.setHeaderText("The invitation code is not valid.");
		alertInvitationCodeIsInvalid.setContentText("Correct the code and try again.");

		// If the passwords do not match, this alert dialog will tell the user
		alertUsernamePasswordError.setTitle("Passwords Do Not Match");
		alertUsernamePasswordError.setHeaderText("The two passwords must be identical.");
		alertUsernamePasswordError.setContentText("Correct the passwords and try again.");

        // Set up the account creation and login
        Theme.primaryButton(button_UserSetup, 490);
        button_UserSetup.setOnAction((_) -> {ControllerNewAccount.doCreateUser(); });
		
        // Enable the user to quit the application
        Theme.secondaryButtonCentred(button_Quit, 540, 120);
        button_Quit.setOnAction((_) -> {ControllerNewAccount.performQuit(); });
	}
	
	
	/**********
	 * <p> Method: showPasswordRequirements(String password) </p>
	 * 
	 * <p> Description: Rewrites the list of password requirements so it describes the password
	 * currently in the first password field.
	 * 
	 * The recognizer has already been run by the controller, so this method only reads the flags it
	 * left behind.  That is what guarantees the live list and the decision made when the button is
	 * pressed come from the same code: there is only one implementation of the rules, and this
	 * display is a view of its result rather than a second opinion.
	 * 
	 * Each line carries a text mark, "met" or "not met", as well as a colour.  Colour alone would
	 * leave the list unreadable for anyone who cannot distinguish red from green, and the text mark
	 * means the information survives being printed in black and white as well.
	 * 
	 * When a character outside the accepted groups is found, the recognizer records where it was.
	 * That position is reported here, counting from one rather than zero, because the user is
	 * looking at characters on a screen rather than at an array index.</p>
	 * 
	 * @param password the password currently typed into the first password field
	 */
	protected static void showPasswordRequirements(String password) {
		
		// With nothing typed, every requirement is shown as outstanding rather than as failed.
		// Telling a user they have failed a rule before they have typed anything would be noise.
		if (password == null || password.isEmpty()) {
			setRequirement(label_RequireUpperCase, false, "An upper case letter, A to Z");
			setRequirement(label_RequireLowerCase, false, "A lower case letter, a to z");
			setRequirement(label_RequireDigit, false, "A digit, 0 to 9");
			setRequirement(label_RequireSpecial, false, "A special character, such as ! or ?");
			setRequirement(label_RequireLongEnough, false,
					"At least " + PasswordRecognizer.MIN_LENGTH + " characters");
			setRequirement(label_RequireNotTooLong, false,
					"No more than " + InputLimits.MAX_PASSWORD + " characters");
			setRequirement(label_RequireValidCharacters, false, "No unsupported characters");
			return;
		}
		
		setRequirement(label_RequireUpperCase, PasswordRecognizer.foundUpperCase,
				"An upper case letter, A to Z");
		setRequirement(label_RequireLowerCase, PasswordRecognizer.foundLowerCase,
				"A lower case letter, a to z");
		setRequirement(label_RequireDigit, PasswordRecognizer.foundNumericDigit,
				"A digit, 0 to 9");
		setRequirement(label_RequireSpecial, PasswordRecognizer.foundSpecialChar,
				"A special character, such as ! or ?");
		setRequirement(label_RequireLongEnough, PasswordRecognizer.foundLongEnough,
				"At least " + PasswordRecognizer.MIN_LENGTH + " characters");
		
		// The maximum length requirement is the one the supplied state diagram does not test.
		setRequirement(label_RequireNotTooLong, !PasswordRecognizer.foundTooLong,
				"No more than " + InputLimits.MAX_PASSWORD + " characters");
		
		// An unsupported character stops the recognizer, and the position it stopped at is the
		// position of the offending character.
		boolean invalidCharacterFound = PasswordRecognizer.passwordErrorMessage
				.contains("invalid character");
		if (invalidCharacterFound) {
			int position = PasswordRecognizer.passwordIndexofError + 1;
			setRequirement(label_RequireValidCharacters, false,
					"No unsupported characters. Character " + position + " is not allowed");
		} else {
			setRequirement(label_RequireValidCharacters, true, "No unsupported characters");
		}
	}
	
	/**********
	 * <p> Method: setRequirement(Label theLabel, boolean isMet, String requirement) </p>
	 * 
	 * <p> Description: Writes one line of the requirement list, marking it in words and in colour.
	 * 
	 * The words come first in the text so that the state of the requirement is read before the
	 * requirement itself, which is the order that makes the list quick to scan for whatever is
	 * still outstanding.</p>
	 * 
	 * @param theLabel    the label for this requirement
	 * @param isMet       true when the password currently satisfies this requirement
	 * @param requirement the wording of the requirement
	 */
	private static void setRequirement(Label theLabel, boolean isMet, String requirement) {
		// Only the words and the colour change here.  The position of each line is set once in the
		// constructor, by the theme, so the list keeps the same spacing as the rest of the page and
		// cannot drift into the button below it as the text changes.
		if (isMet) {
			theLabel.setText("[met]      " + requirement);
			theLabel.setTextFill(Color.web("#1B7F3B"));
		} else {
			theLabel.setText("[not met]  " + requirement);
			theLabel.setTextFill(Color.web("#B3261E"));
		}
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	
}
