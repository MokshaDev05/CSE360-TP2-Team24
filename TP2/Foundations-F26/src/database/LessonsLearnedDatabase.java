package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: LessonsLearnedDatabase Class </p>
 *
 * <p> Description: Stores lessons learned in the same H2 database the supplied Foundations code
 * uses, so lessons persist between separate runs of the application (Contributor story CS-12).
 * It connects to the same jdbc:h2:~/FoundationDatabase the supplied Database class uses and adds
 * one table of its own rather than changing the user table.  Each operation uses a
 * PreparedStatement in the same way the supplied register and update methods do, so pasted text
 * can never change the meaning of a query. </p>
 *
 * <p> The entity classes LessonsLearned and LessonsLearnedList are what the screens call; this
 * class is the only place that knows the table's layout.  Create is {@code addLesson}, read is
 * {@code getAllLessons} and {@code getLessonById}, update is {@code updateLesson}, and delete is
 * {@code deleteLesson}. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  HW2 and TP2 additions by CSE 360 Team 24: Arush
 * Ugal, Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-05  Initial version for Individual Homework 2
 * @version 2.00  2026-10-14  TP2: keywords, dates, and TP3 review columns; newest first order
 */
public class LessonsLearnedDatabase {

	// JDBC driver name and database URL, matching the supplied Database class so the lessons are
	// stored in the same database as the user accounts.
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials, again matching the supplied class.
	static final String USER = "sa";
	static final String PASS = "";

	// Every column, in one place, so that the two read queries cannot list them differently.
	private static final String ALL_COLUMNS = "id, title, category, description, resolution, "
			+ "keywords, contributor, dateCreated, dateUpdated, reviewStatus, reviewerComment, "
			+ "helpfulCount";

	/** The name an incompatible HW2 lessons table is renamed to, so its rows are kept rather
	 *  than dropped.  See {@link #moveLegacyTableAside()}. */
	public static final String LEGACY_TABLE = "lessonsLearnedHW2Legacy";

	private Connection connection = null;
	private Statement statement = null;


	/*****
	 * <p> Method: LessonsLearnedDatabase() </p>
	 *
	 * <p> Description: The default constructor used to establish this object. </p>
	 */
	public LessonsLearnedDatabase() {
	}


	/*****
	 * <p> Method: void connectToDatabase() </p>
	 *
	 * <p> Description: Opens the application's stored database and makes sure the lessons table
	 * exists with every column TP2 needs.  FoundationsMain calls this once at start up. </p>
	 *
	 * @throws SQLException when the DriverManager cannot establish a connection
	 */
	public void connectToDatabase() throws SQLException {
		connectToDatabase(DB_URL);
	}


	/*****
	 * <p> Method: void connectToDatabase(String url) </p>
	 *
	 * <p> Description: Opens the H2 database at the given URL.  This exists so the testing
	 * automation can run every create, read, update, and delete check against a private in memory
	 * database rather than the developer's own stored file.  The application calls the
	 * no-argument version above, so a test run never changes the real data. </p>
	 *
	 * @param url the H2 URL to connect to
	 *
	 * @throws SQLException when the DriverManager cannot establish a connection
	 */
	public void connectToDatabase(String url) throws SQLException {
		try {
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(url, USER, PASS);
			statement = connection.createStatement();
			createTables();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}


	/*****
	 * <p> Method: void createTables() </p>
	 *
	 * <p> Description: Creates the lessons table if it is not already there, then adds each TP2
	 * column if it is missing. </p>
	 *
	 * <p> The TP2 columns are added with separate ALTER TABLE statements rather than written into
	 * the CREATE TABLE, for the reason recorded in Database.createTables in TP1: CREATE TABLE IF
	 * NOT EXISTS does nothing at all when the table is already there.  Anybody who ran HW2 already
	 * has a lessonsLearned table with the HW2 columns, and a column written only into the CREATE
	 * TABLE would be silently missing for them.  ADD COLUMN IF NOT EXISTS is safe to run on every
	 * start, so a new database and an HW2 database end up with the same layout and keep their
	 * existing lessons. </p>
	 *
	 * <p> That only works for an HW2 table that already used the TP2 column names.  Several HW2
	 * solutions used the same table name with different columns (author, body, tags, createdAt).
	 * For such a table CREATE TABLE IF NOT EXISTS does nothing, the ALTER statements cannot add
	 * description, resolution, or contributor, and every later INSERT and SELECT fails.  So,
	 * before anything else, a table without a description column is renamed out of the way by
	 * {@link #moveLegacyTableAside()} and a TP2 table is created in its place.  The old rows are
	 * kept, untouched, in the renamed table. </p>
	 *
	 * @throws SQLException when the table cannot be created or changed
	 */
	private void createTables() throws SQLException {
		moveLegacyTableAside();

		statement.execute("CREATE TABLE IF NOT EXISTS lessonsLearned ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "title VARCHAR(80), "
				+ "category VARCHAR(30), "
				+ "description VARCHAR(2000), "
				+ "resolution VARCHAR(2000), "
				+ "contributor VARCHAR(16))");

		statement.execute("ALTER TABLE lessonsLearned "
				+ "ADD COLUMN IF NOT EXISTS keywords VARCHAR(200) DEFAULT ''");
		statement.execute("ALTER TABLE lessonsLearned "
				+ "ADD COLUMN IF NOT EXISTS dateCreated VARCHAR(16) DEFAULT ''");
		statement.execute("ALTER TABLE lessonsLearned "
				+ "ADD COLUMN IF NOT EXISTS dateUpdated VARCHAR(16) DEFAULT ''");
		statement.execute("ALTER TABLE lessonsLearned "
				+ "ADD COLUMN IF NOT EXISTS reviewStatus VARCHAR(20) DEFAULT '"
				+ LessonsLearned.REVIEW_NOT_REVIEWED + "'");
		statement.execute("ALTER TABLE lessonsLearned "
				+ "ADD COLUMN IF NOT EXISTS reviewerComment VARCHAR(2000) DEFAULT ''");
		statement.execute("ALTER TABLE lessonsLearned "
				+ "ADD COLUMN IF NOT EXISTS helpfulCount INT DEFAULT 0");
	}


	/*****
	 * <p> Method: boolean moveLegacyTableAside() </p>
	 *
	 * <p> Description: Renames a lessonsLearned table that has no description column to
	 * {@link #LEGACY_TABLE}, so createTables can build the TP2 table under the expected name.
	 * The rename is used instead of DROP TABLE so that no lesson written by an earlier version is
	 * ever destroyed.  If the legacy name is already taken, a numbered name is used instead. </p>
	 *
	 * <p> H2 stores unquoted identifiers in upper case, which is why the INFORMATION_SCHEMA
	 * queries compare against LESSONSLEARNED and DESCRIPTION. </p>
	 *
	 * @return true when a legacy table was found and renamed, false when there was nothing to do
	 *
	 * @throws SQLException when the schema cannot be read or the table cannot be renamed
	 */
	private boolean moveLegacyTableAside() throws SQLException {
		if (!tableExists("LESSONSLEARNED") || columnExists("LESSONSLEARNED", "DESCRIPTION"))
			return false;

		String target = LEGACY_TABLE;
		for (int n = 2; tableExists(target.toUpperCase()); n++)
			target = LEGACY_TABLE + n;
		statement.execute("ALTER TABLE lessonsLearned RENAME TO " + target);
		return true;
	}

	// True when a table with this (upper case) name exists in the PUBLIC schema.
	private boolean tableExists(String upperName) throws SQLException {
		try (PreparedStatement pstmt = connection.prepareStatement(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
				+ "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = ?")) {
			pstmt.setString(1, upperName);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	// True when the (upper case) table has a column with this (upper case) name.
	private boolean columnExists(String upperTable, String upperColumn) throws SQLException {
		try (PreparedStatement pstmt = connection.prepareStatement(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
				+ "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = ? AND COLUMN_NAME = ?")) {
			pstmt.setString(1, upperTable);
			pstmt.setString(2, upperColumn);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}


	/*-********************************************************************************************
	 *
	 * Create
	 *
	 */

	/*****
	 * <p> Method: int addLesson(LessonsLearned lesson) </p>
	 *
	 * <p> Description: Writes one new lesson and returns the identifier the database assigned.
	 * The identifier is read back from the generated keys rather than guessed, and is also stored
	 * in the lesson object, so the lesson can be updated or deleted straight away. </p>
	 *
	 * @param lesson the lesson to store, which LessonsLearned.create has already validated
	 *
	 * @return the new identifier on success, or -1 if the row could not be written
	 */
	public int addLesson(LessonsLearned lesson) {
		String query = "INSERT INTO lessonsLearned (title, category, description, resolution, "
				+ "keywords, contributor, dateCreated, dateUpdated, reviewStatus, "
				+ "reviewerComment, helpfulCount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt =
				connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, lesson.getTitle());
			pstmt.setString(2, lesson.getCategory());
			pstmt.setString(3, lesson.getDescription());
			pstmt.setString(4, emptyIfNull(lesson.getResolution()));
			pstmt.setString(5, emptyIfNull(lesson.getKeywords()));
			pstmt.setString(6, lesson.getContributor());
			pstmt.setString(7, emptyIfNull(lesson.getDateCreated()));
			pstmt.setString(8, emptyIfNull(lesson.getDateUpdated()));
			pstmt.setString(9, lesson.getReviewStatus());
			pstmt.setString(10, emptyIfNull(lesson.getReviewerComment()));
			pstmt.setInt(11, lesson.getHelpfulCount());
			pstmt.executeUpdate();

			try (ResultSet keys = pstmt.getGeneratedKeys()) {
				if (keys.next()) {
					int newId = keys.getInt(1);
					lesson.setId(newId);
					return newId;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}


	/*-********************************************************************************************
	 *
	 * Read
	 *
	 */

	/*****
	 * <p> Method: LessonsLearnedList getAllLessons() </p>
	 *
	 * <p> Description: Reads every stored lesson, newest first, into a LessonsLearnedList.  The
	 * order is by identifier, highest first, because identifiers are assigned in the order lessons
	 * are created; ordering by the date text would put HW2 lessons, which have no date, in an
	 * arbitrary place. </p>
	 *
	 * @return a LessonsLearnedList holding every stored lesson, possibly empty
	 */
	public LessonsLearnedList getAllLessons() {
		LessonsLearnedList list = new LessonsLearnedList();
		String query = "SELECT " + ALL_COLUMNS + " FROM lessonsLearned ORDER BY id DESC";
		try (PreparedStatement pstmt = connection.prepareStatement(query);
				ResultSet rs = pstmt.executeQuery()) {
			while (rs.next())
				list.add(readRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}


	/*****
	 * <p> Method: LessonsLearned getLessonById(int id) </p>
	 *
	 * <p> Description: Reads the single lesson with the given identifier. </p>
	 *
	 * @param id the identifier to read
	 *
	 * @return the matching lesson, or null when there is none
	 */
	public LessonsLearned getLessonById(int id) {
		String query = "SELECT " + ALL_COLUMNS + " FROM lessonsLearned WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return readRow(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	/*****
	 * <p> Method: int getNumberOfLessons() </p>
	 *
	 * <p> Description: Returns how many lessons are stored.  Used by the tests to confirm that a
	 * create or a delete changed the count by exactly one. </p>
	 *
	 * @return the number of stored lessons
	 */
	public int getNumberOfLessons() {
		try (ResultSet rs = statement.executeQuery(
				"SELECT COUNT(*) AS count FROM lessonsLearned")) {
			if (rs.next())
				return rs.getInt("count");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


	/*-********************************************************************************************
	 *
	 * Update
	 *
	 */

	/*****
	 * <p> Method: boolean updateLesson(LessonsLearned lesson) </p>
	 *
	 * <p> Description: Writes the editable values and the update date back to the lesson's row,
	 * matched by identifier.  The contributor, the creation date, and the three TP3 attributes are
	 * deliberately left out of the SET clause: a contributor's edit has no business changing who
	 * wrote the lesson, when it was written, or how staff reviewed it, and leaving them out means
	 * no bug in a screen can change them either. </p>
	 *
	 * @param lesson the lesson holding the identifier to match and the new values
	 *
	 * @return true when a row was updated, false when no lesson had that identifier
	 */
	public boolean updateLesson(LessonsLearned lesson) {
		String query = "UPDATE lessonsLearned SET title = ?, category = ?, description = ?, "
				+ "resolution = ?, keywords = ?, dateUpdated = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, lesson.getTitle());
			pstmt.setString(2, lesson.getCategory());
			pstmt.setString(3, lesson.getDescription());
			pstmt.setString(4, emptyIfNull(lesson.getResolution()));
			pstmt.setString(5, emptyIfNull(lesson.getKeywords()));
			pstmt.setString(6, emptyIfNull(lesson.getDateUpdated()));
			pstmt.setInt(7, lesson.getId());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	/*-********************************************************************************************
	 *
	 * Delete
	 *
	 */

	/*****
	 * <p> Method: boolean deleteLesson(int id) </p>
	 *
	 * <p> Description: Removes the lesson with the given identifier.  The screen confirms with the
	 * user first, because the removal cannot be undone. </p>
	 *
	 * @param id the identifier of the lesson to remove
	 *
	 * @return true when a row was removed, false when no lesson had that identifier
	 */
	public boolean deleteLesson(int id) {
		try (PreparedStatement pstmt =
				connection.prepareStatement("DELETE FROM lessonsLearned WHERE id = ?")) {
			pstmt.setInt(1, id);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	/*****
	 * <p> Method: void deleteAllLessons() </p>
	 *
	 * <p> Description: Removes every lesson.  Only the testing automation calls this, and only
	 * against its private in memory database, so each run starts from a known empty state. </p>
	 */
	public void deleteAllLessons() {
		try {
			statement.execute("DELETE FROM lessonsLearned");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/*-********************************************************************************************
	 *
	 * Shared helpers
	 *
	 */

	// Builds one lesson from the current row.  In one place so that getAllLessons and
	// getLessonById cannot read the columns differently.  Columns that an HW2 row may hold as
	// null are turned into empty strings, so the screens never display the word "null".
	private LessonsLearned readRow(ResultSet rs) throws SQLException {
		String reviewStatus = rs.getString("reviewStatus");
		return new LessonsLearned(
				rs.getInt("id"),
				rs.getString("title"),
				rs.getString("category"),
				rs.getString("description"),
				emptyIfNull(rs.getString("resolution")),
				emptyIfNull(rs.getString("keywords")),
				rs.getString("contributor"),
				emptyIfNull(rs.getString("dateCreated")),
				emptyIfNull(rs.getString("dateUpdated")),
				reviewStatus == null ? LessonsLearned.REVIEW_NOT_REVIEWED : reviewStatus,
				emptyIfNull(rs.getString("reviewerComment")),
				rs.getInt("helpfulCount"));
	}

	private static String emptyIfNull(String s) {
		return s == null ? "" : s;
	}


	/*****
	 * <p> Method: void closeConnection() </p>
	 *
	 * <p> Description: Closes the statement and the connection, in the same way the supplied
	 * class does. </p>
	 */
	public void closeConnection() {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}
