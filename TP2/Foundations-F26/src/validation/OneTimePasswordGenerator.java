package validation;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******
 * <p> Title: OneTimePasswordGenerator Class. </p>
 * 
 * <p> Description: Builds the temporary password an administrator hands to a locked out user, for
 * story AD-2.</p>
 * 
 * <p> The algorithm is the one that was written inside the Admin controller.  It was moved here
 * unchanged for two reasons.  It belongs beside the password rules rather than inside a screen,
 * since it is not a user interface concern; and while it sat in the controller as a private method
 * no test could reach it, so the claim that a generated password always satisfies every rule was
 * only ever an argument rather than something demonstrated.  It is now exercised directly by the
 * one time password test suite.</p>
 * 
 * <p> One character is taken from each required group first, so the result cannot be missing a
 * group, and the rest are drawn from all of the groups together.  The characters are then shuffled,
 * because leaving them in group order would make the shape of every generated password identical
 * and therefore easier to guess.</p>
 * 
 * <p> SecureRandom is used rather than Random.  An ordinary random number generator started from a
 * predictable seed produces a predictable sequence, and a predictable temporary password is no
 * better than no password at all.</p>
 * 
 * <p> Visually ambiguous characters are left out on purpose.  A temporary password is usually read
 * aloud or copied by hand, and a capital I that might be a lower case l wastes the administrator's
 * time and the user's.</p>
 * 
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP1 additions by CSE 360 Team: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 * 
 * @author CSE 360 TP1 Team
 * 
 * @version 1.00  2026-09-23  Moved out of ControllerAdminHome so it can be tested, for story AD-2
 */
public class OneTimePasswordGenerator {

	/** Upper case letters, with I left out because it can be read as a lower case l. */
	public static final String UPPER_CASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";

	/** Lower case letters, with l left out for the same reason. */
	public static final String LOWER_CASE = "abcdefghijkmnpqrstuvwxyz";

	/** Digits, with 0 and 1 left out because they can be read as O and l. */
	public static final String DIGITS = "23456789";

	/** A small set of special characters that are easy to say out loud. */
	public static final String SPECIALS = "!@#$%^&*?";

	/** How long a generated password is.  Comfortably above the minimum of eight. */
	public static final int LENGTH = 12;

	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public OneTimePasswordGenerator() {
	}

	/**********
	 * <p> Method: generate </p>
	 * 
	 * <p> Description: Produces a temporary password that satisfies every password requirement by
	 * construction.</p>
	 * 
	 * @return a password that PasswordRecognizer will accept
	 */
	public static String generate() {
		SecureRandom randomSource = new SecureRandom();
		List<Character> characters = new ArrayList<>();

		// One from each group, so no requirement can be missed.
		characters.add(UPPER_CASE.charAt(randomSource.nextInt(UPPER_CASE.length())));
		characters.add(LOWER_CASE.charAt(randomSource.nextInt(LOWER_CASE.length())));
		characters.add(DIGITS.charAt(randomSource.nextInt(DIGITS.length())));
		characters.add(SPECIALS.charAt(randomSource.nextInt(SPECIALS.length())));

		// Then enough more to comfortably clear the minimum length.
		String everything = UPPER_CASE + LOWER_CASE + DIGITS + SPECIALS;
		while (characters.size() < LENGTH)
			characters.add(everything.charAt(randomSource.nextInt(everything.length())));

		// Shuffle so the first four positions do not always hold the same kinds of character.
		Collections.shuffle(characters, randomSource);

		StringBuilder thePassword = new StringBuilder();
		for (char oneCharacter : characters)
			thePassword.append(oneCharacter);

		return thePassword.toString();
	}
}
