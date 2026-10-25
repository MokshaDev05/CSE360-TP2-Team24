package testingAutomation;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.LessonsLearnedDatabase;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import validation.LessonInputPolicy.Problem;

/*******
 * <p> Title: LessonsCrudTestingAutomation Class. </p>
 *
 * <p> Description: Semi-automated tests for the persistent Create, Read, Update, and Delete
 * operations of {@link entityClasses.LessonsLearned} and {@link entityClasses.LessonsLearnedList},
 * and for the storage they rely on in {@link database.LessonsLearnedDatabase}.  Requirements
 * covered: CS-1 (create), CS-3 (read by id), CS-7 (update), CS-8 (delete), CS-9 (ownership
 * test), and CS-12 (lessons persist across restarts). </p>
 *
 * <p> Three kinds of database are used, each for a stated reason: </p>
 * <ul>
 *   <li>CRUD-01 to CRUD-10 use a private in-memory database, so the developer's own
 *       FoundationDatabase file is never touched.</li>
 *   <li>CRUD-11 and CRUD-12 use a database file in a new temporary folder.  An in-memory
 *       database cannot show persistence, so these tests close the connection and open a new
 *       one on the same file, which is what restarting the application does.</li>
 *   <li>CRUD-13 and CRUD-14 build the table layout an HW2 solution left behind (author, body,
 *       tags, createdAt) and then open it the way FoundationsMain does.  Before the fix this
 *       made every save and every read fail with "Column DESCRIPTION not found".</li>
 * </ul>
 *
 * <p> Each test is one private method named after its id, whose Javadoc states the requirement,
 * how the test exercises the code, and the condition for a Pass.  The tests run in id order
 * because later ones use the lesson the earlier ones stored. </p>
 *
 * <p> How to read the output: one "Pass:" or "Failure:" line per test id, then the suite
 * totals.  A "SETUP FAILED" line means a database could not be opened and the tests that
 * needed it were counted as failures. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-16  Initial version for Team Project Phase 2
 * @version 1.10  2026-10-25  One method per test; real close-and-reopen persistence tests;
 *                            HW2 table layout tests
 */
public class LessonsCrudTestingAutomation {

	/** Count of tests that produced the expected result in this suite. */
	public static int numPassed = 0;

	/** Count of tests that did not produce the expected result in this suite. */
	public static int numFailed = 0;

	// The in-memory database for CRUD-01 to CRUD-10.  DB_CLOSE_DELAY=-1 keeps it alive between
	// statements; it is discarded when the program ends.
	private static final String MEMORY_URL = "jdbc:h2:mem:tp2crudtest;DB_CLOSE_DELAY=-1";

	private static final String TITLE = "H2 file lock prevents a second connection";
	private static final String NEW_TITLE = "H2 file lock: only one process at a time";

	// The lesson stored by CRUD-01 and used by CRUD-03 to CRUD-08.
	private static LessonsLearned stored;


	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public LessonsCrudTestingAutomation() {
	}


	/**********
	 * <p> Method: main </p>
	 *
	 * <p> Description: Opens the in-memory database, runs CRUD-01 to CRUD-10 against it, closes
	 * it, then runs the persistence and HW2-layout tests, which open their own databases. </p>
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		numPassed = 0;
		numFailed = 0;

		System.out.println("------------------------------------------------------------");
		System.out.println("  Lessons CRUD Tests (CS-1, CS-3, CS-7, CS-8, CS-9, CS-12)");
		System.out.println("------------------------------------------------------------");

		LessonsLearnedDatabase db = new LessonsLearnedDatabase();
		try {
			db.connectToDatabase(MEMORY_URL);
			db.deleteAllLessons();
			crud01CreateValidLesson(db);
			crud02CreateBlankTitleRefused(db);
			crud03ReadById(db);
			crud04ReadUnknownId(db);
			crud05UpdatePersists(db);
			crud06UpdateBlankTitleRefused(db);
			crud07Delete(db);
			crud08DeleteTwice(db);
			crud09OwnerRecognised(db);
			crud10NonOwnerNotRecognised();
		} catch (SQLException e) {
			setupFailed("in-memory database", e, 10);
		} finally {
			db.closeConnection();
		}

		crud11And12PersistAcrossReopen();
		crud13And14OpenHw2Layout();

		System.out.println("  -> passed " + numPassed + ", failed " + numFailed);
	}


	/*-*******************************************************************************************
	 *
	 * CS-1 Create
	 *
	 */

	/**
	 * <p> CRUD-01 (CS-1, positive).  A valid lesson is stored and given an identifier. </p>
	 * <p> How: LessonsLearned.create validates the lesson, writes it with
	 * LessonsLearnedDatabase.addLesson, and records the generated key in the object.  The
	 * stored row count is read before and after. </p>
	 * <p> Pass when: create returns null, the lesson's id is greater than 0, and the count has
	 * gone up by exactly one. </p>
	 *
	 * @param db the in-memory database
	 */
	private static void crud01CreateValidLesson(LessonsLearnedDatabase db) {
		int before = db.getNumberOfLessons();
		stored = new LessonsLearned(TITLE, "Database",
				"The H2 embedded engine locks the database file to a single JVM process.",
				"Append ;AUTO_SERVER=TRUE to the JDBC URL, or use an in-memory URL for tests.",
				"H2, JDBC, lock", "arushugal");
		Problem problem = stored.create(db);
		check("CRUD-01 valid lesson stored with an id (positive, CS-1)",
				problem == null && stored.getId() > 0
				&& db.getNumberOfLessons() == before + 1);
	}

	/**
	 * <p> CRUD-02 (CS-1, negative).  A lesson with a blank title is refused and nothing is
	 * written. </p>
	 * <p> How: create is called on a lesson whose title is "".  validate runs before addLesson,
	 * so the database should never see the row. </p>
	 * <p> Pass when: create returns "The title is required", the lesson's id is still -1, and
	 * the stored row count has not changed. </p>
	 *
	 * @param db the in-memory database
	 */
	private static void crud02CreateBlankTitleRefused(LessonsLearnedDatabase db) {
		int before = db.getNumberOfLessons();
		LessonsLearned bad = new LessonsLearned("", "Database",
				"A description that is otherwise valid.", "", "", "arushugal");
		Problem problem = bad.create(db);
		check("CRUD-02 blank title refused and nothing stored (negative, CS-1)",
				problem != null && "The title is required".equals(problem.title())
				&& bad.getId() == -1 && db.getNumberOfLessons() == before);
	}


	/*-*******************************************************************************************
	 *
	 * CS-3 Read
	 *
	 */

	/**
	 * <p> CRUD-03 (CS-3, positive).  The lesson stored by CRUD-01 can be read back by its id
	 * with every field intact. </p>
	 * <p> How: LessonsLearned.read(db, id) runs a SELECT by id and rebuilds the object. </p>
	 * <p> Pass when: the lesson read back has the same title, category, contributor, and
	 * keywords, and a creation date has been recorded. </p>
	 *
	 * @param db the in-memory database
	 */
	private static void crud03ReadById(LessonsLearnedDatabase db) {
		LessonsLearned read = LessonsLearned.read(db, stored.getId());
		check("CRUD-03 lesson read back by id with its fields (positive, CS-3)",
				read != null && TITLE.equals(read.getTitle())
				&& "Database".equals(read.getCategory())
				&& "arushugal".equals(read.getContributor())
				&& "H2, JDBC, lock".equals(read.getKeywords())
				&& !read.getDateCreated().isEmpty());
	}

	/**
	 * <p> CRUD-04 (CS-3, negative).  Reading an identifier that was never stored returns null
	 * rather than throwing an exception or returning an empty lesson. </p>
	 * <p> How: read is called with id 99999. </p>
	 * <p> Pass when: read returns null. </p>
	 *
	 * @param db the in-memory database
	 */
	private static void crud04ReadUnknownId(LessonsLearnedDatabase db) {
		check("CRUD-04 unknown id returns null (negative, CS-3)",
				LessonsLearned.read(db, 99999) == null);
	}


	/*-*******************************************************************************************
	 *
	 * CS-7 Update
	 *
	 */

	/**
	 * <p> CRUD-05 (CS-7, positive).  A changed title is written to the database, and the
	 * contributor and creation date are not changed by the update. </p>
	 * <p> How: the title is changed with setTitle and update is called.  The lesson is then
	 * read again from the database, not from the object in memory. </p>
	 * <p> Pass when: update returns null, the re-read lesson has the new title, and its
	 * contributor and creation date equal the originals. </p>
	 *
	 * @param db the in-memory database
	 */
	private static void crud05UpdatePersists(LessonsLearnedDatabase db) {
		String createdBefore = LessonsLearned.read(db, stored.getId()).getDateCreated();
		stored.setTitle(NEW_TITLE);
		Problem problem = stored.update(db);
		LessonsLearned read = LessonsLearned.read(db, stored.getId());
		check("CRUD-05 updated title stored; contributor and creation date kept (positive, CS-7)",
				problem == null && read != null && NEW_TITLE.equals(read.getTitle())
				&& "arushugal".equals(read.getContributor())
				&& createdBefore.equals(read.getDateCreated()));
	}

	/**
	 * <p> CRUD-06 (CS-7, negative).  An update to a blank title is refused and the stored
	 * lesson keeps its previous title, because update applies the same validation as create. </p>
	 * <p> How: a freshly read copy has its title set to "" and update is called. </p>
	 * <p> Pass when: update returns "The title is required" and the stored title is still the
	 * one CRUD-05 wrote. </p>
	 *
	 * @param db the in-memory database
	 */
	private static void crud06UpdateBlankTitleRefused(LessonsLearnedDatabase db) {
		LessonsLearned copy = LessonsLearned.read(db, stored.getId());
		copy.setTitle("");
		Problem problem = copy.update(db);
		LessonsLearned read = LessonsLearned.read(db, stored.getId());
		check("CRUD-06 blank title update refused, stored title unchanged (negative, CS-7)",
				problem != null && "The title is required".equals(problem.title())
				&& NEW_TITLE.equals(read.getTitle()));
	}


	/*-*******************************************************************************************
	 *
	 * CS-8 Delete
	 *
	 */

	/**
	 * <p> CRUD-07 (CS-8, positive).  A stored lesson is deleted and can no longer be read. </p>
	 * <p> How: delete is called on the CRUD-01 lesson; read is then called with its old id. </p>
	 * <p> Pass when: delete returns true and read returns null. </p>
	 *
	 * @param db the in-memory database
	 */
	private static void crud07Delete(LessonsLearnedDatabase db) {
		int id = stored.getId();
		boolean removed = stored.delete(db);
		check("CRUD-07 lesson deleted and no longer readable (positive, CS-8)",
				removed && LessonsLearned.read(db, id) == null);
	}

	/**
	 * <p> CRUD-08 (CS-8, negative).  Deleting the same lesson a second time reports failure
	 * instead of claiming success. </p>
	 * <p> How: delete is called again on the object CRUD-07 deleted, whose id is now -1. </p>
	 * <p> Pass when: delete returns false. </p>
	 *
	 * @param db the in-memory database
	 */
	private static void crud08DeleteTwice(LessonsLearnedDatabase db) {
		check("CRUD-08 second delete of the same lesson returns false (negative, CS-8)",
				!stored.delete(db));
	}


	/*-*******************************************************************************************
	 *
	 * CS-9 Ownership test
	 *
	 */

	// The lesson contributed by karanpatel, used by CRUD-09 and CRUD-10.
	private static LessonsLearned karansLesson;

	/**
	 * <p> CRUD-09 (CS-9, positive).  isOwnedBy recognises the contributor.  This is the test
	 * that every edit and delete screen relies on; the screens themselves are tested in
	 * LessonsModelTestingAutomation (MOD-11, MOD-12, MOD-16, MOD-20). </p>
	 * <p> How: a lesson is stored for "karanpatel" and isOwnedBy("karanpatel") is called. </p>
	 * <p> Pass when: the lesson is stored and isOwnedBy returns true. </p>
	 *
	 * @param db the in-memory database
	 */
	private static void crud09OwnerRecognised(LessonsLearnedDatabase db) {
		karansLesson = new LessonsLearned("JavaFX thread rule", "GUI",
				"All JavaFX widget changes must happen on the Application Thread.",
				"Wrap updates in Platform.runLater when calling from a background thread.",
				"JavaFX, threading", "karanpatel");
		Problem problem = karansLesson.create(db);
		check("CRUD-09 contributor recognised as owner (positive, CS-9)",
				problem == null && karansLesson.isOwnedBy("karanpatel"));
	}

	/**
	 * <p> CRUD-10 (CS-9, negative).  isOwnedBy refuses any other account, including the same
	 * name in a different case and a null username. </p>
	 * <p> How: isOwnedBy is called with "arushugal", "KaranPatel", and null. </p>
	 * <p> Pass when: all three calls return false. </p>
	 */
	private static void crud10NonOwnerNotRecognised() {
		check("CRUD-10 other users, case variants, and null are not owners (negative, CS-9)",
				!karansLesson.isOwnedBy("arushugal") && !karansLesson.isOwnedBy("KaranPatel")
				&& !karansLesson.isOwnedBy(null));
	}


	/*-*******************************************************************************************
	 *
	 * CS-12 Persistence across restarts
	 *
	 */

	/**
	 * <p> CRUD-11 (CS-12, positive) and CRUD-12 (CS-12, negative).  A lesson written before the
	 * database is closed is still there when it is opened again, and a lesson deleted before
	 * closing is still gone. </p>
	 * <p> How: a database file is created in a new temporary folder.  Two lessons are stored
	 * and one of them is deleted.  The connection is then closed, which is what quitting the
	 * application does, and a brand new LessonsLearnedDatabase object is connected to the same
	 * file, which is what starting it again does.  The folder is removed afterwards. </p>
	 * <p> Pass when, for CRUD-11: the kept lesson is read back after reopening with its title
	 * and contributor.  For CRUD-12: reading the deleted lesson's id after reopening returns
	 * null and exactly one lesson is stored. </p>
	 */
	private static void crud11And12PersistAcrossReopen() {
		File folder = null;
		try {
			folder = Files.createTempDirectory("tp2persist").toFile();
			String url = "jdbc:h2:" + folder.getAbsolutePath() + "/lessons";

			// First run of the application.
			LessonsLearnedDatabase firstRun = new LessonsLearnedDatabase();
			firstRun.connectToDatabase(url);
			LessonsLearned kept = new LessonsLearned("Lessons survive a restart", "Database",
					"Written in the first run and read in the second.", "", "", "mokshadev");
			LessonsLearned removed = new LessonsLearned("Deleted before the restart", "Database",
					"Written and then deleted in the first run.", "", "", "mokshadev");
			kept.create(firstRun);
			removed.create(firstRun);
			int removedId = removed.getId();
			removed.delete(firstRun);
			firstRun.closeConnection();

			// Second run: a new object, a new connection, the same file.
			LessonsLearnedDatabase secondRun = new LessonsLearnedDatabase();
			secondRun.connectToDatabase(url);
			LessonsLearned reread = LessonsLearned.read(secondRun, kept.getId());
			check("CRUD-11 lesson still stored after close and reopen (positive, CS-12)",
					reread != null && "Lessons survive a restart".equals(reread.getTitle())
					&& "mokshadev".equals(reread.getContributor()));
			check("CRUD-12 deleted lesson still absent after close and reopen (negative, CS-12)",
					LessonsLearned.read(secondRun, removedId) == null
					&& secondRun.getNumberOfLessons() == 1);
			secondRun.closeConnection();
		} catch (Exception e) {
			setupFailed("temporary database file", e, 2);
		} finally {
			deleteFolder(folder);
		}
	}

	/**
	 * <p> CRUD-13 (CS-12, positive) and CRUD-14 (CS-12, negative).  The application still
	 * saves and lists lessons when the database already holds an HW2 lessonsLearned table with
	 * different columns, and the HW2 rows are kept rather than lost. </p>
	 * <p> How: plain JDBC creates the HW2 layout (author, title, body, tags, category,
	 * createdAt) in a new in-memory database and inserts one HW2 row.  LessonsLearnedDatabase
	 * then connects to it, as FoundationsMain does at start up, which renames the HW2 table to
	 * LEGACY_TABLE and creates the TP2 table.  A lesson is then created and the list is read. </p>
	 * <p> Pass when, for CRUD-13: create returns null and readAll returns exactly that one
	 * lesson.  For CRUD-14: the renamed table still holds the one HW2 row, so nothing written
	 * by HW2 was deleted. </p>
	 */
	private static void crud13And14OpenHw2Layout() {
		String url = "jdbc:h2:mem:tp2hw2layout;DB_CLOSE_DELAY=-1";
		try (Connection raw = DriverManager.getConnection(url, "sa", "");
				Statement sql = raw.createStatement()) {
			sql.execute("CREATE TABLE lessonsLearned (id INT AUTO_INCREMENT PRIMARY KEY, "
					+ "author VARCHAR(255), title VARCHAR(255), body VARCHAR(5000), "
					+ "tags VARCHAR(255), category VARCHAR(64), createdAt BIGINT)");
			sql.execute("INSERT INTO lessonsLearned (author, title, body, tags, category, "
					+ "createdAt) VALUES ('pranjal', 'HW2 lesson', 'Written by HW2.', '', "
					+ "'General', 0)");

			LessonsLearnedDatabase db = new LessonsLearnedDatabase();
			db.connectToDatabase(url);
			LessonsLearned lesson = new LessonsLearned("Saved over an HW2 table", "Database",
					"This save failed before the table layout fix.", "", "", "pranjalganvir");
			Problem problem = lesson.create(db);
			LessonsLearnedList all = LessonsLearnedList.readAll(db);
			check("CRUD-13 save and list work over an HW2 table layout (positive, CS-12)",
					problem == null && all.size() == 1
					&& "Saved over an HW2 table".equals(all.get(0).getTitle()));

			int legacyRows = -1;
			try (ResultSet rs = sql.executeQuery("SELECT COUNT(*) FROM "
					+ LessonsLearnedDatabase.LEGACY_TABLE)) {
				if (rs.next())
					legacyRows = rs.getInt(1);
			} catch (SQLException missing) {
				// No renamed table means the HW2 rows were not kept; CRUD-14 reports it below.
			}
			check("CRUD-14 HW2 rows kept in " + LessonsLearnedDatabase.LEGACY_TABLE
					+ ", not deleted (negative, CS-12)", legacyRows == 1);
			db.closeConnection();
		} catch (SQLException e) {
			setupFailed("HW2 layout database", e, 2);
		}
	}


	/*-*******************************************************************************************
	 *
	 * Helpers
	 *
	 */

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

	// Reports a database that could not be opened, and counts the tests that depended on it as
	// failures, so a broken environment can never look like a passing run.
	private static void setupFailed(String what, Exception e, int testsLost) {
		System.out.println("  SETUP FAILED (" + what + "): " + e.getMessage());
		numFailed += testsLost;
	}

	// Removes the temporary folder used by CRUD-11 and CRUD-12, including the H2 files in it.
	private static void deleteFolder(File folder) {
		if (folder == null)
			return;
		File[] files = folder.listFiles();
		if (files != null)
			for (File f : files)
				f.delete();
		folder.delete();
	}
}
