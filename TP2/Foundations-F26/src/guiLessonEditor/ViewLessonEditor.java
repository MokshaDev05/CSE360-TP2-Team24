package guiLessonEditor;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.LessonsLearned;
import entityClasses.User;
import guiTools.Theme;
import validation.LessonInputPolicy;

/*******
 * <p> Title: ViewLessonEditor Class. </p>
 *
 * <p> Description: The Java/FX-based lesson editor page, used both to record a new lesson
 * (CS-1) and to change an existing one (CS-7).  One page serves both purposes because the
 * fields, their limits, and their checks are identical; two copies of the same form would be
 * two places for the rules to drift apart, which is exactly what CS-7 says must not happen. </p>
 *
 * <p> Each field has its name above it and a live count of characters used beside the name, so
 * the contributor can see the limit before they reach it (CS-11) instead of discovering it only
 * when Save refuses the lesson.  The contributor is shown as text, not as a field, because it is
 * always the logged in account (CS-9). </p>
 *
 * <p> As in every supplied View, this class is a singleton.  The constructor builds the widgets
 * once; {@link #displayLessonEditor} fills them in for a new or an existing lesson. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-16  Initial version for Team Project Phase 2
 */
public class ViewLessonEditor {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// This page's content area.  Wider than the login column because a description needs room.
	private static final double LEFT = 100;
	private static final double CONTENT_WIDTH = 600;

	// GUI Area 1: what the page is doing and for whom.
	protected static Label label_PageTitle = new Label();
	protected static Label label_Contributor = new Label();

	// GUI Area 2: the lesson's fields, in the order LessonInputPolicy checks them, so the first
	// problem reported is always the first one the contributor would read.
	protected static Label label_Title = new Label("Title");
	protected static Label count_Title = new Label();
	protected static TextField text_Title = new TextField();

	protected static Label label_Category = new Label("Category");
	protected static Label count_Category = new Label();
	protected static TextField text_Category = new TextField();

	protected static Label label_Keywords = new Label("Keywords (optional)");
	protected static Label count_Keywords = new Label();
	protected static TextField text_Keywords = new TextField();
	protected static Label help_Keywords = new Label("Separate keywords with commas, "
			+ "for example: javafx, setup, mac");

	protected static Label label_Description = new Label("Description");
	protected static Label count_Description = new Label();
	protected static TextArea text_Description = new TextArea();

	protected static Label label_Resolution = new Label("Resolution (optional)");
	protected static Label count_Resolution = new Label();
	protected static TextArea text_Resolution = new TextArea();

	// GUI Area 3: saving or leaving.
	protected static Button button_Save = new Button("Save Lesson");
	protected static Button button_Cancel = new Button("Cancel");
	protected static Label label_Note = new Label("Every field is checked when you press Save. "
			+ "Nothing is stored until all of them are acceptable.");

	// These attributes are used to configure the page.
	private static ViewLessonEditor theView;	// The singleton instance, null until first use

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User

	/** Source: CS-7.  The lesson being edited, or null when a new lesson is being recorded.
	 *  The Controller uses this to decide between creating and updating. */
	protected static LessonsLearned theLesson;

	private static Scene theLessonEditorScene;	// The shared Scene each invocation populates


	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayLessonEditor(Stage ps, User user, LessonsLearned lesson) </p>
	 *
	 * <p> Description: The single entry point from outside this package that displays the lesson
	 * editor.  With a null lesson the form is empty and Save creates a new lesson (CS-1).  With a
	 * lesson, the form shows its current values (CS-7: the contributor edits the real current
	 * text rather than retyping it, and a field left alone stays as it was). </p>
	 *
	 * @param ps     the JavaFX Stage to be used for this GUI and its methods
	 * @param user   the User for this GUI and its methods
	 * @param lesson the lesson to edit, or null to record a new one
	 */
	public static void displayLessonEditor(Stage ps, User user, LessonsLearned lesson) {
		theStage = ps;
		theUser = user;
		theLesson = lesson;

		if (theView == null) theView = new ViewLessonEditor();

		label_Contributor.setText("Contributor: " + theUser.getUserName());

		if (lesson == null) {
			label_PageTitle.setText("New Lesson");
			button_Save.setText("Save Lesson");
			text_Title.setText("");
			text_Category.setText("");
			text_Keywords.setText("");
			text_Description.setText("");
			text_Resolution.setText("");
		} else {
			label_PageTitle.setText("Edit Lesson #" + lesson.getId());
			button_Save.setText("Save Changes");
			text_Title.setText(lesson.getTitle());
			text_Category.setText(lesson.getCategory());
			text_Keywords.setText(lesson.getKeywords());
			text_Description.setText(lesson.getDescription());
			text_Resolution.setText(lesson.getResolution());
		}

		theStage.setTitle("CSE 360 Foundations: " + label_PageTitle.getText());
		theStage.setScene(theLessonEditorScene);
		theStage.show();
		text_Title.requestFocus();
	}


	/**********
	 * <p> Method: ViewLessonEditor() </p>
	 *
	 * <p> Description: Initializes every element of the page.  This is a singleton and is only
	 * performed once. </p>
	 */
	private ViewLessonEditor() {

		theRootPane = new Pane();
		theLessonEditorScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		Theme.title(label_PageTitle, 18);
		Theme.subtitle(label_Contributor, 54);

		// GUI Area 2.  Title, then Category and Keywords side by side, then the two long fields.
		fieldLabel(label_Title, LEFT, 84);
		counter(count_Title, LEFT + CONTENT_WIDTH - 150, 84);
		textField(text_Title, LEFT, 104, CONTENT_WIDTH);
		liveCount(text_Title, count_Title, LessonInputPolicy.MAX_TITLE);

		fieldLabel(label_Category, LEFT, 146);
		counter(count_Category, LEFT + 280 - 150, 146);
		textField(text_Category, LEFT, 166, 280);
		liveCount(text_Category, count_Category, LessonInputPolicy.MAX_CATEGORY);

		fieldLabel(label_Keywords, LEFT + 300, 146);
		counter(count_Keywords, LEFT + CONTENT_WIDTH - 150, 146);
		textField(text_Keywords, LEFT + 300, 166, 300);
		liveCount(text_Keywords, count_Keywords, LessonInputPolicy.MAX_KEYWORDS);
		Theme.helperText(help_Keywords, 202);
		help_Keywords.setLayoutX(LEFT + 300);
		help_Keywords.setMinWidth(300);
		help_Keywords.setMaxWidth(300);

		fieldLabel(label_Description, LEFT, 226);
		counter(count_Description, LEFT + CONTENT_WIDTH - 150, 226);
		textArea(text_Description, LEFT, 246, 92);
		liveCount(text_Description, count_Description, LessonInputPolicy.MAX_DESCRIPTION);

		fieldLabel(label_Resolution, LEFT, 346);
		counter(count_Resolution, LEFT + CONTENT_WIDTH - 150, 346);
		textArea(text_Resolution, LEFT, 366, 76);
		liveCount(text_Resolution, count_Resolution, LessonInputPolicy.MAX_RESOLUTION);

		// GUI Area 3.  Save is the one filled button on the page.
		Theme.primaryButton(button_Save, 462);
		button_Save.setLayoutX(LEFT);
		button_Save.setMinWidth(380);
		button_Save.setMaxWidth(380);
		button_Save.setOnAction((_) -> {ControllerLessonEditor.performSave(); });

		Theme.secondaryButton(button_Cancel, LEFT + 400, 465, 200);
		button_Cancel.setOnAction((_) -> {ControllerLessonEditor.performCancel(); });

		Theme.helperText(label_Note, 512);
		label_Note.setLayoutX(LEFT);
		label_Note.setMinWidth(CONTENT_WIDTH);
		label_Note.setMaxWidth(CONTENT_WIDTH);

		theRootPane.getChildren().addAll(
				label_PageTitle, label_Contributor,
				label_Title, count_Title, text_Title,
				label_Category, count_Category, text_Category,
				label_Keywords, count_Keywords, text_Keywords, help_Keywords,
				label_Description, count_Description, text_Description,
				label_Resolution, count_Resolution, text_Resolution,
				button_Save, button_Cancel, label_Note);
	}


	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	// A field name, styled by Theme and moved to this page's column.
	private static void fieldLabel(Label l, double x, double y) {
		Theme.fieldLabel(l, y);
		l.setLayoutX(x);
	}

	// The "12 / 80" counter, right aligned at the end of a field's label row.
	private static void counter(Label l, double x, double y) {
		Theme.helperText(l, y + 1);
		l.setLayoutX(x);
		l.setMinWidth(150);
		l.setMaxWidth(150);
		l.setAlignment(javafx.geometry.Pos.BASELINE_RIGHT);
	}

	// A one line field, styled by Theme and resized to this page.
	private static void textField(TextField t, double x, double y, double w) {
		Theme.field(t, y);
		t.setLayoutX(x);
		t.setMinWidth(w);
		t.setMaxWidth(w);
	}

	// A multi line field.  Theme has no text area style because TP1 had no multi line input, so
	// the same border and font as Theme.field are applied here by hand.
	private static void textArea(TextArea t, double x, double y, double h) {
		t.setFont(Font.font(Theme.FONT, 14));
		t.setWrapText(true);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setPrefSize(CONTENT_WIDTH, h);
		t.setStyle("-fx-border-color: #AFB8C1; -fx-border-radius: 6; -fx-background-radius: 6;");
	}

	// Keeps a counter label up to date as the user types.  The text turns to the refused colour
	// and says "over the limit" in words once the limit is passed, so the warning does not rely
	// on colour alone (a TP1 user experience rule).
	private static void liveCount(TextInputControl field, Label counter, int limit) {
		Runnable update = () -> {
			int used = field.getText() == null ? 0 : field.getText().length();
			counter.setText(used <= limit ? used + " / " + limit
					: used + " / " + limit + "  over the limit");
			counter.setStyle(used <= limit ? "" : "-fx-text-fill: #B3261E;");
		};
		field.textProperty().addListener((_, _, _) -> update.run());
		update.run();
	}
}
