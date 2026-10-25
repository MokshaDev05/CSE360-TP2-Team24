/**
 * <p> The guiLessonDetail package holds the page that shows one lesson in full, opened from
 * the list page. </p>
 *
 * <p> {@link guiLessonDetail.ViewLessonDetail} shows every field, including the read-only TP3
 * review attributes, and enables Edit Lesson and Delete Lesson only for the contributor;
 * {@link guiLessonDetail.ControllerLessonDetail} asks for confirmation before a delete;
 * {@link guiLessonDetail.ModelLessonDetail} decides who may change the lesson, writes the
 * ownership note, and carries out the delete. </p>
 *
 * <p> Contributor stories implemented here: CS-3 (read one lesson), CS-8 (delete after
 * confirming), and CS-9 (only the contributor may edit or delete; everyone else is told whose
 * lesson it is). </p>
 */
package guiLessonDetail;
