package validation;

/*******
 * <p> Title: ScreenInputPolicy Class. </p>
 * 
 * <p> Description: Holds the exact sequence of checks each screen applies to the values typed
 * into it, in the exact order the screen applies them.</p>
 * 
 * <p> The reason this class exists is a defect the team found late in the sprint.  The three
 * original test suites exercise the individual recognizers thoroughly, so every rule was known
 * to be correct, but nothing tested whether a given screen actually called those rules.  The
 * login page had therefore reached the database with values that had never been examined, and no
 * test failed, because no test knew what the login page was supposed to do.</p>
 * 
 * <p> Putting the per-screen sequences here fixes that at the root.  A controller no longer
 * contains its own chain of checks; it calls the method for its screen and reports whatever comes
 * back.  The automated tests call the same methods.  A screen and its tests therefore cannot
 * disagree about which checks run, or about the order they run in, because there is only one copy
 * of that decision.</p>
 * 
 * <p> The order of the checks inside each method is deliberate and is part of what is being
 * tested.  Length is always examined first so that an over long value is refused without any
 * detailed rule ever walking it, which is the defence described in InputLimits.  Two passwords
 * are only compared once the first one is known to be usable, because reporting a mismatch
 * between two unusable values tells the user nothing helpful.</p>
 * 
 * <p> This class deliberately has no reference to JavaFX and performs no database access, so
 * every sequence in it can be exercised without opening a window or creating a database.  Where
 * a screen has to consult the database in the middle of its sequence, the sequence is split into
 * the part before that query and the part after it, so the pure checks stay testable and the
 * order is still preserved.</p>
 * 
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP1 additions by CSE 360 Team: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-22  Initial version for Team Project Phase 1
 */
public class ScreenInputPolicy {

	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public ScreenInputPolicy() {
	}

	/**********
	 * <p> Record: Problem </p>
	 * 
	 * <p> Description: One refused input, described in the two parts a screen needs in order to
	 * tell the user about it: a short heading, and the detail underneath.
	 * 
	 * A null Problem means the input was acceptable.  Returning null rather than an empty object
	 * keeps the test for "was anything wrong" to a single comparison at every call site.</p>
	 * 
	 * @param title  the short heading, for example "The username is too long"
	 * @param detail the explanation shown underneath, naming the field and the limit
	 */
	public record Problem(String title, String detail) {
	}

	/*-********************************************************************************************
	 * 
	 * The login page
	 * 
	 */

	/**********
	 * <p> Method: validateLogin </p>
	 * 
	 * <p> Description: Checks the username and password typed on the login page before either
	 * value is used to query the database.
	 * 
	 * Only the length is checked here, and that is a deliberate decision rather than an
	 * omission.  An account created before a rule changed still has to be able to sign in, so
	 * applying the current username rules on this page would lock out existing users, which is a
	 * worse failure than the one it would prevent.  There is also a disclosure reason: the
	 * supplied code answers a failed login with a single "Incorrect username/password" message
	 * precisely so an attacker cannot learn which half of the pair was wrong, and telling someone
	 * their username is the wrong shape would undo that.
	 * 
	 * Length is different in kind.  It is a safety check rather than a rules check, and without it
	 * these two values reached a database query without ever being examined.</p>
	 * 
	 * @param username the username typed on the login page
	 * @param password the password typed on the login page
	 * 
	 * @return null when both values are an acceptable length, otherwise the first problem found
	 */
	public static Problem validateLogin(String username, String password) {

		String problem = InputLimits.checkLength(username, InputLimits.MAX_USERNAME, "Username");
		if (!problem.isEmpty())
			return new Problem("The username is too long", problem);

		problem = InputLimits.checkLength(password, InputLimits.MAX_PASSWORD, "Password");
		if (!problem.isEmpty())
			return new Problem("The password is too long", problem);

		return null;
	}

	/*-********************************************************************************************
	 * 
	 * The invitation code, which is typed on the login page and travels to the New Account page
	 * 
	 */

	/**********
	 * <p> Method: validateInvitationCode </p>
	 * 
	 * <p> Description: Checks an invitation code before it is used to look anything up.
	 * 
	 * The same method is used by the login page, where the code is typed, and by the New Account
	 * page, which receives it.  The code is checked at the point it is typed so that an over long
	 * value never reaches a database query, and it is checked again on arrival because the New
	 * Account page is reachable from more than one place and should not depend on its caller
	 * having done the work.</p>
	 * 
	 * @param invitationCode the code the user typed
	 * 
	 * @return null when the code is an acceptable length, otherwise the problem found
	 */
	public static Problem validateInvitationCode(String invitationCode) {

		if (InputLimits.isEmpty(invitationCode))
			return new Problem("No invitation code was entered",
					"Enter the invitation code that was sent to you, then press Setup Account.");

		String problem = InputLimits.checkLength(invitationCode, InputLimits.MAX_INVITATION,
				"Invitation code");
		if (!problem.isEmpty())
			return new Problem("The invitation code is too long", problem);

		return null;
	}

	/*-********************************************************************************************
	 * 
	 * The New Account page
	 * 
	 */

	/**********
	 * <p> Method: validateNewAccountBeforeLookup </p>
	 * 
	 * <p> Description: The first part of the New Account sequence: everything that can be decided
	 * without asking the database anything.
	 * 
	 * The sequence stops before the point where the controller has to ask the database whether the
	 * username is already taken.  Splitting it here means the database is never asked about a
	 * username that is already known to be unusable, and it keeps this part of the sequence free
	 * of any database reference so the tests can drive it directly.</p>
	 * 
	 * @param invitationCode the invitation code this page was opened with
	 * @param username       the username the user typed
	 * 
	 * @return null when both values are acceptable, otherwise the first problem found
	 */
	public static Problem validateNewAccountBeforeLookup(String invitationCode, String username) {

		// The invitation code is what allowed this page to open, so it is checked first.
		Problem invitationProblem = validateInvitationCode(invitationCode);
		if (invitationProblem != null)
			return invitationProblem;

		// The username: length first, then the rules the team agreed on in HW1.
		String problem = InputLimits.checkLength(username, InputLimits.MAX_USERNAME, "Username");
		if (!problem.isEmpty())
			return new Problem("The username is too long", problem);

		problem = UserNameRecognizer.checkForValidUserName(username);
		if (!problem.isEmpty())
			return new Problem("That username cannot be used", problem);

		return null;
	}

	/**********
	 * <p> Method: validateNewAccountAfterLookup </p>
	 * 
	 * <p> Description: The second part of the New Account sequence: the two password fields.
	 * 
	 * This runs once the username has been accepted and found not to be taken.  Both password
	 * fields are length checked, including the confirmation field, because a user can paste a very
	 * long value into either box and the confirmation box is just as much a way into the program as
	 * the first one.  The two are only compared afterwards, since comparing them is pointless
	 * until the first is known to be a usable password.</p>
	 * 
	 * @param password1 the first password the user typed
	 * @param password2 the confirmation password the user typed
	 * 
	 * @return null when the passwords are acceptable and identical, otherwise the problem found
	 */
	public static Problem validateNewAccountAfterLookup(String password1, String password2) {

		String problem = InputLimits.checkLength(password1, InputLimits.MAX_PASSWORD, "Password");
		if (!problem.isEmpty())
			return new Problem("The password is too long", problem);

		problem = PasswordRecognizer.evaluatePassword(password1);
		if (!problem.isEmpty())
			return new Problem("That password does not meet the requirements",
					describePasswordProblem(problem));

		// The confirmation box gets its own length check rather than being trusted because it is
		// only ever compared.  A 5000 character paste here must be refused for the same reason it
		// is refused in the first box.
		problem = InputLimits.checkLength(password2, InputLimits.MAX_PASSWORD,
				"Confirmation password");
		if (!problem.isEmpty())
			return new Problem("The confirmation password is too long", problem);

		if (password1.compareTo(password2) != 0)
			return new Problem("The two passwords do not match",
					"The password and its confirmation must be identical. "
					+ "Please type both of them again.");

		return null;
	}

	/**********
	 * <p> Method: validateNewAccount </p>
	 * 
	 * <p> Description: Runs both parts of the New Account sequence back to back, for callers that
	 * have no database to consult.
	 * 
	 * The automated tests use this so that one call exercises the whole sequence in the same order
	 * the screen applies it.  The screen itself does not use this method, because it has to make
	 * its "is this username taken" query between the two parts.</p>
	 * 
	 * @param invitationCode the invitation code this page was opened with
	 * @param username       the username the user typed
	 * @param password1      the first password the user typed
	 * @param password2      the confirmation password the user typed
	 * 
	 * @return null when every value is acceptable, otherwise the first problem found
	 */
	public static Problem validateNewAccount(String invitationCode, String username,
			String password1, String password2) {

		Problem problem = validateNewAccountBeforeLookup(invitationCode, username);
		if (problem != null)
			return problem;

		return validateNewAccountAfterLookup(password1, password2);
	}

	/**********
	 * <p> Method: validatePasswordChange </p>
	 * 
	 * <p> Description: Checks a replacement password and its confirmation, for the screen that
	 * appears when a user signing in with a one time password has to choose a new one.
	 * 
	 * The checks are exactly those applied when an account is first created, in the same order,
	 * because a replacement password has to satisfy the same requirements as an original one.  The
	 * work is delegated rather than restated so that a change to the password rules cannot leave
	 * this path behind.</p>
	 * 
	 * @param newPassword  the replacement the user typed
	 * @param confirmation the second copy the user typed
	 * 
	 * @return null when the replacement is acceptable and may be saved, otherwise the problem
	 */
	public static Problem validatePasswordChange(String newPassword, String confirmation) {
		return validateNewAccountAfterLookup(newPassword, confirmation);
	}

	/*-********************************************************************************************
	 * 
	 * The first administrator page
	 * 
	 */

	/**********
	 * <p> Method: validateFirstAdmin </p>
	 * 
	 * <p> Description: Checks the username and the two passwords typed on the first administrator
	 * page.
	 * 
	 * The very first account created in the system is an administrator, so letting an unchecked
	 * username or a weak password through here would leave the account with the most authority as
	 * the weakest one.  There is no invitation code on this page and no existing account to
	 * collide with, so the sequence is the username followed by the two passwords.</p>
	 * 
	 * @param username  the username the administrator typed
	 * @param password1 the first password the administrator typed
	 * @param password2 the confirmation password the administrator typed
	 * 
	 * @return null when every value is acceptable, otherwise the first problem found
	 */
	public static Problem validateFirstAdmin(String username, String password1, String password2) {

		String problem = InputLimits.checkLength(username, InputLimits.MAX_USERNAME, "Username");
		if (!problem.isEmpty())
			return new Problem("The username is too long", problem);

		problem = UserNameRecognizer.checkForValidUserName(username);
		if (!problem.isEmpty())
			return new Problem("That username cannot be used", problem);

		problem = InputLimits.checkLength(password1, InputLimits.MAX_PASSWORD, "Password");
		if (!problem.isEmpty())
			return new Problem("The password is too long", problem);

		problem = PasswordRecognizer.evaluatePassword(password1);
		if (!problem.isEmpty())
			return new Problem("That password does not meet the requirements",
					describePasswordProblem(problem));

		problem = InputLimits.checkLength(password2, InputLimits.MAX_PASSWORD,
				"Confirmation password");
		if (!problem.isEmpty())
			return new Problem("The confirmation password is too long", problem);

		if (password1.compareTo(password2) != 0)
			return new Problem("The two passwords do not match",
					"The password and its confirmation must be identical. "
					+ "Please type both of them again.");

		return null;
	}

	/*-********************************************************************************************
	 * 
	 * Shared wording
	 * 
	 */

	/**********
	 * <p> Method: describePasswordProblem </p>
	 * 
	 * <p> Description: Adds the full list of password requirements underneath whatever the
	 * recognizer reported, so that a user who is told one thing is missing can still see all of
	 * what is expected without going looking for it.</p>
	 * 
	 * @param recognizerMessage the message the password recognizer produced
	 * 
	 * @return the message the screen should show
	 */
	private static String describePasswordProblem(String recognizerMessage) {
		return recognizerMessage
				+ "\n\nA password needs an upper case letter, a lower case letter, a digit, "
				+ "a special character, at least " + PasswordRecognizer.MIN_LENGTH
				+ " characters, and no more than " + InputLimits.MAX_PASSWORD + ".";
	}
}
