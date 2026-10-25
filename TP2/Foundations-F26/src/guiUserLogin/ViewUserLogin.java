package guiUserLogin;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import guiTools.Theme;


/*******
 * <p> Title: ViewUserLogin Class. </p>
 * 
 * <p> Description: The Java/FX-based System Startup Page.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-04-20 Initial version
 *  
 */

public class ViewUserLogin {

	/*-********************************************************************************************

	Attributes

	 *********************************************************************************************/

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static Label label_ApplicationTitle = new Label("Foundation Application");

	// This set is for all subsequent starts of the system
	private static Label label_OperationalStartTitle = new Label("Sign in to continue");
	private static Label label_LogInInsrtuctions = new Label("Username");
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

	// NU-7: every field is named by a label above it rather than by prompt text inside it, so the
	// name is still there once the user has started typing.  These labels are the ones that were
	// previously carried as prompt text.
	private static Label label_Password = new Label("Password");
	private static Label label_InvitationCode = new Label("Invitation code");
	private static Label label_Separator = new Label();


	//	private User user;
	protected static TextField text_Username = new TextField();
	protected static PasswordField text_Password = new PasswordField();
	private static Button button_Login = new Button("Log In");	

	private static Label label_AccountSetupInsrtuctions = new Label("No account yet? "+	
			"Enter the invitation code you were sent to set one up.");
	private static TextField text_Invitation = new TextField();
	private static Button button_SetupAccount = new Button("Set Up Account");

	private static Button button_Quit = new Button("Quit");

	private static Stage theStage;	
	private static Pane theRootPane;
	public static Scene theUserLoginScene = null;	


	private static ViewUserLogin theView = null;	//	private static guiUserLogin.ControllerUserLogin theController;


	/*-********************************************************************************************

	Constructor

	 *********************************************************************************************/

	public static void displayUserLogin(Stage ps) {
		
		// Establish the references to the GUI. There is no current user yet.
		theStage = ps;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewUserLogin();
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.		
		text_Username.setText("");		// Reset the username and password from the last use
		text_Password.setText("");
		text_Invitation.setText("");	// Same for the invitation code

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundation Code: User Login Page");		
		theStage.setScene(theUserLoginScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewUserLoginPage() </p>
	 * 
	 * <p> Description: This method is called when the application first starts. It must handle
	 * two cases: 1) when no has been established and 2) when one or more users have been 
	 * established.
	 * 
	 * If there are no users in the database, this means that the person starting the system jmust
	 * be an administrator, so a special GUI is provided to allow this Admin to set a username and
	 * password.
	 * 
	 * If there is at least one user, then a different display is shown for existing users to login
	 * and for potential new users to provide an invitation code and if it is valid, they are taken
	 * to a page where they can specify a username and password.</p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param theRoot specifies the JavaFX Pane to be used for this GUI and it's methods
	 * 
	 * @param db specifies the Database to be used by this GUI and it's methods
	 * 
	 */
	private ViewUserLogin() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theUserLoginScene = new Scene(theRootPane, width, height);

		// NU-7: the whole form sits in one narrow centred column, which was the single layout
		// every professional sign-in page the team looked at had in common.  All of the
		// measurements and the styling come from guiTools.Theme so that this screen and the other
		// non-Admin screens cannot drift apart.
		Theme.title(label_ApplicationTitle, 30);
		Theme.subtitle(label_OperationalStartTitle, 66);

		// The sign-in part of the page: two labelled fields and the one filled button.
		Theme.fieldLabel(label_LogInInsrtuctions, 112);
		Theme.field(text_Username, 134);

		Theme.fieldLabel(label_Password, 182);
		Theme.field(text_Password, 204);

		Theme.primaryButton(button_Login, 254);
		button_Login.setOnAction((_) -> {ControllerUserLogin.doLogin(theStage); });

		alertUsernamePasswordError.setTitle("Invalid username/password!");
		alertUsernamePasswordError.setHeaderText(null);

		// A quiet rule separates the secondary route into the application from the primary one,
		// the way the professional pages set alternative sign-in methods apart.
		Theme.separator(label_Separator, 312);

		// The invitation part: supporting text first, then a labelled field, then a button that is
		// deliberately not filled so it cannot be mistaken for the main action.
		Theme.helperText(label_AccountSetupInsrtuctions, 326);
		Theme.fieldLabel(label_InvitationCode, 368);
		Theme.field(text_Invitation, 390);

		Theme.secondaryButton(button_SetupAccount, Theme.COLUMN_LEFT, 436, Theme.COLUMN_WIDTH);
		button_SetupAccount.setOnAction((_) -> {
			System.out.println("**** Calling doSetupAccount");
			ControllerUserLogin.doSetupAccount(theStage, text_Invitation.getText());
		});

		// Quit belongs at the foot of the page, quiet and out of the way of the main task.
		Theme.secondaryButtonCentred(button_Quit, 530, 120);
		button_Quit.setOnAction((_) -> {ControllerUserLogin.performQuit(); });

		theRootPane.getChildren().addAll(
				label_ApplicationTitle, 
				label_OperationalStartTitle,
				label_LogInInsrtuctions, label_Password, text_Username, text_Password,
				button_Login,
				label_Separator, label_AccountSetupInsrtuctions,
				label_InvitationCode, text_Invitation, button_SetupAccount,
				button_Quit);
	}


	/*-********************************************************************************************

	Helper methods to reduce code length

	 *********************************************************************************************/

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
