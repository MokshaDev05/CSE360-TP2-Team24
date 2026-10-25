package guiUserUpdate;

import java.util.Optional;

import database.Database;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import guiTools.Theme;
import entityClasses.User;

/*******
 * <p> Title: ViewUserUpdate Class. </p>
 * 
 * <p> Description: The Java/FX-based User Update Page.  This page enables the user to update the
 * attributes about the user held by the system.  Currently, this page does not provide a mechanism
 * to change the Username and not all of the functions on this page are implemented.
 * 
 * Currently the following attributes can be updated:
 * 		- First Name
 * 		- Middle Name
 * 		- Last Name
 * 		- Preferred First Name
 * 		- Email Address
 * The page uses dialog boxes for updating these items.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.01		2025-08-19 Initial version plus new internal documentation
 *  
 */

public class ViewUserUpdate {

	/*-********************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// Unlike may of the other pages, the GUI on this page is not organized into areas and the user
	// is not able to logout, return, or quit from this page
	
	// These widgets display the purpose of the page and guide the user.
	private static Label label_ApplicationTitle = new Label("Update a User's Account Details");
    private static Label label_Purpose = 
    		new Label(" Use this page to define or update your account information."); 
    
    // These are static output labels and do not change during execution
	private static Label label_Username = new Label("Username:");
	private static Label label_Password = new Label("Password:");
	private static Label label_FirstName = new Label("First Name:");
	private static Label label_MiddleName = new Label("Middle Name:");
	private static Label label_LastName = new Label("Last Name:");
	private static Label label_PreferredFirstName = new Label("Preferred First Name:");
	private static Label label_EmailAddress = new Label("Email Address:");
	
	// These are dynamic labels and they change based on the user and user interactions.
	protected static Label label_CurrentUsername = new Label();
	protected static Label label_CurrentPassword = new Label();
	protected static Label label_CurrentFirstName = new Label();
	protected static Label label_CurrentMiddleName = new Label();
	protected static Label label_CurrentLastName = new Label();
	protected static Label label_CurrentPreferredFirstName = new Label();
	protected static Label label_CurrentEmailAddress = new Label();
	
	// These buttons enable the user to edit the various dynamic fields.  The username and the
	// passwords for a user are currently not editable.
	private static Button button_UpdateUsername = new Button("Update Username");
	private static Button button_UpdatePassword = new Button("Update Password");
	private static Button button_UpdateFirstName = new Button("Update First Name");
	private static Button button_UpdateMiddleName = new Button("Update Middle Name");
	private static Button button_UpdateLastName = new Button("Update Last Name");
	private static Button button_UpdatePreferredFirstName = new Button("Update Preferred First Name");
	private static Button button_UpdateEmailAddress = new Button("Update Email Address");

	// This button enables the user to finish working on this page and proceed to the user's home
	// page determined by the user's role at the time of log in.
	private static Button button_ProceedToUserHomePage = new Button("Proceed to the User Home Page");
	
	// This is the end of the GUI widgets for this page.
	
	// These are the set of pop-up dialog boxes that are used to enable the user to change the
	// the values of the various account detail items.
	protected static TextInputDialog dialogUpdateFirstName;
	protected static TextInputDialog dialogUpdateMiddleName;
	protected static TextInputDialog dialogUpdateLastName;
	protected static TextInputDialog dialogUpdatePreferredFirstName;
	protected static TextInputDialog dialogUpdateEmailAddresss;
	
	// TP1 addition: dialogs for the two buttons that were present but had no action attached.
	protected static TextInputDialog dialogUpdateUsername;
	protected static TextInputDialog dialogUpdatePassword;
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewUserUpdate theView;	// Used to determine if instantiation of the class
											// is needed

	// This enables access to the application's database
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	private static Stage theStage;				// The Stage that JavaFX has established for us	
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current user of the application

	/**
	 * The scene that each invocation of the user update page populates.
	 */
	public static Scene theUserUpdateScene = null;// The Scene each invocation populates

	private static Optional<String> result;		// The result from a pop-up dialog

	/*-********************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayUserUpdate(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the UserUpdate page to be displayed.
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
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayUserUpdate(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theUser = user;
		theStage = ps;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewUserUpdate();
		
		// Set the widget values that change from use of page to another use of the page.
		String s = "";
		
		// Set the dynamic aspects of the window based on the user logged in and the current state
		// of the various account elements.
		s = theUser.getUserName();
		System.out.println("*** Fetching account data for user: " + s);
    	if (s == null || s.length() < 1)label_CurrentUsername.setText("<none>");
    	else label_CurrentUsername.setText(s);
		
		s = theUser.getPassword();
    	// NU-7: the password is shown as a row of dots rather than as itself.  The team already
    	// applied this reasoning to the Admin list of users, where passwords are left out because
    	// anybody glancing at the screen could read them, and the same argument applies here.  The
    	// length is not revealed either: a fixed number of dots is shown, so the display says a
    	// password is set without saying anything about what it is.
    	if (s == null || s.length() < 1) label_CurrentPassword.setText("<none>");
    	else label_CurrentPassword.setText("\u2022".repeat(10));
    	
		s = theUser.getFirstName();
    	if (s == null || s.length() < 1)label_CurrentFirstName.setText("<none>");
    	else label_CurrentFirstName.setText(s);
       
        s = theUser.getMiddleName();
    	if (s == null || s.length() < 1)label_CurrentMiddleName.setText("<none>");
    	else label_CurrentMiddleName.setText(s);
        
        s = theUser.getLastName();
    	if (s == null || s.length() < 1)label_CurrentLastName.setText("<none>");
    	else label_CurrentLastName.setText(s);
        
		s = theUser.getPreferredFirstName();
    	if (s == null || s.length() < 1)label_CurrentPreferredFirstName.setText("<none>");
    	else label_CurrentPreferredFirstName.setText(s);
        
		s = theUser.getEmailAddress();
    	if (s == null || s.length() < 1)label_CurrentEmailAddress.setText("<none>");
    	else label_CurrentEmailAddress.setText(s);

		// Set the title for the window, display the page, and wait for the Admin to do something
    	theStage.setTitle("CSE 360 Foundation Code: Update User Account Details");
        theStage.setScene(theUserUpdateScene);
		theStage.show();
	}

	
	/**********
	 * <p> Method: ViewUserUpdate() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayUserUpdate method.</p>
	 * 
	 */
	
	private ViewUserUpdate() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theUserUpdateScene = new Scene(theRootPane, width, height);

		// Initialize the pop-up dialogs to an empty text filed.
		dialogUpdateFirstName = new TextInputDialog("");
		dialogUpdateMiddleName = new TextInputDialog("");
		dialogUpdateLastName = new TextInputDialog("");
		dialogUpdatePreferredFirstName = new TextInputDialog("");
		dialogUpdateEmailAddresss = new TextInputDialog("");
		dialogUpdateUsername = new TextInputDialog("");
		dialogUpdatePassword = new TextInputDialog("");

		// Establish the label for each of the dialogs.
		dialogUpdateFirstName.setTitle("Update First Name");
		dialogUpdateFirstName.setHeaderText("Update your First Name");
		
		dialogUpdateMiddleName.setTitle("Update Middle Name");
		dialogUpdateMiddleName.setHeaderText("Update your Middle Name");
		
		dialogUpdateLastName.setTitle("Update Last Name");
		dialogUpdateLastName.setHeaderText("Update your Last Name");
		
		dialogUpdatePreferredFirstName.setTitle("Update Preferred First Name");
		dialogUpdatePreferredFirstName.setHeaderText("Update your Preferred First Name");
		
		dialogUpdateEmailAddresss.setTitle("Update Email Address");
		dialogUpdateEmailAddresss.setHeaderText("Update your Email Address");
		
		dialogUpdateUsername.setTitle("Update Username");
		dialogUpdateUsername.setHeaderText("Update your Username");
		dialogUpdateUsername.setContentText("New username (4 to 16 characters, starting with a "
				+ "letter):");
		
		dialogUpdatePassword.setTitle("Update Password");
		dialogUpdatePassword.setHeaderText("Update your Password");
		dialogUpdatePassword.setContentText("New password (upper case, lower case, digit, "
				+ "special character, at least 8 characters):");

		// Label theScene with the name of the startup screen, centered at the top of the pane
		// NU-7: this page is a list of current values with one action beside each, so the layout
		// is a wider two-column grid rather than the narrow form column.  The type scale, the
		// button weights and the one-filled-button rule are the same as every other screen.
		Theme.title(label_ApplicationTitle, 24);

        // Label to display the welcome message for the first theUser
		Theme.subtitle(label_Purpose, 60);
        
        // Display the titles, values, and update buttons for the various admin account attributes.
        // If the attributes is null or empty, display "<none>".
        
        // USername
		Theme.rowLabel(label_Username, 108);
		Theme.secondaryButton(button_UpdateUsername, 560, 102, 210);
        // TP1 addition: this button was present in the supplied code but had no action attached
        // to it, so pressing it did nothing at all.  It now opens a dialog, checks the new name
        // with the same recognizer used when the account was created, and saves it.
        button_UpdateUsername.setOnAction((_) -> ControllerUserUpdate.doUpdateUsername());
       
        // password
		Theme.rowLabel(label_Password, 156);
		Theme.secondaryButton(button_UpdatePassword, 560, 150, 210);
        // TP1 addition: as with the username button above, this had no action attached.
        button_UpdatePassword.setOnAction((_) -> ControllerUserUpdate.doUpdatePassword());
        
        // First Name
		Theme.rowLabel(label_FirstName, 204);
		Theme.secondaryButton(button_UpdateFirstName, 560, 198, 210);
        // TP1 change: the supplied version wrote whatever was typed straight into the database.
        // The work now goes through the controller, which checks the value first.
        button_UpdateFirstName.setOnAction((_) -> ControllerUserUpdate.doUpdateFirstName());
               
        // Middle Name
		Theme.rowLabel(label_MiddleName, 252);
		Theme.secondaryButton(button_UpdateMiddleName, 560, 246, 210);
        button_UpdateMiddleName.setOnAction((_) -> ControllerUserUpdate.doUpdateMiddleName());
        
        // Last Name
		Theme.rowLabel(label_LastName, 300);
		Theme.secondaryButton(button_UpdateLastName, 560, 294, 210);
        button_UpdateLastName.setOnAction((_) -> ControllerUserUpdate.doUpdateLastName());
        
        // Preferred First Name
		Theme.rowLabel(label_PreferredFirstName, 348);
		Theme.secondaryButton(button_UpdatePreferredFirstName, 560, 342, 210);
        button_UpdatePreferredFirstName.setOnAction((_) ->
        	ControllerUserUpdate.doUpdatePreferredFirstName());
        
        // Email Address
		Theme.rowLabel(label_EmailAddress, 396);
		Theme.secondaryButton(button_UpdateEmailAddress, 560, 390, 210);
        button_UpdateEmailAddress.setOnAction((_) -> ControllerUserUpdate.doUpdateEmailAddress());
        
        // Set up the button to proceed to this user's home page
		// NU-7: each current value sits in its own column beside the name of the field, and the
		// button that changes it sits to the right of that, so the three line up down the page.
		Theme.valueLabel(label_CurrentUsername, 250, 108, 300);
		Theme.valueLabel(label_CurrentPassword, 250, 156, 300);
		Theme.valueLabel(label_CurrentFirstName, 250, 204, 300);
		Theme.valueLabel(label_CurrentMiddleName, 250, 252, 300);
		Theme.valueLabel(label_CurrentLastName, 250, 300, 300);
		Theme.valueLabel(label_CurrentPreferredFirstName, 250, 348, 300);
		Theme.valueLabel(label_CurrentEmailAddress, 250, 396, 300);

		Theme.primaryButton(button_ProceedToUserHomePage, 470);
        button_ProceedToUserHomePage.setOnAction((_) -> 
        	{ControllerUserUpdate.goToUserHomePage(theStage, theUser);});
    	
        // Populate the Pane's list of children widgets
        theRootPane.getChildren().addAll(
        		label_ApplicationTitle, label_Purpose, label_Username,
        		label_CurrentUsername, button_UpdateUsername,
        		label_Password, label_CurrentPassword, 
        		button_UpdatePassword, 
        		label_FirstName, label_CurrentFirstName, button_UpdateFirstName,
        		label_MiddleName, label_CurrentMiddleName, button_UpdateMiddleName,
        		label_LastName, label_CurrentLastName, button_UpdateLastName,
        		label_PreferredFirstName, label_CurrentPreferredFirstName,
        		button_UpdatePreferredFirstName, button_UpdateEmailAddress,
        		label_EmailAddress, label_CurrentEmailAddress, 
        		button_ProceedToUserHomePage);
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}
