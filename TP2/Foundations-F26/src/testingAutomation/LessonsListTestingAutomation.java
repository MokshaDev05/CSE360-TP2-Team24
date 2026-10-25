package testingAutomation;

import java.sql.SQLException;
import java.util.List;

import database.LessonsLearnedDatabase;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: LessonsListTestingAutomation Class. </p>
 *
 * <p> Description: Semi-automated tests for {@link entityClasses.LessonsLearnedList}: the
 * persistent list of every lesson and the subsets made from it.  Requirements covered: CS-2
 * (see all lessons, newest first), CS-4 (search title, description, and keywords, ignoring
 * case), CS-5 (filter by category), and CS-6 (only my lessons). </p>
 *
 * <p> The tests run against a private in-memory database seeded with three lessons, created in
 * this order so the expected newest-first order is known: </p>
 * <ol>
 *   <li>A: "H2 file lock prevents a second connection", category Database, keywords
 *       "H2, JDBC, lock", contributor arushugal</li>
 *   <li>B: "PreparedStatement prevents SQL injection", category Database, keywords
 *       "SQL, security, JDBC", contributor karanpatel</li>
 *   <li>C: "JavaFX thread rule: use Application Thread", category GUI, keywords
 *       "JavaFX, threading", contributor arushugal</li>
 * </ol>
 *
 * <p> Each test is one private method named after its id, whose Javadoc states the
 * requirement, how the test exercises the code, and the condition for a Pass.  Every subset
 * test checks which lessons came back, not only how many. </p>
 *
 * <p> How to read the output: one "Pass:" or "Failure:" line per test id, then the suite
 * totals. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-19  Initial version for Team Project Phase 2
 * @version 1.10  2026-10-25  One method per test; newest-first and case-insensitive tests;
 *                            subset contents checked, not only sizes
 */
public class LessonsListTestingAutomation {

	/** Count of tests that produced the expected result in this suite. */
	public static int numPassed = 0;

	/** Count of tests that did not produce the expected result in this suite. */
	public static int numFailed = 0;

	private static final String A = "H2 file lock prevents a second connection";
	private static final String B = "PreparedStatement prevents SQL injection";
	private static final String C = "JavaFX thread rule: use Application Thread";

	// The list of every seeded lesson, read once by main.
	private static LessonsLearnedList all;


	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public LessonsListTestingAutomation() {
	}


	/**********
	 * <p> Method: main </p>
	 *
	 * <p> Description: Seeds the in-memory database, reads the full list once, and runs every
	 * list test in id order. </p>
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		numPassed = 0;
		numFailed = 0;

		System.out.println("------------------------------------------------------------");
		System.out.println("  Lessons List Tests (CS-2, CS-4, CS-5, CS-6)");
		System.out.println("------------------------------------------------------------");

		LessonsLearnedDatabase db = new LessonsLearnedDatabase();
		try {
			db.connectToDatabase("jdbc:h2:mem:tp2listtest;DB_CLOSE_DELAY=-1");
			db.deleteAllLessons();
			seed(db);
			all = LessonsLearnedList.readAll(db);

			list01ReadAllReturnsEveryLesson();
			list02EmptyDatabaseGivesEmptyList();
			list03SearchFindsKeyword();
			list04SearchForAbsentTerm();
			list05FilterByCategory();
			list06FilterByAbsentCategory();
			list07MyLessons();
			list08LessonsOfUnknownUser();
			list09CategoriesOffered();
			list10SearchThenFilter();
			list11NewestFirst();
			list12SearchIgnoresCase();
			list13EmptySearchTerm();
		} catch (SQLException e) {
			System.out.println("  SETUP FAILED: " + e.getMessage());
			numFailed += 13;
		} finally {
			db.closeConnection();
		}

		System.out.println("  -> passed " + numPassed + ", failed " + numFailed);
	}

	// Stores lessons A, B, and C in that order.  See the class description.
	private static void seed(LessonsLearnedDatabase db) {
		new LessonsLearned(A, "Database", "The H2 embedded engine locks the database file.", "",
				"H2, JDBC, lock", "arushugal").create(db);
		new LessonsLearned(B, "Database",
				"Always use PreparedStatement, never string concatenation.", "",
				"SQL, security, JDBC", "karanpatel").create(db);
		new LessonsLearned(C, "GUI",
				"All widget updates must happen on the JavaFX Application Thread.", "",
				"JavaFX, threading", "arushugal").create(db);
	}


	/*-*******************************************************************************************
	 *
	 * CS-2 See all lessons
	 *
	 */

	/**
	 * <p> LIST-01 (CS-2, positive).  readAll returns every stored lesson. </p>
	 * <p> How: LessonsLearnedList.readAll reads the seeded database. </p>
	 * <p> Pass when: the list holds three lessons with titles A, B, and C. </p>
	 */
	private static void list01ReadAllReturnsEveryLesson() {
		check("LIST-01 readAll returns all 3 stored lessons (positive, CS-2)",
				all.size() == 3 && hasExactly(all, A, B, C));
	}

	/**
	 * <p> LIST-02 (CS-2, negative).  An empty database gives an empty list, not null and not an
	 * exception, so the page can show its "no lessons yet" message. </p>
	 * <p> How: readAll is called on a second, empty in-memory database. </p>
	 * <p> Pass when: the list is not null and isEmpty returns true. </p>
	 */
	private static void list02EmptyDatabaseGivesEmptyList() {
		LessonsLearnedDatabase emptyDb = new LessonsLearnedDatabase();
		try {
			emptyDb.connectToDatabase("jdbc:h2:mem:tp2emptytest;DB_CLOSE_DELAY=-1");
			LessonsLearnedList empty = LessonsLearnedList.readAll(emptyDb);
			check("LIST-02 empty database gives an empty list (negative, CS-2)",
					empty != null && empty.isEmpty());
		} catch (SQLException e) {
			System.out.println("  SETUP FAILED for LIST-02: " + e.getMessage());
			numFailed++;
		} finally {
			emptyDb.closeConnection();
		}
	}

	/**
	 * <p> LIST-11 (CS-2, positive).  The list is newest first. </p>
	 * <p> How: the order of the list read by readAll is compared with the seeding order.  The
	 * database orders by identifier, highest first, and identifiers are given out in creation
	 * order. </p>
	 * <p> Pass when: the titles are C, B, A in that order, and each identifier is larger than
	 * the next. </p>
	 */
	private static void list11NewestFirst() {
		check("LIST-11 list is newest first: C, B, A (positive, CS-2)",
				all.size() == 3 && C.equals(all.get(0).getTitle())
				&& B.equals(all.get(1).getTitle()) && A.equals(all.get(2).getTitle())
				&& all.get(0).getId() > all.get(1).getId()
				&& all.get(1).getId() > all.get(2).getId());
	}


	/*-*******************************************************************************************
	 *
	 * CS-4 Search
	 *
	 */

	/**
	 * <p> LIST-03 (CS-4, positive).  A search finds a word that appears only in the keywords. </p>
	 * <p> How: searchByText("JDBC").  "JDBC" is in the keywords of A and B and in none of the
	 * titles or descriptions, so this also shows the keywords are searched. </p>
	 * <p> Pass when: the subset is exactly A and B. </p>
	 */
	private static void list03SearchFindsKeyword() {
		check("LIST-03 search 'JDBC' finds exactly A and B (positive, CS-4)",
				hasExactly(all.searchByText("JDBC"), A, B));
	}

	/**
	 * <p> LIST-04 (CS-4, negative).  A term that appears in no lesson gives an empty subset. </p>
	 * <p> How: searchByText("quantumEntanglement"). </p>
	 * <p> Pass when: the subset is empty and the full list still holds three lessons, so the
	 * search did not change the list it searched. </p>
	 */
	private static void list04SearchForAbsentTerm() {
		check("LIST-04 search for an absent term gives an empty subset (negative, CS-4)",
				all.searchByText("quantumEntanglement").isEmpty() && all.size() == 3);
	}

	/**
	 * <p> LIST-12 (CS-4, positive).  The search ignores case. </p>
	 * <p> How: searchByText("jdbc") and searchByText("JAVAFX"), neither of which matches the
	 * stored text letter for letter. </p>
	 * <p> Pass when: "jdbc" finds exactly A and B, and "JAVAFX" finds exactly C. </p>
	 */
	private static void list12SearchIgnoresCase() {
		check("LIST-12 search ignores case: 'jdbc' and 'JAVAFX' (positive, CS-4)",
				hasExactly(all.searchByText("jdbc"), A, B)
				&& hasExactly(all.searchByText("JAVAFX"), C));
	}

	/**
	 * <p> LIST-13 (CS-4, negative).  An empty or blank search term matches nothing at the list
	 * level, so a search can only narrow a list.  The page treats an empty box as "no search"
	 * instead; that is tested by MOD-03. </p>
	 * <p> How: searchByText(""), searchByText("   "), and searchByText(null). </p>
	 * <p> Pass when: all three subsets are empty. </p>
	 */
	private static void list13EmptySearchTerm() {
		check("LIST-13 empty, blank, and null search terms match nothing (negative, CS-4)",
				all.searchByText("").isEmpty() && all.searchByText("   ").isEmpty()
				&& all.searchByText(null).isEmpty());
	}


	/*-*******************************************************************************************
	 *
	 * CS-5 Filter by category
	 *
	 */

	/**
	 * <p> LIST-05 (CS-5, positive).  Filtering by category returns the lessons in it. </p>
	 * <p> How: filterByCategory("database"), in lower case, while the stored category is
	 * "Database". </p>
	 * <p> Pass when: the subset is exactly A and B. </p>
	 */
	private static void list05FilterByCategory() {
		check("LIST-05 category 'database' gives exactly A and B (positive, CS-5)",
				hasExactly(all.filterByCategory("database"), A, B));
	}

	/**
	 * <p> LIST-06 (CS-5, negative).  A category no lesson uses gives an empty subset. </p>
	 * <p> How: filterByCategory("Cryptography"). </p>
	 * <p> Pass when: the subset is empty. </p>
	 */
	private static void list06FilterByAbsentCategory() {
		check("LIST-06 absent category gives an empty subset (negative, CS-5)",
				all.filterByCategory("Cryptography").isEmpty());
	}

	/**
	 * <p> LIST-09 (CS-5, positive).  getCategories offers each category in use once, in
	 * alphabetical order.  The page fills its category filter from this list. </p>
	 * <p> How: getCategories on the seeded list, where Database is used twice. </p>
	 * <p> Pass when: the result is exactly ["Database", "GUI"]. </p>
	 */
	private static void list09CategoriesOffered() {
		List<String> categories = all.getCategories();
		check("LIST-09 categories offered once each: Database, GUI (positive, CS-5)",
				categories.equals(List.of("Database", "GUI")));
	}

	/**
	 * <p> LIST-10 (CS-4 and CS-5, positive).  A search result can be filtered further, because
	 * every subset is itself a LessonsLearnedList. </p>
	 * <p> How: searchByText("thread") then filterByCategory("GUI"), and separately
	 * searchByText("JDBC") then filterByCategory("GUI"). </p>
	 * <p> Pass when: the first chain gives exactly C and the second gives an empty subset. </p>
	 */
	private static void list10SearchThenFilter() {
		check("LIST-10 search then filter narrows correctly (positive, CS-4, CS-5)",
				hasExactly(all.searchByText("thread").filterByCategory("GUI"), C)
				&& all.searchByText("JDBC").filterByCategory("GUI").isEmpty());
	}


	/*-*******************************************************************************************
	 *
	 * CS-6 Only my lessons
	 *
	 */

	/**
	 * <p> LIST-07 (CS-6, positive).  filterByContributor returns that user's lessons only. </p>
	 * <p> How: filterByContributor("arushugal"). </p>
	 * <p> Pass when: the subset is exactly A and C. </p>
	 */
	private static void list07MyLessons() {
		check("LIST-07 arushugal's lessons are exactly A and C (positive, CS-6)",
				hasExactly(all.filterByContributor("arushugal"), A, C));
	}

	/**
	 * <p> LIST-08 (CS-6, negative).  A user with no lessons gets an empty subset. </p>
	 * <p> How: filterByContributor("unknownuser"). </p>
	 * <p> Pass when: the subset is empty. </p>
	 */
	private static void list08LessonsOfUnknownUser() {
		check("LIST-08 user with no lessons gets an empty subset (negative, CS-6)",
				all.filterByContributor("unknownuser").isEmpty());
	}


	/*-*******************************************************************************************
	 *
	 * Helpers
	 *
	 */

	// True when the list holds exactly the lessons with these titles, in any order.
	private static boolean hasExactly(LessonsLearnedList list, String... titles) {
		if (list.size() != titles.length)
			return false;
		for (String title : titles) {
			boolean found = false;
			for (LessonsLearned lesson : list.getAll())
				if (title.equals(lesson.getTitle()))
					found = true;
			if (!found)
				return false;
		}
		return true;
	}

	/**
	 * <p> Method: check </p>
	 *
	 * <p> Description: Records one test outcome.  Prints "Pass:" or "Failure:" followed by the
	 * label, and updates {@link #numPassed} or {@link #numFailed}. </p>
	 *
	 * @param label  the test id, what was checked, and the requirement
	 * @param result true when the behaviour matched the requirement
	 */
	private static void check(String label, boolean result) {
		if (result) {
			System.out.println("  Pass:    " + label);
			numPassed++;
		} else {
			System.out.println("  Failure: " + label);
			numFailed++;
		}
	}
}
