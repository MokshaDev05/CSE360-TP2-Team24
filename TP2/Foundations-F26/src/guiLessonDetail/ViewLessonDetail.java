package guiLessonDetail;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import entityClasses.LessonsLearned;
import entityClasses.User;
import guiTools.Theme;

/*******
 * <p> Title: ViewLessonDetail Class. </p>
 *
 * <p> Description: The Java/FX-based lesson detail page.  It shows every attribute of one lesson
 * (CS-3), read only, with Edit and Delete buttons for the lesson's contributor (CS-7, CS-8) that
 * are disabled, with a line saying why, for anyone else (CS-9). </p>
 *
 * <p> The lesson is shown on its own page rather than as a dialog over the list because the
 * description and resolution can each be 2000 characters, which does not fit in a dialog, and
 * because this page is where the editor returns to after a save, so the contributor sees the
 * stored result and the identifier it was given. </p>
 *
 * <p> The long fields are shown in read only text areas rather than labels so that long text
 * scrolls instead of running off the page, and so the text can be selected and copied. </p>
 *
 * <p> As in every supplied View, this class is a singleton. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-16  Initial version for Team Project Phase 2
 */
public class ViewLessonDetail {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static final double LEFT = 100;
	private static final double CONTENT_WIDTH = 600;
	private static final double VALUE_LEFT = 230;		// where the values start in each row
	private static final double ROW_SPACING = 24;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();

	// GUI Area 2: the short attributes, one row each.  CS-3.
	protected static Label name_Title = new Label("Title");
	protected static Label value_Title = new Label();
	protected static Label name_Category = new Label("Category");
	protected static Label value_Category = new Label();
	protected static Label name_Keywords = new Label("Keywords");
	protected static Label value_Keywords = new Label();
	protected static Label name_Contributor = new Label("Contributor");
	protected static Label value_Contributor = new Label();
	protected static Label name_Created = new Label("Created");
	protected static Label value_Created = new Label();
	protected static Label name_Updated = new Label("Last updated");
	protected static Label value_Updated = new Label();
	protected static Label name_Review = new Label("Staff review");
	protected static Label value_Review = new Label();

	// GUI Area 3: the two long attributes.
	protected static Label name_Description = new Label("Description");
	protected static TextArea text_Description = new TextArea();
	protected static Label name_Resolution = new Label("Resolution");
	protected static TextArea text_Resolution = new TextArea();

	// GUI Area 4: what can be done with the lesson.
	protected static Button button_Edit = new Button("Edit Lesson");
	protected static Button button_Delete = new Button("Delete Lesson");
	protected static Button button_Back = new Button("Back to List");
	protected static Label label_Ownership = new Label();

	private static ViewLessonDetail theView;	// The singleton instance, null until first use

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User

	/** Source: CS-3.  The lesson on screen, which Edit and Delete act on. */
	protected static LessonsLearned theLesson;

	private static Scene theLessonDetailScene;	// The shared Scene each invocation populates


	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayLessonDetail(Stage ps, User user, LessonsLearned lesson) </p>
	 *
	 * <p> Description: The single entry point from outside this package that displays one
	 * lesson.  Every value is filled in from the lesson, and Edit and Delete are enabled only when
	 * the logged in user is the lesson's contributor (CS-9). </p>
	 *
	 * @param ps     the JavaFX Stage to be used for this GUI and its methods
	 * @param user   the User for this GUI and its methods
	 * @param lesson the lesson to show
	 */
	public static void displayLessonDetail(Stage ps, User user, LessonsLearned lesson) {
		theStage = ps;
		theUser = user;
		theLesson = lesson;

		if (theView == null) theView = new ViewLessonDetail();

		label_PageTitle.setText("Lesson #" + lesson.getId());
		value_Title.setText(lesson.getTitle());
		value_Category.setText(lesson.getCategory());
		value_Keywords.setText(ModelLessonDetail.showOrDash(lesson.getKeywords()));
		value_Contributor.setText(lesson.getContributor());
		value_Created.setText(ModelLessonDetail.showOrDash(lesson.getDateCreated()));
		value_Updated.setText(ModelLessonDetail.showOrDash(lesson.getDateUpdated()));
		value_Review.setText(ModelLessonDetail.reviewLine(lesson));
		text_Description.setText(lesson.getDescription());
		text_Resolution.setText(lesson.getResolution() == null || lesson.getResolution().isEmpty()
				? "Not resolved yet." : lesson.getResolution());

		boolean mine = ModelLessonDetail.canChange(lesson, theUser.getUserName());
		button_Edit.setDisable(!mine);
		button_Delete.setDisable(!mine);
		label_Ownership.setText(ModelLessonDetail.ownershipNote(lesson, theUser.getUserName()));

		theStage.setTitle("CSE 360 Foundations: Lesson #" + lesson.getId());
		theStage.setScene(theLessonDetailScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewLessonDetail() </p>
	 *
	 * <p> Description: Initializes every element of the page.  This is a singleton and is only
	 * performed once. </p>
	 */
	private ViewLessonDetail() {

		theRootPane = new Pane();
		theLessonDetailScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		Theme.title(label_PageTitle, 18);

		// GUI Area 2
		double y = 70;
		row(name_Title, value_Title, y);
		value_Title.setFont(Font.font(Theme.FONT, FontWeight.BOLD, 14));
		row(name_Category, value_Category, y += ROW_SPACING);
		row(name_Keywords, value_Keywords, y += ROW_SPACING);
		row(name_Contributor, value_Contributor, y += ROW_SPACING);
		row(name_Created, value_Created, y += ROW_SPACING);
		row(name_Updated, value_Updated, y += ROW_SPACING);
		row(name_Review, value_Review, y += ROW_SPACING);

		// GUI Area 3
		rowName(name_Description, 248);
		readOnlyArea(text_Description, 268, 92);
		rowName(name_Resolution, 368);
		readOnlyArea(text_Resolution, 388, 70);

		// GUI Area 4.  Edit is the one filled button.
		Theme.primaryButton(button_Edit, 474);
		button_Edit.setLayoutX(LEFT);
		button_Edit.setMinWidth(230);
		button_Edit.setMaxWidth(230);
		button_Edit.setOnAction((_) -> {ControllerLessonDetail.performEdit(); });

		Theme.secondaryButton(button_Delete, LEFT + 245, 477, 170);
		button_Delete.setOnAction((_) -> {ControllerLessonDetail.performDelete(); });

		Theme.secondaryButton(button_Back, LEFT + 430, 477, 170);
		button_Back.setOnAction((_) -> {ControllerLessonDetail.performBack(); });

		Theme.helperText(label_Ownership, 522);
		label_Ownership.setLayoutX(LEFT);
		label_Ownership.setMinWidth(CONTENT_WIDTH);
		label_Ownership.setMaxWidth(CONTENT_WIDTH);

		theRootPane.getChildren().addAll(label_PageTitle,
				name_Title, value_Title, name_Category, value_Category,
				name_Keywords, value_Keywords, name_Contributor, value_Contributor,
				name_Created, value_Created, name_Updated, value_Updated,
				name_Review, value_Review,
				name_Description, text_Description, name_Resolution, text_Resolution,
				button_Edit, button_Delete, button_Back, label_Ownership);
	}


	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	// One row: the attribute's name on the left, its value to the right, in the layout TP1 used
	// for the account update page so the two read alike.
	private static void row(Label name, Label value, double y) {
		rowName(name, y);
		Theme.valueLabel(value, VALUE_LEFT, y, LEFT + CONTENT_WIDTH - VALUE_LEFT);
	}

	private static void rowName(Label name, double y) {
		Theme.fieldLabel(name, y);
		name.setLayoutX(LEFT);
		name.setMinWidth(120);
		name.setMaxWidth(120);
		name.setAlignment(Pos.BASELINE_LEFT);
	}

	private static void readOnlyArea(TextArea t, double y, double h) {
		t.setEditable(false);
		t.setWrapText(true);
		t.setFont(Font.font(Theme.FONT, 14));
		t.setLayoutX(LEFT);
		t.setLayoutY(y);
		t.setPrefSize(CONTENT_WIDTH, h);
		t.setStyle("-fx-border-color: #D8DEE4; -fx-border-radius: 6; -fx-background-radius: 6;");
	}
}
