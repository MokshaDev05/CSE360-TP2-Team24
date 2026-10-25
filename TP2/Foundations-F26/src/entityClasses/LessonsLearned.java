package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import database.LessonsLearnedDatabase;
import validation.LessonInputPolicy;
import validation.LessonInputPolicy.Problem;

/*******
 * <p> Title: LessonsLearned Class </p>
 *
 * <p> Description: This LessonsLearned class represents a single lesson learned entity in the
 * system, together with the persistent Create, Read, Update, and Delete operations and the input
 * validation that every one of the Contributor user stories relies on.  It is the TP2 counterpart
 * of the supplied {@code User} entity class and is written in the same plain attributes with
 * getters and setters style, so that the two sit comfortably in the same package. </p>
 *
 * <p> A lesson models the kind of item the team saw on the Ed Discussion board: a question or an
 * observation, the reply that resolved it, and the person who contributed it.  To that HW2 shape
 * TP2 adds keywords for searching, the dates the lesson was created and last changed, and three
 * attributes that the TP3 staff epics need (a review status, a reviewer comment, and a helpful
 * count).  The TP3 attributes are stored now, rather than added in TP3, because adding columns to
 * a table that already holds data is the riskiest kind of change this project makes; see the
 * TP1 note in Database.createTables.  Contributors cannot change those three attributes: there
 * are no setters for them, and LessonsLearnedDatabase.updateLesson leaves their columns out of
 * its SET clause. </p>
 *
 * <p> Two TP3 needs are deliberately not met by attributes here.  Recording who found a lesson
 * helpful, so one reader cannot count twice, needs a separate table keyed by lesson and user;
 * the helpfulCount attribute is the total that table will maintain.  Likewise, comments from
 * several readers belong in their own table keyed by lesson identifier. </p>
 *
 * <p> How the class supports each Contributor user story (the stories, CS-1 to CS-12, are listed
 * in "Requirements to Tests Map.pdf" in the TP2 folder): </p>
 * <ul>
 *   <li>CS-1 Create: {@link #create(LessonsLearnedDatabase)}</li>
 *   <li>CS-2, CS-3 Read: {@link #read(LessonsLearnedDatabase, int)} and the getters</li>
 *   <li>CS-4 Search: {@link #matches(String)}, used by LessonsLearnedList.searchByText</li>
 *   <li>CS-6, CS-9 Ownership: {@link #isOwnedBy(String)}</li>
 *   <li>CS-7 Update: {@link #update(LessonsLearnedDatabase)}</li>
 *   <li>CS-8 Delete: {@link #delete(LessonsLearnedDatabase)}</li>
 *   <li>CS-10, CS-11 Validation: {@link #validate()}, which create and update both call</li>
 *   <li>CS-12 Persistence: every operation goes through LessonsLearnedDatabase</li>
 * </ul>
 *
 * <p> The class has no JavaFX reference, so the testing automation can create, store, read,
 * change, and delete lessons without opening a window. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  HW2 and TP2 additions by CSE 360 Team 24: Arush
 * Ugal, Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-05  Initial version (named LessonLearned) for Individual Homework 2
 * @version 2.00  2026-10-14  TP2: renamed, persistent CRUD moved into the entity, keywords,
 *                            dates, ownership, and the TP3 review attributes added
 */
public class LessonsLearned {

	/*-*******************************************************************************************
	 *
	 * Constants
	 *
	 */

	/** The review status every new lesson starts with.  Used by the TP3 staff review epic. */
	public static final String REVIEW_NOT_REVIEWED = "Not reviewed";

	/** The format used for the two date attributes, chosen so that text order is date order. */
	public static final DateTimeFormatter DATE_FORMAT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


	/*-*******************************************************************************************
	 *
	 * Attributes.  Each one names its source (the user story or epic that requires it) and the
	 * reason it is stored the way it is.
	 *
	 */

	/** Source: CS-3, CS-7, CS-8.  Assigned by the database, -1 until the lesson is stored.  An
	 *  identifier is used to name a lesson, rather than its title, because the title can change. */
	private int id;

	/** Source: CS-1, CS-2.  The one line shown in the list.  Required, at most 80 characters. */
	private String title;

	/** Source: CS-1, CS-5.  A short label used to group lessons and to filter by category.
	 *  Required, at most 30 characters. */
	private String category;

	/** Source: CS-1, CS-3.  The question or observation the lesson is about.  Required, because
	 *  a lesson with only a title records that something happened but not what. */
	private String description;

	/** Source: CS-1, CS-7.  The reply that resolved the question.  Optional, because a lesson can
	 *  be recorded the moment a problem is noticed and resolved later by an update. */
	private String resolution;

	/** Source: CS-4.  A comma separated list of words that the search also looks at, so a lesson
	 *  can be found by a word that does not appear in its title or description.  Optional. */
	private String keywords;

	/** Source: CS-1, CS-6, CS-9.  The username of the account that created the lesson.  It is set
	 *  from the logged in user by the screens, never typed, so it cannot be used to impersonate
	 *  someone, and it is what the ownership rule compares against. */
	private String contributor;

	/** Source: CS-2, CS-3.  When the lesson was created, so the list can show the newest first and
	 *  a reader can judge how current it is.  Set once by {@link #create}. */
	private String dateCreated;

	/** Source: CS-3, CS-7.  When the lesson was last changed, so a reader can see an edited lesson
	 *  has been edited.  Set by {@link #create} and by every {@link #update}. */
	private String dateUpdated;

	/** Source: TP3 staff epic (reviewing lessons).  The outcome of a staff review.  Stored now so
	 *  the schema does not need to change in TP3; contributors can see it but not change it. */
	private String reviewStatus;

	/** Source: TP3 staff epic (reviewing lessons).  The reviewer's comment to the contributor. */
	private String reviewerComment;

	/** Source: TP3 epic (marking a lesson as helpful, from the HW2 LessonShare evaluation).  How
	 *  many readers found the lesson helpful.  Starts at zero. */
	private int helpfulCount;


	/*-*******************************************************************************************
	 *
	 * Constructors
	 *
	 */

	/*****
	 * <p> Method: LessonsLearned() </p>
	 *
	 * <p> Description: The default constructor.  It leaves the identifier as -1 to show that this
	 * object does not yet correspond to a stored row, and gives the TP3 attributes their starting
	 * values. </p>
	 */
	public LessonsLearned() {
		this.id = -1;
		this.reviewStatus = REVIEW_NOT_REVIEWED;
		this.reviewerComment = "";
		this.helpfulCount = 0;
	}


	/*****
	 * <p> Method: LessonsLearned(String title, String category, String description,
	 *      String resolution, String keywords, String contributor) </p>
	 *
	 * <p> Description: The constructor used when a contributor is creating a brand new lesson.
	 * No identifier and no dates are supplied, because {@link #create} sets them when the lesson
	 * is stored. </p>
	 *
	 * @param title       the short heading for the lesson
	 * @param category    the area the lesson belongs to
	 * @param description the question or observation itself
	 * @param resolution  the reply that resolved the question, which may be empty
	 * @param keywords    the comma separated keywords, which may be empty
	 * @param contributor the username of the person contributing the lesson
	 */
	public LessonsLearned(String title, String category, String description, String resolution,
			String keywords, String contributor) {
		this();
		this.title = title;
		this.category = category;
		this.description = description;
		this.resolution = resolution;
		this.keywords = keywords;
		this.contributor = contributor;
	}


	/*****
	 * <p> Method: LessonsLearned(int id, String title, String category, String description,
	 *      String resolution, String keywords, String contributor, String dateCreated,
	 *      String dateUpdated, String reviewStatus, String reviewerComment, int helpfulCount) </p>
	 *
	 * <p> Description: The constructor used when a lesson is rebuilt from a database row, where
	 * every value is already known. </p>
	 *
	 * @param id              the database identifier
	 * @param title           the short heading
	 * @param category        the category
	 * @param description     the question or observation
	 * @param resolution      the reply that resolved it
	 * @param keywords        the comma separated keywords
	 * @param contributor     the username of the contributor
	 * @param dateCreated     when the lesson was created
	 * @param dateUpdated     when the lesson was last changed
	 * @param reviewStatus    the staff review status
	 * @param reviewerComment the staff reviewer's comment
	 * @param helpfulCount    how many readers found the lesson helpful
	 */
	public LessonsLearned(int id, String title, String category, String description,
			String resolution, String keywords, String contributor, String dateCreated,
			String dateUpdated, String reviewStatus, String reviewerComment, int helpfulCount) {
		this.id = id;
		this.title = title;
		this.category = category;
		this.description = description;
		this.resolution = resolution;
		this.keywords = keywords;
		this.contributor = contributor;
		this.dateCreated = dateCreated;
		this.dateUpdated = dateUpdated;
		this.reviewStatus = reviewStatus;
		this.reviewerComment = reviewerComment;
		this.helpfulCount = helpfulCount;
	}


	/*-*******************************************************************************************
	 *
	 * Create, Read, Update, Delete
	 *
	 */

	/*****
	 * <p> Method: Problem create(LessonsLearnedDatabase db) </p>
	 *
	 * <p> Description: Stores this lesson as a new row (CS-1).  The lesson is validated first and
	 * nothing is written if any field is refused, so an invalid lesson can never reach the
	 * database (CS-10, CS-11).  Both dates are set to now, and on success the identifier the
	 * database assigned is recorded in this object so that it can be updated or deleted straight
	 * away. </p>
	 *
	 * @param db the open lessons database
	 *
	 * @return null when the lesson was stored, otherwise the problem that stopped it
	 */
	public Problem create(LessonsLearnedDatabase db) {
		Problem problem = validate();
		if (problem != null)
			return problem;

		// A lesson that already has an identifier is already stored.  Creating it a second time
		// would make a duplicate row with a new identifier, so it is refused.
		if (id > 0)
			return new Problem("This lesson has already been saved",
					"Use update to change a lesson that is already stored.");

		String now = LocalDateTime.now().format(DATE_FORMAT);
		dateCreated = now;
		dateUpdated = now;

		int newId = db.addLesson(this);
		if (newId < 0)
			return new Problem("The lesson could not be saved",
					"The database refused the new lesson. Nothing was stored.");
		return null;
	}


	/*****
	 * <p> Method: static LessonsLearned read(LessonsLearnedDatabase db, int id) </p>
	 *
	 * <p> Description: Reads the stored lesson with the given identifier (CS-3).  It is static
	 * because there is no lesson object to call it on until the row has been read. </p>
	 *
	 * @param db the open lessons database
	 * @param id the identifier to read
	 *
	 * @return the stored lesson, or null when no lesson has that identifier
	 */
	public static LessonsLearned read(LessonsLearnedDatabase db, int id) {
		return db.getLessonById(id);
	}


	/*****
	 * <p> Method: Problem update(LessonsLearnedDatabase db) </p>
	 *
	 * <p> Description: Writes the current values of this lesson back to its row (CS-7).  The same
	 * validate method used by create is called first, so an edit cannot put a lesson into a state
	 * that creation would have refused.  The update date is set to now; the creation date and the
	 * contributor are never changed by an update. </p>
	 *
	 * <p> The ownership rule (CS-9) is enforced by the lesson editor before this is called,
	 * rather than here, because this method does not know who is logged in. </p>
	 *
	 * @param db the open lessons database
	 *
	 * @return null when the row was updated, otherwise the problem that stopped it
	 */
	public Problem update(LessonsLearnedDatabase db) {
		Problem problem = validate();
		if (problem != null)
			return problem;

		if (id <= 0)
			return new Problem("This lesson has not been saved yet",
					"A lesson has to be created before it can be updated.");

		// The old date is kept so that a failed write does not leave this object claiming a
		// change that the database does not have.
		String previousDate = dateUpdated;
		dateUpdated = LocalDateTime.now().format(DATE_FORMAT);

		if (!db.updateLesson(this)) {
			dateUpdated = previousDate;
			return new Problem("The lesson could not be updated",
					"No stored lesson has the identifier " + id + ". Nothing was changed.");
		}
		return null;
	}


	/*****
	 * <p> Method: boolean delete(LessonsLearnedDatabase db) </p>
	 *
	 * <p> Description: Removes this lesson's row (CS-8).  Asking the contributor to confirm is the
	 * screen's job, because the removal cannot be undone and only the screen can ask.  On success
	 * the identifier goes back to -1, so this object no longer claims to be stored. </p>
	 *
	 * @param db the open lessons database
	 *
	 * @return true when a row was removed, false when no lesson had this identifier
	 */
	public boolean delete(LessonsLearnedDatabase db) {
		if (id <= 0)
			return false;
		boolean removed = db.deleteLesson(id);
		if (removed)
			id = -1;
		return removed;
	}


	/*-*******************************************************************************************
	 *
	 * Validation, ownership, and searching
	 *
	 */

	/*****
	 * <p> Method: Problem validate() </p>
	 *
	 * <p> Description: Applies every input rule to this lesson's current values by calling
	 * LessonInputPolicy.validateLesson (CS-10, CS-11).  The rules themselves live in the
	 * validation package, next to the TP1 rules, so that the editor screen can check a single
	 * field without building a lesson, and so the tests can exercise each rule on its own. </p>
	 *
	 * @return null when every field is acceptable, otherwise the first problem found
	 */
	public Problem validate() {
		return LessonInputPolicy.validateLesson(title, category, description, resolution,
				keywords, contributor);
	}


	/*****
	 * <p> Method: boolean isOwnedBy(String userName) </p>
	 *
	 * <p> Description: Reports whether the given account contributed this lesson (CS-6, CS-9).
	 * The comparison is exact, because usernames are unique and case is part of a username in
	 * the supplied database. </p>
	 *
	 * @param userName the username of the logged in account
	 *
	 * @return true when that account is this lesson's contributor
	 */
	public boolean isOwnedBy(String userName) {
		return userName != null && contributor != null && contributor.equals(userName);
	}


	/*****
	 * <p> Method: boolean matches(String term) </p>
	 *
	 * <p> Description: Reports whether the term appears in the title, the description, or the
	 * keywords, ignoring case (CS-4).  The keywords are included in TP2 so a contributor can make
	 * a lesson findable by a word that does not appear in its text.  An empty term matches
	 * nothing, because an empty search is not a request to see everything. </p>
	 *
	 * @param term the search term
	 *
	 * @return true when the lesson contains the term
	 */
	public boolean matches(String term) {
		if (term == null || term.trim().isEmpty())
			return false;
		String needle = term.trim().toLowerCase();
		return contains(title, needle) || contains(description, needle)
				|| contains(keywords, needle);
	}

	// Null safe, case insensitive containment, so a lesson with an empty optional field can
	// still be searched.
	private static boolean contains(String haystack, String lowerCaseNeedle) {
		return haystack != null && haystack.toLowerCase().contains(lowerCaseNeedle);
	}


	/*****
	 * <p> Method: String toDisplayLine() </p>
	 *
	 * <p> Description: Produces one readable line describing this lesson, for the list on the
	 * Lessons Learned page (CS-2).  The identifier is included so the line can be matched to the
	 * lesson it describes even when two lessons have the same title. </p>
	 *
	 * @return a single formatted line describing this lesson
	 */
	public String toDisplayLine() {
		return String.format("#%-4d %-12s %s   (by %s)", id,
				"[" + (category == null ? "" : category) + "]",
				title == null ? "" : title,
				contributor == null ? "" : contributor);
	}


	/*-*******************************************************************************************
	 *
	 * Getters
	 *
	 */

	/** @return the database identifier, or -1 if the lesson has not been stored */
	public int getId() { return id; }

	/** @return the title */
	public String getTitle() { return title; }

	/** @return the category */
	public String getCategory() { return category; }

	/** @return the description */
	public String getDescription() { return description; }

	/** @return the resolution, which may be empty */
	public String getResolution() { return resolution; }

	/** @return the comma separated keywords, which may be empty */
	public String getKeywords() { return keywords; }

	/** @return the username of the contributor */
	public String getContributor() { return contributor; }

	/** @return when the lesson was created, in the DATE_FORMAT pattern */
	public String getDateCreated() { return dateCreated; }

	/** @return when the lesson was last changed, in the DATE_FORMAT pattern */
	public String getDateUpdated() { return dateUpdated; }

	/** @return the staff review status (TP3) */
	public String getReviewStatus() { return reviewStatus; }

	/** @return the staff reviewer's comment (TP3) */
	public String getReviewerComment() { return reviewerComment; }

	/** @return how many readers found the lesson helpful (TP3) */
	public int getHelpfulCount() { return helpfulCount; }


	/*-*******************************************************************************************
	 *
	 * Setters.  There are deliberately no setters for the contributor, the dates, or the TP3
	 * attributes after construction: the contributor is fixed when the lesson is created, the
	 * dates are set by create and update, and the TP3 attributes belong to the staff role.  The
	 * database layer is the only code that sets the identifier.
	 *
	 */

	/** @param id the identifier assigned by the database; used only by LessonsLearnedDatabase */
	public void setId(int id) { this.id = id; }

	/** @param title the new title */
	public void setTitle(String title) { this.title = title; }

	/** @param category the new category */
	public void setCategory(String category) { this.category = category; }

	/** @param description the new description */
	public void setDescription(String description) { this.description = description; }

	/** @param resolution the new resolution */
	public void setResolution(String resolution) { this.resolution = resolution; }

	/** @param keywords the new comma separated keywords */
	public void setKeywords(String keywords) { this.keywords = keywords; }
}
