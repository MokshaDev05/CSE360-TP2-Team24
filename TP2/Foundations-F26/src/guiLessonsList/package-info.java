/**
 * <p> The guiLessonsList package holds the Lessons Learned list page, the page a contributor
 * reaches from the Lessons Learned button on the Role1 and Role2 home pages. </p>
 *
 * <p> It follows the Model-View-Controller split of the supplied GUI packages.
 * {@link guiLessonsList.ViewLessonsList} builds the widgets once and shows the page;
 * {@link guiLessonsList.ControllerLessonsList} reacts to the search box, the category filter,
 * the "Only my lessons" box, and the buttons; {@link guiLessonsList.ModelLessonsList} holds the
 * list of every lesson and decides which lessons are shown and what the status line says.
 * Unlike the supplied Models, which are empty, this Model holds data, because the page narrows a
 * list and has to explain the result.  The Model contains no JavaFX, so its decisions are
 * tested directly by testingAutomation.LessonsModelTestingAutomation. </p>
 *
 * <p> Contributor stories implemented here: CS-2 (see all lessons, newest first, with an empty
 * list explained in words), CS-3 (open the selected lesson), CS-4 (search), CS-5 (filter by
 * category), CS-6 (only my lessons), and the entry point to CS-1 (New Lesson). </p>
 */
package guiLessonsList;
