# TP2 Main Video Scripts — Team 24

Course: CSE 360, 2026 Fall C
Team 24: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev

TA confirmed: all three main screencasts may be recorded in one session.
TA confirmed: end-to-end walkthrough is acceptable as long as the full submission is present.

---

## The three screencasts

| File name | Task | Duration | Presenters |
|---|---|---|---|
| `LessonsLearnedScreencast.mp4` | Task 9.1 — shows Task 3 Javadoc | 5–10 min | Arush + Anirudh |
| `PhaseTwoGUIScreencast.mp4` | Task 9.2 — shows Task 4 Javadoc | 5–10 min | Moksha + Karan |
| `TestingScreencast.mp4` | Task 9.3 — shows Task 7 Javadoc + live tests | 8–15 min | Pranjal + Anirudh |

Place all three in the **TP2/** root folder (not inside a subfolder).

**Before recording each screencast:**
1. Open the matching Javadoc folder in your browser
2. Open IntelliJ/Eclipse with the source file visible
3. Start Zoom, share screen, record to Cloud
4. Follow the script — do not read word-for-word, talk naturally

---

## Part 1 — LessonsLearnedScreencast (5–10 min)
### Arush Ugal + Anirudh B

**[ARUSH opens `Lessons_Learned_Javadocs/` in Finder, then opens `index.html` in browser]**

**ARUSH:** This is the LessonsLearnedScreencast for Team 24. I am Arush Ugal and I am joined by Anirudh B. We will walk through the contents of the Lessons-Learned-Javadocs folder, which documents Task 3 of Team Project Phase 2.

This folder contains the Javadoc HTML for the two entity classes: LessonsLearned and LessonsLearnedList, plus supporting classes LessonsLearnedDatabase and LessonInputPolicy.

**[Click `entityClasses/LessonsLearned.html`]**

**ARUSH:** I will start with LessonsLearned. This class represents a single lesson learned. It has twelve attributes. The first six are the contributor-facing fields: title, category, description, resolution, keywords, and contributor. Contributor is set automatically from the logged-in user, so a contributor cannot record a lesson in someone else's name.

The last six support TP3: dateCreated, dateUpdated, reviewStatus, reviewerComment, helpfulCount, and the REVIEW-NOT-REVIEWED constant. Those are present now so the entity does not need reworking when the staff review epic is implemented.

**[Scroll to constructor section]**

The class has three constructors. The six-argument constructor is what the GUI uses to create a new lesson. The twelve-argument constructor is what the database layer uses to rebuild a lesson from a stored row. This separation means the GUI never needs to know the internal identifier until after the lesson is stored.

**[Open screenshot `3.4-LessonsLearned-attributes.png`]**

**ARUSH:** This screenshot shows the attribute declarations and their Javadoc comments. Each attribute has a Source field naming the user story it supports and a Rationale explaining why it is there.

**[Open screenshot `3.4-LessonsLearned-create.png`]**

Here is the create CRUD method. It validates the lesson, stamps both dates to now, calls the database, and records the assigned identifier. If validation fails it returns the Problem immediately and nothing is written. This is Contributor Story CS-1.

**ANIRUDH:** I will now cover LessonsLearnedList.

**[Click `entityClasses/LessonsLearnedList.html`]**

This class wraps a list of LessonsLearned objects and provides both in-memory operations and persistent CRUD. readAll reads every lesson from the database in newest-first order. createLesson validates, writes to the database, and prepends to the in-memory list on success. updateLesson and deleteLesson keep the in-memory list in sync so the GUI does not need to re-read the database after every change.

**[Open screenshot `3.5-LessonsLearnedList-search.png`]**

The three subset methods — searchByText, filterByCategory, and filterByContributor — each return a new LessonsLearnedList rather than a bare Java list. Returning the same type means the caller can chain filters: search for "JDBC" and then filter by category "Database" in two lines.

**[Open screenshot `3.1-entityClasses-package.png`]**

Finally, this screenshot shows the entityClasses package as it appears in the project: LessonsLearned, LessonsLearnedList, and the existing User class. The two TP2 classes sit in the same package as User, exactly as the rubric requires.

That concludes the LessonsLearnedScreencast. Thank you.

---

## Part 2 — PhaseTwoGUIScreencast (5–10 min)
### Moksha Dev + Karan Patel

**[MOKSHA opens `Contributor_Javadocs/index.html`]**

**MOKSHA:** This is the PhaseTwoGUIScreencast for Team 24. I am Moksha Dev and I am joined by Karan Patel. We will walk through the Contributor-Javadocs folder, which documents Task 4.

**[Open screenshot `4.1-gui-packages.png`]**

**MOKSHA:** This screenshot shows the three new GUI packages we added: guiLessonsList, guiLessonEditor, and guiLessonDetail. Each contains three classes: a Model, a View, and a Controller, following the MVC pattern of the supplied Foundations code.

**[Click `guiLessonsList/ViewLessonsList.html`]**

**MOKSHA:** ViewLessonsList is the list page. It shows every lesson, newest first, with a search box, a category dropdown, and a "show only my lessons" checkbox. The New Lesson and Open buttons are at the bottom.

**[Open screenshot `4.2-ViewLessonsList-mvc.png`]**

This screenshot shows the top of the ViewLessonsList constructor. The class is a singleton — the if-null check around the constructor guarantees the widgets are built only once. This is the same pattern the supplied guiRole1 and guiNewAccount packages use.

**KARAN:** I will cover the Model classes, which are where the decisions are made.

**[Click `guiLessonsList/ModelLessonsList.html`]**

**KARAN:** ModelLessonsList holds the full list of every stored lesson and the subset currently shown on screen. The decision about which lessons to show is in this class, not in the View or the Controller. That matters for Task 7: because the Model contains no JavaFX, the testing automation can call its methods directly without opening a window.

**[Open screenshot `4.4-ModelLessonsList-applyFilters.png`]**

This screenshot shows the applyFilters method. It narrows the full list in three independent steps: search, category, and ownership. Each step is skipped when the user has not asked for it.

**[Click `guiLessonEditor/ModelLessonEditor.html`]**

**KARAN:** ModelLessonEditor handles create and update. saveNewLesson builds a LessonsLearned object from the typed values, trims all fields, tidies the keywords into standard form, and calls createLesson on the shared list. saveChanges adds an ownership check and a "nothing has changed" check, so the database is not written unless something actually changed.

**[Open screenshot `4.5-ModelLessonEditor-saveNew.png`]**

This screenshot shows saveNewLesson. The contributor argument comes from the Controller, which passes the logged-in username. The contributor field is never typed by the user.

**MOKSHA:** The detail page works the same way. ModelLessonDetail.canChange returns true only when the logged-in username matches the lesson's contributor, and the View uses that to enable or disable the Edit and Delete buttons. That is Contributor Story CS-9.

That concludes the PhaseTwoGUIScreencast. Thank you.

---

## Part 3 — TestingScreencast (8–15 min)
### Pranjal Ganvir + Anirudh B

**[PRANJAL opens `ContributorTests_Javadocs/index.html`]**

**PRANJAL:** This is the TestingScreencast for Team 24. I am Pranjal Ganvir and I am joined by Anirudh B. We will walk through the ContributorTests-Javadocs folder and then show the execution of every test.

**[Open `Requirements to Tests Map.pdf` from TP2 root]**

**PRANJAL:** This document lists every Contributor requirement and for each one names the tests that show it is satisfied and the HTML file that documents those tests. There are twelve requirements, CS-1 through CS-12. Each has at least one positive test and one negative test.

**[Scroll through the table]**

For example, CS-11 covers length limits. The positive tests — VAL-04, VAL-08, VAL-12, VAL-13, VAL-16 — confirm that inputs at or under the limit are accepted. The negative tests — VAL-05, VAL-09, VAL-14, VAL-17 — confirm that inputs over the limit are refused.

VAL-12 is worth highlighting specifically. It tests a description of 300 characters. In HW2 this test would have failed, because the shared InputLimits class has a 256-character ceiling. We found the bug during TP2 iteration, added the checkLongText method to LessonInputPolicy, and the test now passes.

**[Click `testingAutomation/LessonValidationTestingAutomation.html`]**

**PRANJAL:** The Javadoc for LessonValidationTestingAutomation explains what each test case checks, which requirement it covers, and how to read the output. Each test method is individually documented.

**[Open screenshot `7.4-VAL-01-09.png`]**

This screenshot shows the source code for the title and category tests. Each test label includes the test ID, a description, and the requirement number in parentheses.

**ANIRUDH:** I will cover the CRUD and list tests.

**[Click `testingAutomation/LessonsCrudTestingAutomation.html`]**

**ANIRUDH:** LessonsCrudTestingAutomation runs every test against an in-memory H2 database — jdbc:h2:mem: — so the production database file is never touched. The setup code opens the in-memory database in main and passes it to runTests. The finally block closes it regardless of what happens.

**[Open screenshot `7.4-CRUD-01-04.png`]**

CRUD-01 creates a valid lesson and checks that the database assigned it an id greater than zero. CRUD-03 reads it back and checks the title matches. CRUD-04 tries to read a non-existent id and checks that null is returned.

**[Open screenshot `7.4-CRUD-05-12.png`]**

CRUD-07 deletes the lesson and checks that it can no longer be retrieved. CRUD-08 tries to delete the same id again and checks that false is returned.

**PRANJAL:** Now let me show the actual execution.

**[Open terminal in TP2/Foundations-F26, run the command:]**

```
java -cp bin:/Users/arushugal/h2/bin/h2-2.4.240.jar testingAutomation.TP2TestRunner
```

**[Scroll through the output as each suite runs]**

You can see every test printing Pass. Twenty-five validation tests, twelve CRUD tests, ten list tests, sixteen model tests. At the bottom: total passed 63, total failed 0. Every test behaved as expected.

The model tests are particularly important for the rubric: they prove the GUI decision logic works without opening a window. MOD-11 and MOD-12 show the ownership rule: MOD-11 confirms the owner can modify their lesson; MOD-12 confirms a different user cannot.

**[Show screenshot `7.4-TP2TestRunner.png`]**

This is the TP2TestRunner source. It runs all four suites in sequence and prints a combined total, so the entire test suite can be demonstrated from one entry point, exactly as the TP1TestRunner did.

That concludes the TestingScreencast. Thank you for watching.

---

## Optional end-to-end demo segment (TA-approved addition)

If you want a continuous live walkthrough to tie everything together, add this at the end of the TestingScreencast (keeping total under 15 min):

1. Log in as a Role1 user and click "Lessons Learned"
2. The list page opens (empty — show the message CS-2 produces)
3. Press "New Lesson" and type: Title = "H2 file lock prevents a second connection", Category = "Database", Description = (paste a 300-char description), Keywords = "H2, JDBC, lock"
4. Press Save — lesson appears in list
5. Type "JDBC" in search box — lesson still shown (CS-4)
6. Click "Open" — detail page shows all fields read-only (CS-3)
7. Press "Edit" — editor opens with fields pre-filled (CS-7)
8. Change the title slightly, press "Save Changes"
9. Press "Back" — returns to list, updated title visible
10. Press "Edit" again, then "Delete" with confirmation (CS-8)
11. Return to list — empty again, with the CS-2 message

One person narrates each step; the other drives. This segment is not required but demonstrates the full contributor workflow in a single take.
