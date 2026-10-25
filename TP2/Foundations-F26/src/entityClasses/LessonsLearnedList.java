package entityClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import database.LessonsLearnedDatabase;
import validation.LessonInputPolicy.Problem;

/*******
 * <p> Title: LessonsLearnedList Class </p>
 *
 * <p> Description: Holds a collection of LessonsLearned objects and provides the persistent
 * Create, Read, Update, and Delete operations for that collection, together with the operations
 * that produce a subset of it.  One instance, produced by {@link #readAll}, is the list of every
 * lesson in the system.  Every other instance is a subset, such as the lessons that contain a
 * search term or the lessons a particular contributor wrote. </p>
 *
 * <p> A subset is returned as a new LessonsLearnedList rather than as a bare java.util.List.
 * Returning the same type means the caller can keep narrowing a result, count it, or hand it to
 * the same display code that handles the full set, without caring whether it holds everything or
 * the result of a search.  A subset can be empty, can hold one element, or can hold every
 * element, and there is no fixed upper limit on the number of elements, which the growable
 * ArrayList underneath provides. </p>
 *
 * <p> How the class supports each Contributor user story (see "Requirements to Tests Map.pdf" in
 * the TP2 folder for CS-1 to CS-12): </p>
 * <ul>
 *   <li>CS-1 Create: {@link #createLesson} stores the lesson and adds it to this list</li>
 *   <li>CS-2 See all lessons: {@link #readAll}, newest first, and {@link #toDisplayLines}</li>
 *   <li>CS-3 Open a lesson: {@link #findById}</li>
 *   <li>CS-4 Search: {@link #searchByText}</li>
 *   <li>CS-5 Filter by category: {@link #filterByCategory} and {@link #getCategories}</li>
 *   <li>CS-6 My lessons: {@link #filterByContributor}</li>
 *   <li>CS-7 Update: {@link #updateLesson} stores the change and replaces the list entry</li>
 *   <li>CS-8 Delete: {@link #deleteLesson} removes the row and the list entry</li>
 *   <li>CS-12 Persistence: the three persistent operations all go through the database</li>
 * </ul>
 *
 * <p> The persistent operations keep this in memory list and the database in step, so the
 * screen showing the list does not have to read every lesson again after each change.  The
 * subset operations do no database work at all, which is what lets the testing automation check
 * every search and filter without a database. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  HW2 and TP2 additions by CSE 360 Team 24: Arush
 * Ugal, Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-05  Initial version for Individual Homework 2
 * @version 2.00  2026-10-14  TP2: persistent create, update, and delete; contributor filter;
 *                            category list; keyword search
 */
public class LessonsLearnedList {

	/** Source: the HW2 requirement that a list hold all lessons or any subset, with no fixed
	 *  upper limit.  An ArrayList is used because the number of lessons is not known in advance.
	 *  It is final so the list object itself is never swapped out from under a caller. */
	private final List<LessonsLearned> lessons;


	/*****
	 * <p> Method: LessonsLearnedList() </p>
	 *
	 * <p> Description: Builds an empty list. </p>
	 */
	public LessonsLearnedList() {
		this.lessons = new ArrayList<>();
	}


	/*****
	 * <p> Method: LessonsLearnedList(List&lt;LessonsLearned&gt; initial) </p>
	 *
	 * <p> Description: Builds a list from an existing collection, copying the references into a
	 * new backing list so that later changes to this object do not reach back into the collection
	 * that was passed in. </p>
	 *
	 * @param initial the lessons to start with; a null is treated as none
	 */
	public LessonsLearnedList(List<LessonsLearned> initial) {
		this.lessons = new ArrayList<>();
		if (initial != null)
			for (LessonsLearned lesson : initial)
				add(lesson);
	}


	/*-*******************************************************************************************
	 *
	 * Persistent operations: Create, Read, Update, Delete
	 *
	 */

	/*****
	 * <p> Method: static LessonsLearnedList readAll(LessonsLearnedDatabase db) </p>
	 *
	 * <p> Description: Reads every stored lesson, newest first (CS-2).  This is the persistent
	 * list of all lessons in the system that the Lessons Learned page starts from. </p>
	 *
	 * @param db the open lessons database
	 *
	 * @return a list holding every stored lesson, possibly empty
	 */
	public static LessonsLearnedList readAll(LessonsLearnedDatabase db) {
		return db.getAllLessons();
	}


	/*****
	 * <p> Method: Problem createLesson(LessonsLearnedDatabase db, LessonsLearned lesson) </p>
	 *
	 * <p> Description: Stores a new lesson and, only if that worked, adds it to the front of this
	 * list so the list stays newest first (CS-1).  Validation happens inside LessonsLearned.create,
	 * so a refused lesson is neither stored nor added. </p>
	 *
	 * @param db     the open lessons database
	 * @param lesson the new lesson
	 *
	 * @return null when the lesson was stored, otherwise the problem that stopped it
	 */
	public Problem createLesson(LessonsLearnedDatabase db, LessonsLearned lesson) {
		if (lesson == null)
			return new Problem("There is no lesson to save", "No lesson was supplied.");
		Problem problem = lesson.create(db);
		if (problem == null)
			lessons.add(0, lesson);
		return problem;
	}


	/*****
	 * <p> Method: Problem updateLesson(LessonsLearnedDatabase db, LessonsLearned lesson) </p>
	 *
	 * <p> Description: Stores the changed lesson and, only if that worked, replaces the entry with
	 * the same identifier in this list (CS-7).  If this list is a subset that does not hold the
	 * lesson, the database is still updated and the list is left as it was. </p>
	 *
	 * @param db     the open lessons database
	 * @param lesson the lesson holding the identifier and the new values
	 *
	 * @return null when the lesson was updated, otherwise the problem that stopped it
	 */
	public Problem updateLesson(LessonsLearnedDatabase db, LessonsLearned lesson) {
		if (lesson == null)
			return new Problem("There is no lesson to update", "No lesson was supplied.");
		Problem problem = lesson.update(db);
		if (problem == null)
			for (int i = 0; i < lessons.size(); i++)
				if (lessons.get(i).getId() == lesson.getId())
					lessons.set(i, lesson);
		return problem;
	}


	/*****
	 * <p> Method: boolean deleteLesson(LessonsLearnedDatabase db, int id) </p>
	 *
	 * <p> Description: Removes the stored lesson with the given identifier and, only if that
	 * worked, removes it from this list as well (CS-8). </p>
	 *
	 * @param db the open lessons database
	 * @param id the identifier of the lesson to remove
	 *
	 * @return true when the lesson was removed, false when no stored lesson had that identifier
	 */
	public boolean deleteLesson(LessonsLearnedDatabase db, int id) {
		if (!db.deleteLesson(id))
			return false;
		removeById(id);
		return true;
	}


	/*-*******************************************************************************************
	 *
	 * In memory operations
	 *
	 */

	/*****
	 * <p> Method: void add(LessonsLearned lesson) </p>
	 *
	 * <p> Description: Adds one lesson to the end of the list without storing it.  Used when a
	 * list is being built from database rows and by the subset operations.  A null is ignored,
	 * so the display and search code never has to guard against a null element. </p>
	 *
	 * @param lesson the lesson to add
	 */
	public void add(LessonsLearned lesson) {
		if (lesson != null)
			lessons.add(lesson);
	}


	/*****
	 * <p> Method: boolean removeById(int id) </p>
	 *
	 * <p> Description: Removes the lesson with the given identifier from this list only.  The
	 * database is not touched; use deleteLesson for that. </p>
	 *
	 * @param id the identifier of the lesson to remove
	 *
	 * @return true if a lesson was removed, false if no lesson had that identifier
	 */
	public boolean removeById(int id) {
		return lessons.removeIf(lesson -> lesson.getId() == id);
	}


	/*****
	 * <p> Method: LessonsLearned findById(int id) </p>
	 *
	 * <p> Description: Returns the lesson in this list with the given identifier (CS-3). </p>
	 *
	 * @param id the identifier to look for
	 *
	 * @return the matching lesson, or null when this list does not hold one
	 */
	public LessonsLearned findById(int id) {
		for (LessonsLearned lesson : lessons)
			if (lesson.getId() == id)
				return lesson;
		return null;
	}


	/*****
	 * <p> Method: LessonsLearned get(int index) </p>
	 *
	 * <p> Description: Returns the lesson at a position in the list.  The Lessons Learned page
	 * uses this to turn the row the user selected into the lesson it shows. </p>
	 *
	 * @param index the position, starting at zero
	 *
	 * @return the lesson at that position, or null when the position is outside the list
	 */
	public LessonsLearned get(int index) {
		if (index < 0 || index >= lessons.size())
			return null;
		return lessons.get(index);
	}


	/** @return how many lessons are in this list; zero is a normal answer for a subset */
	public int size() { return lessons.size(); }

	/** @return true when this list holds no lessons */
	public boolean isEmpty() { return lessons.isEmpty(); }


	/*****
	 * <p> Method: List&lt;LessonsLearned&gt; getAll() </p>
	 *
	 * <p> Description: Returns a copy of the lessons as a plain list.  A copy is returned rather
	 * than the backing list so a caller cannot change the contents behind this object's back. </p>
	 *
	 * @return a new list containing the same lessons
	 */
	public List<LessonsLearned> getAll() {
		return new ArrayList<>(lessons);
	}


	/*-*******************************************************************************************
	 *
	 * Subsets
	 *
	 */

	/*****
	 * <p> Method: LessonsLearnedList searchByText(String term) </p>
	 *
	 * <p> Description: Returns the subset of lessons whose title, description, or keywords
	 * contain the term, ignoring case (CS-4).  The matching itself is LessonsLearned.matches, so
	 * the rule for what counts as a match is written once.  An empty term returns an empty
	 * subset, because the user asked to narrow the list and should not have it widened. </p>
	 *
	 * @param term the text to look for
	 *
	 * @return a new list holding only the matching lessons, possibly empty
	 */
	public LessonsLearnedList searchByText(String term) {
		LessonsLearnedList result = new LessonsLearnedList();
		for (LessonsLearned lesson : lessons)
			if (lesson.matches(term))
				result.add(lesson);
		return result;
	}


	/*****
	 * <p> Method: LessonsLearnedList filterByCategory(String category) </p>
	 *
	 * <p> Description: Returns the subset of lessons in the given category, matched exactly but
	 * without regard to case (CS-5).  A null or empty category returns an empty subset. </p>
	 *
	 * @param category the category to match
	 *
	 * @return a new list holding only the lessons in that category, possibly empty
	 */
	public LessonsLearnedList filterByCategory(String category) {
		LessonsLearnedList result = new LessonsLearnedList();
		if (category == null || category.trim().isEmpty())
			return result;
		for (LessonsLearned lesson : lessons)
			if (lesson.getCategory() != null
					&& lesson.getCategory().trim().equalsIgnoreCase(category.trim()))
				result.add(lesson);
		return result;
	}


	/*****
	 * <p> Method: LessonsLearnedList filterByContributor(String userName) </p>
	 *
	 * <p> Description: Returns the subset of lessons the given account contributed (CS-6).  The
	 * test is LessonsLearned.isOwnedBy, the same test the ownership rule uses, so "my lessons"
	 * and "lessons I may edit" can never disagree. </p>
	 *
	 * @param userName the username of the contributor
	 *
	 * @return a new list holding only that contributor's lessons, possibly empty
	 */
	public LessonsLearnedList filterByContributor(String userName) {
		LessonsLearnedList result = new LessonsLearnedList();
		for (LessonsLearned lesson : lessons)
			if (lesson.isOwnedBy(userName))
				result.add(lesson);
		return result;
	}


	/*****
	 * <p> Method: List&lt;String&gt; getCategories() </p>
	 *
	 * <p> Description: Returns each distinct category in this list once, in alphabetical order,
	 * ignoring case (CS-5).  The Lessons Learned page fills its category filter from this, so the
	 * filter only ever offers categories that will match at least one lesson. </p>
	 *
	 * @return the distinct categories, possibly empty
	 */
	public List<String> getCategories() {
		TreeSet<String> seen = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (LessonsLearned lesson : lessons)
			if (lesson.getCategory() != null && !lesson.getCategory().trim().isEmpty())
				seen.add(lesson.getCategory().trim());
		return new ArrayList<>(seen);
	}


	/*****
	 * <p> Method: List&lt;String&gt; toDisplayLines() </p>
	 *
	 * <p> Description: Produces one readable line per lesson, in list order, for the list on the
	 * Lessons Learned page and for the test output (CS-2). </p>
	 *
	 * @return a list of formatted lines, one per lesson
	 */
	public List<String> toDisplayLines() {
		List<String> lines = new ArrayList<>();
		for (LessonsLearned lesson : lessons)
			lines.add(lesson.toDisplayLine());
		return lines;
	}
}
