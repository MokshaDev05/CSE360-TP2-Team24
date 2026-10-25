package guiLessonsList;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;
import guiTools.Theme;

/*******
 * <p> Title: ViewLessonsList Class. </p>
 *
 * <p> Description: The Java/FX-based Lessons Learned list page.  It is the page a contributor
 * reaches from their home page, and it shows every stored lesson, newest first, with a search
 * box, a category filter, and an "only my lessons" choice above the list.  From here the
 * contributor can record a new lesson or open the selected one. </p>
 *
 * <p> The page is laid out the way every non-Admin page in TP1 is: the title centred at the top,
 * a label above each control rather than placeholder text inside it, exactly one filled button
 * (New Lesson), and Back and Quit drawn quietly at the foot of the page.  The column is wider than
 * on the login pages because a lesson line needs the room; the reasons for every other choice are
 * recorded in guiTools.Theme and in TP1's User Experience.pdf. </p>
 *
 * <p> As in every supplied View, this class is a singleton.  The constructor builds the widgets
 * once; {@link #displayLessonsList} fills in what changes each time the page is shown. </p>
 *
 * <p> Contributor stories supported: CS-2, CS-3, CS-4, CS-5, CS-6, and the entry point to CS-1.
 * </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-16  Initial version for Team Project Phase 2
 */
public class ViewLessonsList {

	/*-*******************************************************************************************

	Attributes

	 */

	// The window size every page uses, taken from the main class so all pages match.
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// The left edge and width of this page's content.  Wider than Theme.COLUMN_WIDTH because one
	// line of the list shows an identifier, a category, a title, and a contributor.
	private static final double LEFT = 60;
	private static final double CONTENT_WIDTH = 680;

	// GUI Area 1: what this page is and whose account is using it.
	protected static Label label_PageTitle = new Label("Lessons Learned");
	protected static Label label_UserDetails = new Label();

	// GUI Area 2: narrowing the list.  CS-4 search, CS-5 category filter, CS-6 my lessons.
	protected static Label label_Search = new Label("Search the title, description, and keywords");
	protected static TextField text_Search = new TextField();
	protected static Button button_Search = new Button("Search");
	protected static Button button_ShowAll = new Button("Show All");
	protected static Label label_Category = new Label("Category");
	protected static ComboBox<String> combo_Category = new ComboBox<>();
	protected static CheckBox check_OnlyMine = new CheckBox("Only my lessons");

	// GUI Area 3: the list itself (CS-2) and the line that explains it.
	protected static ListView<String> list_Lessons = new ListView<>();
	protected static Label label_Status = new Label();

	// GUI Area 4: what can be done with the list.  CS-1 and CS-3.
	protected static Button button_NewLesson = new Button("New Lesson");
	protected static Button button_OpenLesson = new Button("Open Selected Lesson");

	// GUI Area 5: leaving the page.
	protected static Button button_Back = new Button("Back to Home");
	protected static Button button_Quit = new Button("Quit");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewLessonsList theView;		// The singleton instance, null until first use

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User

	private static Scene theLessonsListScene;	// The shared Scene each invocation populates


	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayLessonsList(Stage ps, User user) </p>
	 *
	 * <p> Description: The single entry point from outside this package that displays the
	 * Lessons Learned list page.  It establishes the shared attributes, instantiates the singleton
	 * the first time, reads every lesson from the database so the list is current, clears any
	 * search left over from an earlier visit, and shows the page. </p>
	 *
	 * @param ps   the JavaFX Stage to be used for this GUI and its methods
	 * @param user the User for this GUI and its methods
	 */
	public static void displayLessonsList(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewLessonsList();

		// Populate the dynamic aspects of the GUI with the current user and the stored lessons.
		label_UserDetails.setText("User: " + theUser.getUserName());
		ControllerLessonsList.reloadLessons();

		// A fresh visit starts with no search, all categories, and every contributor's lessons,
		// so the contributor sees the whole list first (CS-2).
		text_Search.setText("");
		check_OnlyMine.setSelected(false);
		ControllerLessonsList.fillCategories();
		ControllerLessonsList.applyFilters();

		theStage.setTitle("CSE 360 Foundations: Lessons Learned");
		theStage.setScene(theLessonsListScene);
		theStage.show();
	}


	/**********
	 * <p> Method: refreshList() </p>
	 *
	 * <p> Description: Shows the current list again without reading the database, used when the
	 * user returns from the detail page or the editor.  The shared list in the Model already holds
	 * any change those pages made, and the search and filters the user had set are kept, so they
	 * come back to what they were looking at. </p>
	 *
	 * @param ps   the JavaFX Stage
	 * @param user the logged in User
	 */
	public static void refreshList(Stage ps, User user) {
		if (theView == null) {
			displayLessonsList(ps, user);
			return;
		}
		theStage = ps;
		theUser = user;
		ControllerLessonsList.fillCategories();
		ControllerLessonsList.applyFilters();
		theStage.setTitle("CSE 360 Foundations: Lessons Learned");
		theStage.setScene(theLessonsListScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewLessonsList() </p>
	 *
	 * <p> Description: Initializes every element of the page: location, size, font, and the
	 * handler for each control.  This is a singleton and is only performed once. </p>
	 */
	private ViewLessonsList() {

		theRootPane = new Pane();
		theLessonsListScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		Theme.title(label_PageTitle, 18);
		Theme.subtitle(label_UserDetails, 56);

		// GUI Area 2
		Theme.fieldLabel(label_Search, 88);
		label_Search.setLayoutX(LEFT);
		Theme.field(text_Search, 110);
		text_Search.setLayoutX(LEFT);
		text_Search.setMinWidth(420);
		text_Search.setMaxWidth(420);
		// Pressing Enter in the search box searches, as it does in every search box the team
		// looked at; making the user reach for the button would be the odd one out.
		text_Search.setOnAction((_) -> {ControllerLessonsList.applyFilters(); });

		Theme.secondaryButton(button_Search, LEFT + 430, 112, 110);
		button_Search.setOnAction((_) -> {ControllerLessonsList.applyFilters(); });
		Theme.secondaryButton(button_ShowAll, LEFT + 550, 112, 130);
		button_ShowAll.setOnAction((_) -> {ControllerLessonsList.showAll(); });

		Theme.fieldLabel(label_Category, 152);
		label_Category.setLayoutX(LEFT);
		combo_Category.setLayoutX(LEFT);
		combo_Category.setLayoutY(174);
		combo_Category.setMinWidth(260);
		// The filter takes effect as soon as a category is chosen, because choosing one is
		// already a clear request; a separate Apply button would be a step with no purpose.
		combo_Category.setOnAction((_) -> {ControllerLessonsList.applyFilters(); });

		check_OnlyMine.setFont(Font.font(Theme.FONT, 13));
		check_OnlyMine.setLayoutX(LEFT + 290);
		check_OnlyMine.setLayoutY(178);
		check_OnlyMine.setOnAction((_) -> {ControllerLessonsList.applyFilters(); });

		// GUI Area 3.  A fixed width font keeps the identifier and category columns lined up.
		list_Lessons.setLayoutX(LEFT);
		list_Lessons.setLayoutY(214);
		list_Lessons.setPrefSize(CONTENT_WIDTH, 230);
		list_Lessons.setStyle("-fx-font-family: 'Menlo', 'Courier New', monospace;"
				+ "-fx-font-size: 13px;");
		// A double click opens the lesson, the same as selecting it and pressing the button.
		list_Lessons.setOnMouseClicked((e) -> {
			if (e.getClickCount() == 2) ControllerLessonsList.openSelectedLesson(); });

		Theme.helperText(label_Status, 450);
		label_Status.setLayoutX(LEFT);
		label_Status.setMinWidth(CONTENT_WIDTH);
		label_Status.setMaxWidth(CONTENT_WIDTH);

		// GUI Area 4.  New Lesson is the one filled button on the page.
		Theme.primaryButton(button_NewLesson, 482);
		button_NewLesson.setLayoutX(LEFT);
		button_NewLesson.setMinWidth(330);
		button_NewLesson.setMaxWidth(330);
		button_NewLesson.setOnAction((_) -> {ControllerLessonsList.newLesson(); });

		Theme.secondaryButton(button_OpenLesson, LEFT + 350, 485, 330);
		button_OpenLesson.setOnAction((_) -> {ControllerLessonsList.openSelectedLesson(); });

		// GUI Area 5
		Theme.secondaryButton(button_Back, LEFT, 540, 200);
		button_Back.setOnAction((_) -> {ControllerLessonsList.goBackHome(); });
		Theme.secondaryButton(button_Quit, LEFT + CONTENT_WIDTH - 200, 540, 200);
		button_Quit.setOnAction((_) -> {ControllerLessonsList.performQuit(); });

		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails,
				label_Search, text_Search, button_Search, button_ShowAll,
				label_Category, combo_Category, check_OnlyMine,
				list_Lessons, label_Status,
				button_NewLesson, button_OpenLesson,
				button_Back, button_Quit);
	}
}
