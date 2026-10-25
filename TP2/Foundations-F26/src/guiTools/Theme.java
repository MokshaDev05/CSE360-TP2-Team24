package guiTools;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/*******
 * <p> Title: Theme Class. </p>
 * 
 * <p> Description: One place that decides how every screen other than the Admin screens looks,
 * so that they look like parts of the same application rather than a collection of pages.</p>
 * 
 * <p> The decisions encoded here are the ones recorded in User Experience.pdf after the team
 * compared five professional sign-in pages.  Five of them shaped this class:</p>
 * <ul>
 *   <li>the form sits in one narrow centred column, because that was the only layout every one
 *       of the five had in common;</li>
 *   <li>every field has a visible label above it rather than grey placeholder text inside it,
 *       because placeholder text disappears as soon as the user starts typing and a user who is
 *       interrupted comes back to a box with nothing to say what it wanted;</li>
 *   <li>there is exactly one filled button on a screen, so the next action is never in doubt;
 *       everything else is drawn quietly;</li>
 *   <li>supporting and secondary actions are smaller and set below the main one, out of the way
 *       of the main task;</li>
 *   <li>nothing relies on colour alone, so the screens still read for somebody who cannot tell
 *       red from green.</li>
 * </ul>
 * 
 * <p> The Admin screens are deliberately left as they were supplied, which the assignment
 * permits, so nothing in this class is applied to them.</p>
 * 
 * <p> This class only sets fonts, colours, sizes and positions.  It does not create widgets, take
 * decisions about behaviour, or perform validation, so restyling a screen cannot change what that
 * screen does.</p>
 * 
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP1 additions by CSE 360 Team: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-23  Initial version for story NU-7
 */
public class Theme {

	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public Theme() {
	}

	/*-********************************************************************************************
	 * 
	 * The measurements of the centred column
	 * 
	 */

	/** The width of the window, repeated here so this class does not depend on the main class. */
	public static final double WINDOW_WIDTH = 800;

	/** The width of the column the form sits in.  Narrow on purpose. */
	public static final double COLUMN_WIDTH = 360;

	/** The left edge of that column, which is what centres it in the window. */
	public static final double COLUMN_LEFT = (WINDOW_WIDTH - COLUMN_WIDTH) / 2;

	/** The height of a text field, and of a button, so the two line up. */
	public static final double FIELD_HEIGHT = 34;

	/** The gap between a label and the field it belongs to. */
	public static final double LABEL_GAP = 22;

	/** The gap between one labelled field and the next. */
	public static final double FIELD_SPACING = 64;

	/*-********************************************************************************************
	 * 
	 * Fonts and colours
	 * 
	 */

	/** One font family everywhere, so no screen looks as though it came from a different program. */
	public static final String FONT = "Helvetica Neue";

	/** A fallback used if the preferred family is unavailable. */
	public static final String FONT_FALLBACK = "Arial";

	private static final Color INK = Color.web("#1B1F24");			// ordinary text
	private static final Color QUIET_INK = Color.web("#57606A");		// supporting text
	private static final Color REFUSED_INK = Color.web("#B3261E");	// something was refused
	private static final Color ACCEPTED_INK = Color.web("#1B7F3B");	// something is satisfied

	/** The one filled button on a screen. */
	private static final String PRIMARY_BUTTON =
			"-fx-background-color: #1F6FEB;"
			+ "-fx-text-fill: white;"
			+ "-fx-font-weight: bold;"
			+ "-fx-background-radius: 6;"
			+ "-fx-cursor: hand;";

	/** Every other button: an outline, so it cannot be mistaken for the main action. */
	private static final String SECONDARY_BUTTON =
			"-fx-background-color: white;"
			+ "-fx-text-fill: #24292F;"
			+ "-fx-border-color: #AFB8C1;"
			+ "-fx-border-radius: 6;"
			+ "-fx-background-radius: 6;"
			+ "-fx-cursor: hand;";

	/** A text field, outlined so its edges are obvious against the page. */
	private static final String FIELD =
			"-fx-background-color: white;"
			+ "-fx-border-color: #AFB8C1;"
			+ "-fx-border-radius: 6;"
			+ "-fx-background-radius: 6;"
			+ "-fx-padding: 4 8 4 8;";

	/*-********************************************************************************************
	 * 
	 * Titles and text
	 * 
	 */

	/**********
	 * <p> Method: title </p>
	 * 
	 * <p> Description: The name of the screen, centred across the window at the top.</p>
	 * 
	 * @param theLabel the label to style
	 * @param y        the distance from the top of the window
	 */
	public static void title(Label theLabel, double y) {
		theLabel.setFont(Font.font(FONT, FontWeight.BOLD, 26));
		theLabel.setTextFill(INK);
		theLabel.setMinWidth(WINDOW_WIDTH);
		theLabel.setMaxWidth(WINDOW_WIDTH);
		theLabel.setWrapText(true);
		theLabel.setAlignment(Pos.CENTER);
		theLabel.setLayoutX(0);
		theLabel.setLayoutY(y);
	}

	/**********
	 * <p> Method: subtitle </p>
	 * 
	 * <p> Description: One quiet line under the title saying what the screen is for.  It is kept
	 * short because the five pages the team looked at all showed that explanation above a form
	 * only delays the user reaching the field.</p>
	 * 
	 * @param theLabel the label to style
	 * @param y        the distance from the top of the window
	 */
	public static void subtitle(Label theLabel, double y) {
		theLabel.setFont(Font.font(FONT, 14));
		theLabel.setTextFill(QUIET_INK);
		theLabel.setMinWidth(WINDOW_WIDTH);
		theLabel.setMaxWidth(WINDOW_WIDTH);
		theLabel.setWrapText(true);
		theLabel.setAlignment(Pos.CENTER);
		theLabel.setLayoutX(0);
		theLabel.setLayoutY(y);
	}

	/**********
	 * <p> Method: fieldLabel </p>
	 * 
	 * <p> Description: The label that sits above a field and names it.  This is the replacement
	 * for placeholder text inside the box.</p>
	 * 
	 * @param theLabel the label to style
	 * @param y        the distance from the top of the window
	 */
	public static void fieldLabel(Label theLabel, double y) {
		theLabel.setFont(Font.font(FONT, FontWeight.SEMI_BOLD, 13));
		theLabel.setTextFill(INK);
		theLabel.setMinWidth(COLUMN_WIDTH);
		theLabel.setMaxWidth(COLUMN_WIDTH);
		theLabel.setAlignment(Pos.BASELINE_LEFT);
		theLabel.setLayoutX(COLUMN_LEFT);
		theLabel.setLayoutY(y);
	}

	/**********
	 * <p> Method: helperText </p>
	 * 
	 * <p> Description: Small supporting text placed below whatever it refers to, never above it.
	 * </p>
	 * 
	 * @param theLabel the label to style
	 * @param y        the distance from the top of the window
	 */
	public static void helperText(Label theLabel, double y) {
		theLabel.setFont(Font.font(FONT, 12));
		theLabel.setTextFill(QUIET_INK);
		theLabel.setMinWidth(COLUMN_WIDTH);
		theLabel.setMaxWidth(COLUMN_WIDTH);
		theLabel.setWrapText(true);
		theLabel.setAlignment(Pos.BASELINE_LEFT);
		theLabel.setLayoutX(COLUMN_LEFT);
		theLabel.setLayoutY(y);
	}

	/**********
	 * <p> Method: valueLabel </p>
	 * 
	 * <p> Description: A label that displays a value the program produced, used on the account
	 * update page where each current value is shown rather than typed.</p>
	 * 
	 * @param theLabel the label to style
	 * @param x        the distance from the left of the window
	 * @param y        the distance from the top of the window
	 * @param width    how wide the label should be
	 */
	public static void valueLabel(Label theLabel, double x, double y, double width) {
		theLabel.setFont(Font.font(FONT, 14));
		theLabel.setTextFill(INK);
		theLabel.setMinWidth(width);
		theLabel.setMaxWidth(width);
		theLabel.setAlignment(Pos.BASELINE_LEFT);
		theLabel.setLayoutX(x);
		theLabel.setLayoutY(y);
	}

	/**********
	 * <p> Method: requirementLine </p>
	 * 
	 * <p> Description: One line of the live password requirement list.  The state is carried in
	 * the words as well as in the colour, so the list does not depend on being able to tell red
	 * from green.</p>
	 * 
	 * @param theLabel the label to style
	 * @param y        the distance from the top of the window
	 * @param isMet    true when the requirement is currently satisfied
	 */
	public static void requirementLine(Label theLabel, double y, boolean isMet) {
		theLabel.setFont(Font.font(FONT, 12));
		theLabel.setTextFill(isMet ? ACCEPTED_INK : REFUSED_INK);
		theLabel.setMinWidth(COLUMN_WIDTH);
		theLabel.setMaxWidth(COLUMN_WIDTH);
		theLabel.setAlignment(Pos.BASELINE_LEFT);
		theLabel.setLayoutX(COLUMN_LEFT + 4);
		theLabel.setLayoutY(y);
	}

	/*-********************************************************************************************
	 * 
	 * Fields and buttons
	 * 
	 */

	/**********
	 * <p> Method: field </p>
	 * 
	 * <p> Description: A text field sized to the column and outlined so its edges are clear.  No
	 * prompt text is set, because the label above the field is what names it.</p>
	 * 
	 * @param theField the field to style
	 * @param y        the distance from the top of the window
	 */
	public static void field(TextField theField, double y) {
		theField.setFont(Font.font(FONT, 14));
		theField.setStyle(FIELD);
		theField.setPromptText("");
		theField.setMinWidth(COLUMN_WIDTH);
		theField.setMaxWidth(COLUMN_WIDTH);
		theField.setPrefHeight(FIELD_HEIGHT);
		theField.setLayoutX(COLUMN_LEFT);
		theField.setLayoutY(y);
	}

	/**********
	 * <p> Method: primaryButton </p>
	 * 
	 * <p> Description: The one filled button on the screen, the full width of the column so it
	 * reads as the thing to do next.</p>
	 * 
	 * @param theButton the button to style
	 * @param y         the distance from the top of the window
	 */
	public static void primaryButton(Button theButton, double y) {
		theButton.setFont(Font.font(FONT, FontWeight.BOLD, 14));
		theButton.setStyle(PRIMARY_BUTTON);
		theButton.setMinWidth(COLUMN_WIDTH);
		theButton.setMaxWidth(COLUMN_WIDTH);
		theButton.setPrefHeight(FIELD_HEIGHT + 2);
		theButton.setLayoutX(COLUMN_LEFT);
		theButton.setLayoutY(y);
	}

	/**********
	 * <p> Method: secondaryButton </p>
	 * 
	 * <p> Description: Any button that is not the main action.  Outlined rather than filled, and
	 * narrower, so it cannot be mistaken for the primary button.</p>
	 * 
	 * @param theButton the button to style
	 * @param x         the distance from the left of the window
	 * @param y         the distance from the top of the window
	 * @param width     how wide the button should be
	 */
	public static void secondaryButton(Button theButton, double x, double y, double width) {
		theButton.setFont(Font.font(FONT, 13));
		theButton.setStyle(SECONDARY_BUTTON);
		theButton.setMinWidth(width);
		theButton.setMaxWidth(width);
		theButton.setPrefHeight(30);
		theButton.setLayoutX(x);
		theButton.setLayoutY(y);
	}

	/**********
	 * <p> Method: secondaryButtonCentred </p>
	 * 
	 * <p> Description: A secondary button centred in the window, used for actions such as Quit
	 * that belong at the foot of the page.</p>
	 * 
	 * @param theButton the button to style
	 * @param y         the distance from the top of the window
	 * @param width     how wide the button should be
	 */
	public static void secondaryButtonCentred(Button theButton, double y, double width) {
		secondaryButton(theButton, (WINDOW_WIDTH - width) / 2, y, width);
	}

	/**********
	 * <p> Method: rowLabel </p>
	 * 
	 * <p> Description: The name of a value on a page laid out as rows rather than as a form, such
	 * as the account update page.  It sits in its own narrow column on the left, so the name, the
	 * current value and the button that changes it line up down the page.</p>
	 * 
	 * @param theLabel the label to style
	 * @param y        the distance from the top of the window
	 */
	public static void rowLabel(Label theLabel, double y) {
		theLabel.setFont(Font.font(FONT, FontWeight.SEMI_BOLD, 13));
		theLabel.setTextFill(INK);
		theLabel.setMinWidth(190);
		theLabel.setMaxWidth(190);
		theLabel.setAlignment(Pos.BASELINE_RIGHT);
		theLabel.setLayoutX(40);
		theLabel.setLayoutY(y);
	}

	/**********
	 * <p> Method: separator </p>
	 * 
	 * <p> Description: A quiet horizontal rule used to set a secondary part of a page apart from
	 * the main form, in the way the professional pages separated alternative sign-in methods from
	 * the primary one.</p>
	 * 
	 * @param theLabel a label used as the rule
	 * @param y        the distance from the top of the window
	 */
	public static void separator(Label theLabel, double y) {
		theLabel.setText("");
		theLabel.setStyle("-fx-border-color: #D8DEE4; -fx-border-width: 1 0 0 0;");
		theLabel.setMinWidth(COLUMN_WIDTH);
		theLabel.setMaxWidth(COLUMN_WIDTH);
		theLabel.setPrefHeight(1);
		theLabel.setLayoutX(COLUMN_LEFT);
		theLabel.setLayoutY(y);
	}
}
