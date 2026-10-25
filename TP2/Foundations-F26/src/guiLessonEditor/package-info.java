/**
 * <p> The guiLessonEditor package holds the page used both to record a new lesson and to edit
 * an existing one.  One page serves both, because the fields and the rules are the same; the
 * title of the page and the Save action differ. </p>
 *
 * <p> {@link guiLessonEditor.ViewLessonEditor} builds the form, with a character counter under
 * each field; {@link guiLessonEditor.ControllerLessonEditor} reads the fields when Save Lesson
 * or Cancel is pressed and reports a refused field in a ValidationAlert;
 * {@link guiLessonEditor.ModelLessonEditor} builds the lesson, checks ownership, and stores it
 * through entityClasses.LessonsLearnedList. </p>
 *
 * <p> Contributor stories implemented here: CS-1 (create, with the contributor taken from the
 * logged in user, never typed), CS-7 (update), CS-9 (only the contributor may update), and the
 * on-screen side of CS-10 and CS-11 (a refused field is named). </p>
 */
package guiLessonEditor;
