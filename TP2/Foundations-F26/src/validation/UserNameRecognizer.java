package validation;


public class UserNameRecognizer {
	/**
	 * <p> Title: FSM-translated UserNameRecognizer. </p>
	 * 
	 * <p> Description: A demonstration of the mechanical translation of a Finite State Machine 
	 * diagram into an executable Java program using the UserName Recognizer. The code detailed 
	 * design is based on a while loop with a select list.</p>
	 * 
	 * <p> The FSM enforces the following rules for a valid UserName: </p>
	 * <ul>
	 *   <li>The first character must be alphabetic (A-Z or a-z); digits are not allowed at
	 *       the start because a purely numeric prefix makes usernames harder for humans to 
	 *       distinguish and conflicts with many system naming conventions.</li>
	 *   <li>Subsequent characters may be A-Z, a-z, or 0-9.</li>
	 *   <li>The characters '.', '-', and '&amp;' may appear as separators between alphanumeric 
	 *       substrings (e.g., "one.two", "one-two", "one&amp;two" are all valid), but they may 
	 *       not appear consecutively and may not appear at the end of the name.</li>
	 *   <li>The total length must be between 4 and 16 characters (inclusive).</li>
	 * </ul>
	 * 
	 * <p> Copyright: Lynn Robert Carter © 2024, modified by Arush Ugal 2026 </p>
	 * 
	 * @author Lynn Robert Carter
	 * @author Arush Ugal
	 * 
	 * @version 1.00		2024-09-13	Initial baseline derived from the Even Recognizer
	 * @version 1.01		2024-09-17	Correction to address UNChar coding error, improper error
	 * 									message, and improve internal documentation
	 * @version 2.00		2026-08-31	First character restricted to alphabetic only (A-Z, a-z);
	 * 									'&amp;' added as a valid separator alongside '.' and '-';
	 * 									error messages updated to reflect the new constraints
	 * 
	 */

	/**********************************************************************************************
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	public static String userNameRecognizerErrorMessage = "";	// The error message text
	public static String userNameRecognizerInput = "";			// The input being processed
	public static int userNameRecognizerIndexofError = -1;		// The index of error location
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running
	private static int userNameSize = 0;				// A username may not exceed 16 characters

	/**********
	 * Private method to display debugging data showing the FSM trace during execution.
	 * Each row shows the current state, whether it is a final state, the character being 
	 * examined, the next state, and the running username length.
	 */
	private static void displayDebuggingInfo() {
		if (currentCharNdx >= inputLine.length())
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "None");
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
				((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
				((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
				nextState + "     " + userNameSize);
	}
	
	/**********
	 * Private method to advance to the next input character.
	 * When the end of the input is reached, currentChar is set to a space and the running
	 * flag is cleared so the FSM loop exits on the next iteration.
	 */
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * This method is a mechanical transformation of the updated Finite State Machine diagram
	 * into a Java method.
	 * 
	 * <p>State summary:</p>
	 * <ul>
	 *   <li>State 0 – Start: accepts only A-Z or a-z (alphabetic), transitions to State 1.
	 *       Digits are explicitly rejected here; that is the key change from version 1.x.</li>
	 *   <li>State 1 – Inside a word (final state): accepts A-Z, a-z, 0-9 (loop back to 1),
	 *       or any of the three separators '.', '-', '&amp;' (transition to State 2).</li>
	 *   <li>State 2 – Just saw a separator (non-final): the next character must be alphanumeric
	 *       to transition back to State 1.  Two consecutive separators are rejected because
	 *       the FSM stays in State 2 on a separator input and then halts.</li>
	 * </ul>
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An empty string if the username is valid, or a descriptive error message
	 * 						that explains exactly what is wrong and where
	 */
	public static String checkForValidUserName(String input) {
		// Reject the empty-string case immediately; there is nothing for the FSM to process
		if (input.length() <= 0) {
			userNameRecognizerIndexofError = 0;
			return "\n*** ERROR *** The input is empty";
		}
		
		// Initialize all FSM tracking variables for a fresh run
		state = 0;
		inputLine = input;
		currentCharNdx = 0;
		currentChar = input.charAt(0);

		userNameRecognizerInput = input;
		running = true;
		nextState = -1;
		System.out.println("\nCurrent Final Input  Next\nState   State Char  State  Size");
		
		// Reset the character count; it is incremented whenever a character is consumed
		userNameSize = 0;

		while (running) {
			switch (state) {

			case 0:
				// State 0 – Start state.
				//
				// Only an alphabetic character (A-Z, a-z) is accepted as the first character.
				// Digits are rejected here intentionally: requiring an alphabetic start makes
				// usernames more human-readable and avoids collisions with numeric identifiers
				// used elsewhere in the system.
				//
				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z') ||
						(currentChar >= 'a' && currentChar <= 'z')) {
					nextState = 1;
					
					// Count this first character toward the length limit
					userNameSize++;
					
					// A single character cannot exceed the 16-character ceiling, so no size
					// check is needed on this transition.
				}
				// Any other character (including digits) causes an immediate halt
				else
					running = false;
				
				break;
			
			case 1:
				// State 1 – Inside an alphanumeric word segment (this is the only final state).
				//
				// From here the FSM can either:
				//   (a) stay in State 1 on another alphanumeric character, or
				//   (b) move to State 2 on a separator character ('.', '-', or '&').
				//
				// The '&' separator was added in version 2.00.  It behaves identically to '.'
				// and '-': it is counted toward the total length and must be followed by at
				// least one alphanumeric character (enforced by State 2).
				
				// A-Z, a-z, 0-9 -> State 1 (remain inside the current word segment)
				if ((currentChar >= 'A' && currentChar <= 'Z') ||
						(currentChar >= 'a' && currentChar <= 'z') ||
						(currentChar >= '0' && currentChar <= '9')) {
					nextState = 1;
					userNameSize++;
				}
				// '.', '-', or '&' -> State 2 (just consumed a separator)
				else if (currentChar == '.' || currentChar == '-' || currentChar == '&') {
					nextState = 2;
					
					// The separator is part of the name and counts toward the length limit
					userNameSize++;
				}
				// Anything else terminates the FSM
				else
					running = false;
				
				// Stop if the username has grown beyond the 16-character ceiling
				if (userNameSize > 16)
					running = false;
				break;			
				
			case 2:
				// State 2 – Separator just consumed (non-final state).
				//
				// A separator must be followed by at least one alphanumeric character.  If
				// another separator appears here the FSM halts (consecutive separators such as
				// "..", "--", "&&", or mixed ".&" are all invalid by design).
				
				// A-Z, a-z, 0-9 -> State 1 (start a new word segment after the separator)
				if ((currentChar >= 'A' && currentChar <= 'Z') ||
						(currentChar >= 'a' && currentChar <= 'z') ||
						(currentChar >= '0' && currentChar <= '9')) {
					nextState = 1;
					userNameSize++;
				}
				// Any other character (including another separator) terminates the FSM
				else
					running = false;

				// Stop if the username has grown beyond the 16-character ceiling
				if (userNameSize > 16)
					running = false;
				break;			
			}
			
			if (running) {
				displayDebuggingInfo();
				moveToNextCharacter();
				state = nextState;
				
				// State 1 is the only final state; update the flag on every transition
				if (state == 1) finalState = true;
				else finalState = false;

				nextState = -1;
			}
		}
		displayDebuggingInfo();
		
		System.out.println("The loop has ended.");
		
		// Post-loop error analysis.
		// The FSM has halted; determine whether it halted because it ran out of valid input
		// (success) or because it encountered an invalid character or condition (failure).
		userNameRecognizerIndexofError = currentCharNdx;
		userNameRecognizerErrorMessage = "\n*** ERROR *** ";
		
		switch (state) {
		case 0:
			// State 0 is not a final state.  The very first character was invalid.
			// The error message is specific: digits are now explicitly disallowed as the
			// first character, so the message names the accepted set precisely.
			userNameRecognizerErrorMessage += 
				"A UserName must start with an alphabetic character (A-Z or a-z).\n" +
				"Digits and special characters are not allowed as the first character.\n";
			return userNameRecognizerErrorMessage;

		case 1:
			// State 1 is the only final state.  Check length constraints and whether the
			// entire input was consumed.
			if (userNameSize < 4) {
				userNameRecognizerErrorMessage += 
					"A UserName must have at least 4 characters.\n";
				return userNameRecognizerErrorMessage;
			}
			else if (userNameSize > 16) {
				userNameRecognizerErrorMessage += 
					"A UserName must have no more than 16 characters.\n";
				return userNameRecognizerErrorMessage;
			}
			else if (currentCharNdx < input.length()) {
				// The FSM stopped before consuming all input.  The character at currentCharNdx
				// is the offending one.
				userNameRecognizerErrorMessage += 
					"A UserName may only contain A-Z, a-z, 0-9, '.', '-', or '&'.\n";
				return userNameRecognizerErrorMessage;
			}
			else {
				// All characters consumed, length in range: the username is valid
				userNameRecognizerIndexofError = -1;
				userNameRecognizerErrorMessage = "";
				return userNameRecognizerErrorMessage;
			}

		case 2:
			// State 2 is not a final state.  The input ended immediately after a separator
			// (e.g., "abc.") or contained a consecutive separator (e.g., "ab..cd", "ab&&cd").
			// Both conditions are caught here with a single, clear message.
			userNameRecognizerErrorMessage +=
				"A separator ('.', '-', or '&') must be followed by an alphanumeric character.\n" +
				"Consecutive separators and trailing separators are not allowed.\n";
			return userNameRecognizerErrorMessage;
			
		default:
			// Defensive branch: should never be reached in a correctly implemented FSM
			return "";
		}
	}
}
