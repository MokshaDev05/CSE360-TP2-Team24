/**
 * <p> The entityClasses package holds the classes that represent the things the application
 * stores.  {@link entityClasses.User} is the supplied account entity.  TP2 adds
 * {@link entityClasses.LessonsLearned}, one lesson with its persistent create, read, update,
 * and delete operations and its validation, and {@link entityClasses.LessonsLearnedList}, the
 * persistent list of every lesson and the subsets made from it by search, category, and
 * contributor. </p>
 *
 * <p> None of these classes refers to JavaFX, so the testing automation can exercise every
 * operation without opening a window.  The table layout is known only to
 * database.LessonsLearnedDatabase. </p>
 */
package entityClasses;
