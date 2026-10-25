#!/usr/bin/env python3
"""Builds every screenshot the TP2 Javadoc folders need, from the current source.

Code screenshots are typeset with LaTeX listings straight from the .java files, with the real
line numbers, so a screenshot can never drift from the code it claims to show.  Javadoc page
screenshots are taken with headless Chrome from the generated HTML.  Run after
working/build_javadoc.sh, which wipes the HTML but keeps the PNG files.

    python3 working/screenshots/make_screenshots.py
"""
import os, re, shutil, subprocess, sys, tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
TP2 = os.path.normpath(os.path.join(HERE, "..", "..", "TP2"))
SRC = os.path.join(TP2, "Foundations-F26", "src")
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
FX = os.path.expanduser("~/Projects/ASU-Foundations-F26/tools/javafx-sdk-25.0.4/lib")
H2 = os.path.expanduser("~/Projects/ASU-Foundations-F26/tools/h2-2.3.232.jar")
MAX_LINES = 72

LL, GUI, TST = "Lessons_Learned_Javadocs", "Contributor_Javadocs", "ContributorTests_Javadocs"


# ------------------------------------------------------------------------------------------
# Locating code
# ------------------------------------------------------------------------------------------

def lines_of(rel):
    with open(os.path.join(SRC, rel), encoding="utf-8") as f:
        return f.read().split("\n")


def find(rel, pattern, after=1):
    """1-based number of the first line at or after `after` that matches `pattern`."""
    rx = re.compile(pattern)
    for i, line in enumerate(lines_of(rel)[after - 1:], start=after):
        if rx.search(line):
            return i
    sys.exit(f"pattern not found in {rel}: {pattern}")


def doc_start(rel, line):
    """Walks up from a declaration to the first line of the comment block above it."""
    ls = lines_of(rel)
    i = line - 1
    while i - 1 >= 0 and re.match(r"\s*(\*|/\*|//)", ls[i - 1]):
        i -= 1
        if re.match(r"\s*/\*", ls[i]):
            break
    return i + 1


def block_end(rel, line):
    """Line holding the brace that closes the block opened on or after `line`."""
    ls = lines_of(rel)
    depth, opened = 0, False
    for i in range(line - 1, len(ls)):
        code = re.sub(r'"(\\.|[^"\\])*"', '""', ls[i])
        code = re.sub(r"//.*", "", code)
        depth += code.count("{") - code.count("}")
        if "{" in code:
            opened = True
        if opened and depth <= 0:
            return i + 1
        if not opened and code.rstrip().endswith(";"):
            return i + 1
    sys.exit(f"unterminated block in {rel} at {line}")


def M(rel, signature, with_doc=True):
    """A method (or field) with the comment above it."""
    s = find(rel, signature)
    return (doc_start(rel, s) if with_doc else s, block_end(rel, s))


def R(rel, start_pattern, end_pattern, after=1):
    """From the first line matching start_pattern to the next line matching end_pattern."""
    s = find(rel, start_pattern, after)
    return (s, find(rel, end_pattern, s))


def HEADER(rel):
    """From the import list to the class declaration: the class Javadoc."""
    return (find(rel, r"^(import|/\*)"), find(rel, r"^public (class|final class)"))


# ------------------------------------------------------------------------------------------
# Screenshot specification
# ------------------------------------------------------------------------------------------

SHOTS = []      # (folder, filename, title, note, [(rel, (first, last)), ...])
TEXTSHOTS = []  # (folder, filename, title, note, text)
PAGES = []      # (folder, filename, html relative to folder, height)


def code(folder, name, title, note, rel, *ranges):
    SHOTS.append((folder, name, title, note, [(rel, r) for r in ranges]))


def multi(folder, name, title, note, *parts):
    SHOTS.append((folder, name, title, note, list(parts)))


E, LIST, DB = "entityClasses/LessonsLearned.java", "entityClasses/LessonsLearnedList.java", \
    "database/LessonsLearnedDatabase.java"
POLICY = "validation/LessonInputPolicy.java"

# ---- Task 3: Lessons_Learned_Javadocs ------------------------------------------------------

def tree(dirs):
    out = ["Foundations-F26/src/"]
    for d in sorted(os.listdir(SRC)):
        p = os.path.join(SRC, d)
        if not os.path.isdir(p):
            out.append(f"|-- {d}")
            continue
        out.append(f"|-- {d}/")
        if d in dirs:
            for f in sorted(os.listdir(p)):
                if f.endswith(".java"):
                    mark = "   <-- TP2" if dirs[d] is None or f in dirs[d] else ""
                    out.append(f"|     |-- {f}{mark}")
    return "\n".join(out)

TEXTSHOTS.append((LL, "3.1-entityClasses-package-source-tree.png",
    "3.1  LessonsLearned and LessonsLearnedList are in the entityClasses package",
    "Directory listing of Foundations-F26/src, taken from the submitted source.  The two TP2 "
    "classes sit next to the supplied User class; each file begins with "
    "\"package entityClasses;\".",
    tree({"entityClasses": ["LessonsLearned.java", "LessonsLearnedList.java", "package-info.java"]})))
PAGES.append((LL, "3.1-entityClasses-package-summary-javadoc.png",
    "entityClasses/package-summary.html", 760))
multi(LL, "3.1-entityClasses-package-declarations.png",
    "3.1  Package declarations of both classes",
    "Line 1 of LessonsLearned.java and of LessonsLearnedList.java declares the entityClasses package.",
    (E, (1, 8)), (LIST, (1, 8)))

code(LL, "3.2-LessonsLearned-create.png", "3.2  Create: LessonsLearned.create",
    "Validates first, refuses a lesson that is already stored, sets both dates, and stores the "
    "row through LessonsLearnedDatabase.addLesson (CS-1, CS-10, CS-11).",
    E, M(E, r"public Problem create\("))
code(LL, "3.2-LessonsLearned-read-and-update.png", "3.2  Read and Update: LessonsLearned.read, LessonsLearned.update",
    "read is static because no object exists until the row is read (CS-3).  update applies the "
    "same validation as create and keeps the old date if the write fails (CS-7).",
    E, M(E, r"public static LessonsLearned read\("), M(E, r"public Problem update\("))
code(LL, "3.2-LessonsLearned-delete-and-validate.png", "3.2  Delete: LessonsLearned.delete, and the shared validate",
    "delete returns the identifier to -1 so the object no longer claims to be stored (CS-8).  "
    "validate is the single call create and update both make (CS-10, CS-11).",
    E, M(E, r"public boolean delete\("), M(E, r"public Problem validate\("))
code(LL, "3.2-LessonsLearnedList-readAll-and-createLesson.png", "3.2  Read all and Create: LessonsLearnedList",
    "readAll is the persistent list of every lesson, newest first (CS-2).  createLesson stores a "
    "lesson and adds it to the front of the list only if the store succeeded (CS-1).",
    LIST, M(LIST, r"public static LessonsLearnedList readAll\("), M(LIST, r"public Problem createLesson\("))
code(LL, "3.2-LessonsLearnedList-updateLesson-and-deleteLesson.png", "3.2  Update and Delete: LessonsLearnedList",
    "Both write to the database first and change the in-memory list only when the write "
    "succeeded, so the list never shows what the database does not hold (CS-7, CS-8).",
    LIST, M(LIST, r"public Problem updateLesson\("), M(LIST, r"public boolean deleteLesson\("))
code(LL, "3.2-LessonsLearnedDatabase-addLesson.png", "3.2  Storage behind Create: LessonsLearnedDatabase.addLesson",
    "PreparedStatement for every value; the generated key is read back and stored in the lesson.",
    DB, M(DB, r"public int addLesson\("))

code(LL, "3.3-LessonsLearned-attributes-contributor-stories.png",
    "3.3  LessonsLearned attributes required by the Contributor stories",
    "Each attribute names the stories it comes from (Source:) and why it is stored that way.",
    E, (doc_start(E, find(E, r"private int id;")), find(E, r"private String dateUpdated;")))
code(LL, "3.3-LessonsLearned-attributes-TP3-epics.png",
    "3.3  LessonsLearned attributes stored now for the TP3 epics",
    "Review status, reviewer comment, and helpful count are stored in TP2 so the table does not "
    "change once it holds data.  The class Javadoc explains why per-user votes and reader "
    "comments will need their own tables.",
    E, (doc_start(E, find(E, r"private String reviewStatus;")), find(E, r"private int helpfulCount;")))
code(LL, "3.3-LessonsLearnedList-attribute.png", "3.3  LessonsLearnedList attribute",
    "One growable list, final so it is never swapped out under a caller.  A subset is a new "
    "LessonsLearnedList, so it can be narrowed again.",
    LIST, M(LIST, r"private final List<LessonsLearned> lessons;"))

code(LL, "3.4-LessonsLearned-class-javadoc.png", "3.4  LessonsLearned: purpose and story mapping",
    "The class Javadoc states the purpose, why the TP3 attributes are stored now, and which "
    "operation implements each Contributor story.",
    E, HEADER(E))
code(LL, "3.4-LessonsLearnedList-class-javadoc.png", "3.4  LessonsLearnedList: purpose and story mapping",
    "Why a subset is returned as a LessonsLearnedList, and which operation implements each story.",
    LIST, HEADER(LIST))
code(LL, "3.4-LessonsLearned-constructors.png", "3.4  LessonsLearned constructors",
    "One constructor for a new lesson (no id, no dates) and one for a row read from the database.",
    E, M(E, r"public LessonsLearned\(\) \{"), M(E, r"public LessonsLearned\(String title"))
code(LL, "3.4-LessonsLearnedList-subsets.png", "3.4  LessonsLearnedList subsets: search, category, contributor",
    "Each subset method returns a new list and does no database work (CS-4, CS-5, CS-6).",
    LIST, M(LIST, r"public LessonsLearnedList searchByText\("), M(LIST, r"public LessonsLearnedList filterByCategory\("))
code(LL, "3.4-LessonsLearnedList-contributor-and-categories.png", "3.4  LessonsLearnedList: my lessons and the category list",
    "filterByContributor uses the same isOwnedBy test as the ownership rule (CS-6, CS-9).",
    LIST, M(LIST, r"public LessonsLearnedList filterByContributor\("), M(LIST, r"public List<String> getCategories\("))

code(LL, "3.5-LessonsLearned-no-setters-for-owner-dates-TP3.png",
    "3.5  Why some attributes have no setter",
    "The block comment records the decision: contributor, dates, and TP3 attributes cannot be "
    "changed by a contributor's edit.",
    E, (find(E, r"^\t \* Setters\.") - 3, find(E, r"public void setKeywords")))
code(LL, "3.5-LessonsLearned-isOwnedBy-and-matches.png", "3.5  Ownership and matching rules",
    "Why the ownership comparison is exact, and why an empty search term matches nothing.",
    E, M(E, r"public boolean isOwnedBy\("), M(E, r"public boolean matches\("))
code(LL, "3.5-LessonsLearnedList-getAll-returns-copy.png", "3.5  getAll returns a copy",
    "A copy is returned so a caller cannot change the list behind this object's back.",
    LIST, M(LIST, r"public List<LessonsLearned> getAll\("))
code(LL, "3.5-LessonsLearnedDatabase-createTables.png", "3.5  Why the TP2 columns are added by ALTER TABLE",
    "CREATE TABLE IF NOT EXISTS does nothing on an existing table, so each TP2 column is added "
    "separately; an incompatible HW2 table is first renamed out of the way.",
    DB, M(DB, r"private void createTables\("))
code(LL, "3.5-LessonsLearnedDatabase-moveLegacyTableAside.png", "3.5  Keeping an HW2 table instead of failing on it",
    "Fix for the defect where saving over an HW2 table failed with \"Column DESCRIPTION not "
    "found\".  The old table is renamed, never dropped.  Tested by CRUD-13 and CRUD-14.",
    DB, M(DB, r"private boolean moveLegacyTableAside\("))
code(LL, "3.5-LessonsLearnedDatabase-getAllLessons-and-updateLesson.png",
    "3.5  Newest first by id, and what an update may not change",
    "Ordering by id rather than date text, and the columns deliberately left out of the SET clause.",
    DB, M(DB, r"public LessonsLearnedList getAllLessons\("), M(DB, r"public boolean updateLesson\("))
code(LL, "3.5-LessonInputPolicy-checkLongText-HW2-defect.png", "3.5  The HW2 length defect and its fix",
    "InputLimits.checkLength stops at 256 characters, which refused valid descriptions.  The two "
    "long fields use checkLongText instead.  Regression test: VAL-12.",
    POLICY, M(POLICY, r"public static String checkLongText\("))

# ---- Task 4: Contributor_Javadocs ----------------------------------------------------------

PAGES.append((GUI, "4.1-Contributor-packages-overview-javadoc.png", "index.html", 820))
for p in ("guiLessonsList", "guiLessonEditor", "guiLessonDetail"):
    PAGES.append((GUI, f"4.1-{p}-package-classes-javadoc.png", f"{p}/package-summary.html", 760))
TEXTSHOTS.append((GUI, "4.1-gui-packages-source-tree.png",
    "4.1  The three new GUI packages and the classes each contains",
    "Directory listing of Foundations-F26/src.  Each new package holds a Model, a View, and a "
    "Controller, named like the supplied packages (guiUserLogin, guiRole1, ...).  guiRole1 and "
    "guiRole2 gained the Lessons Learned button.",
    tree({"guiLessonsList": None, "guiLessonEditor": None, "guiLessonDetail": None,
          "guiRole1": ["ViewRole1Home.java", "ControllerRole1Home.java"],
          "guiRole2": ["ViewRole2Home.java", "ControllerRole2Home.java"]})))

VL, CL, ML = "guiLessonsList/ViewLessonsList.java", "guiLessonsList/ControllerLessonsList.java", "guiLessonsList/ModelLessonsList.java"
VE, CE, ME = "guiLessonEditor/ViewLessonEditor.java", "guiLessonEditor/ControllerLessonEditor.java", "guiLessonEditor/ModelLessonEditor.java"
VD, CD, MD = "guiLessonDetail/ViewLessonDetail.java", "guiLessonDetail/ControllerLessonDetail.java", "guiLessonDetail/ModelLessonDetail.java"

code(GUI, "4.2-guiLessonsList-View-wires-Controller.png", "4.2  MVC in guiLessonsList: the View hands every event to the Controller",
    "The View only builds widgets; each setOnAction calls a ControllerLessonsList method, as in the supplied Views.",
    VL, (find(VL, r"private ViewLessonsList\(\)") - 6, find(VL, r"button_Quit.setOnAction")))
code(GUI, "4.2-guiLessonsList-Controller-asks-Model.png", "4.2  MVC in guiLessonsList: the Controller asks the Model and shows the answer",
    "applyFilters reads the controls, lets ModelLessonsList decide what to show and what to say, and puts the result on screen.",
    CL, M(CL, r"protected static void applyFilters\("), M(CL, r"protected static void showAll\("))
code(GUI, "4.2-guiLessonEditor-View-and-Controller.png", "4.2  MVC in guiLessonEditor: View event to Controller to Model",
    "Save Lesson calls performSave, which reads the fields and calls ModelLessonEditor; a refused field is shown in a ValidationAlert.",
    CE, M(CE, r"protected static void performSave\("))
code(GUI, "4.2-guiLessonDetail-View-and-Controller.png", "4.2  MVC in guiLessonDetail: View event to Controller to Model",
    "Delete Lesson calls performDelete, which confirms with the user and then calls ModelLessonDetail.deleteLesson.",
    CD, M(CD, r"protected static void performDelete\("))

for rel, label in ((VL, "ViewLessonsList"), (CL, "ControllerLessonsList"), (ML, "ModelLessonsList"),
                   (VE, "ViewLessonEditor"), (CE, "ControllerLessonEditor"), (ME, "ModelLessonEditor"),
                   (VD, "ViewLessonDetail"), (CD, "ControllerLessonDetail"), (MD, "ModelLessonDetail")):
    code(GUI, f"4.4-{label}-class-javadoc.png", f"4.4  {label}: purpose, MVC role, and stories",
         "Class Javadoc as written in the source.", rel, HEADER(rel))

code(GUI, "4.4-ViewLessonsList-attributes.png", "4.4  ViewLessonsList attributes",
    "Every widget on the list page, with the reason for each group.",
    VL, (find(VL, r"^\tAttributes") - 2, find(VL, r"protected static Button button_Quit")))
code(GUI, "4.4-ModelLessonsList-attributes.png", "4.4  ModelLessonsList attributes",
    "The full list shared with the editor and detail pages, and the subset on screen, each with its source story.",
    ML, (find(ML, r"public static final String ALL_CATEGORIES") - 2, find(ML, r"private static LessonsLearnedList shownLessons")))
code(GUI, "4.4-ViewLessonEditor-attributes.png", "4.4  ViewLessonEditor attributes",
    "The contributor is a Label, not a TextField: it is set from the login (CS-1).  Each field has a character counter (CS-11).",
    VE, (find(VE, r"protected static Label label_PageTitle") - 3, find(VE, r"protected static Label label_Note")))
code(GUI, "4.4-ViewLessonDetail-attributes.png", "4.4  ViewLessonDetail attributes",
    "Read-only values, the TP3 review line, and the Edit and Delete buttons.",
    VD, (find(VD, r"protected static Label label_PageTitle") - 3, find(VD, r"protected static Label label_Ownership")))
code(GUI, "4.4-ViewLessonDetail-displayLessonDetail.png", "4.4  ViewLessonDetail.displayLessonDetail",
    "Edit Lesson and Delete Lesson are enabled only for the contributor; the ownership note says why (CS-9).",
    VD, M(VD, r"public static void displayLessonDetail\("))
code(GUI, "4.4-ModelLessonDetail-canChange-ownershipNote.png", "4.4  ModelLessonDetail ownership decisions",
    "The one ownership test the buttons, the editor, and the delete all use (CS-9).",
    MD, M(MD, r"public static boolean canChange\("), M(MD, r"public static String ownershipNote\("))
code(GUI, "4.4-guiRole1-Lessons-Learned-button.png", "4.4  Entry point: the Lessons Learned button on the Role1 home page",
    "The button added to the home page and the Controller method it calls.",
    "guiRole1/ViewRole1Home.java", (find("guiRole1/ViewRole1Home.java", r"GUI Area 2: Lessons Learned entry point"), find("guiRole1/ViewRole1Home.java", r"button_LessonsLearned = new Button")))
code(GUI, "4.4-guiRole1-performLessonsLearned.png", "4.4  ControllerRole1Home.performLessonsLearned",
    "Opens the list page with the current stage and user, so Back returns to this home page.",
    "guiRole1/ControllerRole1Home.java", M("guiRole1/ControllerRole1Home.java", r"protected static void performLessonsLearned\("))
code(GUI, "4.4-guiRole2-performLessonsLearned.png", "4.4  ControllerRole2Home.performLessonsLearned",
    "The same entry point for a Role2 contributor.",
    "guiRole2/ControllerRole2Home.java", M("guiRole2/ControllerRole2Home.java", r"protected static void performLessonsLearned\("))
for p in ("guiLessonsList", "guiLessonEditor", "guiLessonDetail"):
    rel = f"{p}/package-info.java"
    code(GUI, f"4.4-{p}-package-info.png", f"4.4  Purpose of the {p} package", "package-info.java", rel, (1, len(lines_of(rel)) - 1))

code(GUI, "4.5-ModelLessonsList-applyFilters.png", "4.5  Why an empty search box shows everything on the page",
    "The list-level search treats an empty term as matching nothing; the page treats it as no search.  The comment explains the difference.",
    ML, M(ML, r"public static LessonsLearnedList applyFilters\("))
code(GUI, "4.5-ModelLessonsList-statusMessage.png", "4.5  Empty results are explained in words",
    "Which filter produced an empty result is named in the message (CS-2, CS-4, CS-5, CS-6).",
    ML, M(ML, r"public static String statusMessage\("))
code(GUI, "4.5-ModelLessonEditor-saveNewLesson.png", "4.5  The contributor is never typed",
    "In HW2 the contributor was a text field; the comment records why it now comes from the login (CS-1, CS-9).",
    ME, M(ME, r"public static Problem saveNewLesson\("))
code(GUI, "4.5-ModelLessonEditor-saveChanges.png", "4.5  Changes are applied to a copy",
    "Ownership is checked again, an untouched form is not saved, and the original object is not changed until the database has accepted the change.",
    ME, M(ME, r"public static Problem saveChanges\("))
code(GUI, "4.5-ModelLessonEditor-tidyKeywords.png", "4.5  Keywords are tidied before validation",
    "Duplicates are dropped, but an empty entry is kept so validation can report it.",
    ME, M(ME, r"public static String tidyKeywords\("))
code(GUI, "4.5-ModelLessonDetail-deleteLesson.png", "4.5  The ownership check is repeated in the Model",
    "The Delete button is already disabled for others; the Model checks again so no other route can bypass the rule.",
    MD, M(MD, r"public static Problem deleteLesson\("))
code(GUI, "4.5-ControllerLessonsList-reloadLessons-fillCategories.png", "4.5  Reading from the database, not memory",
    "Re-reading shows lessons from earlier runs (CS-12); the category choice survives a return from the editor.",
    CL, M(CL, r"protected static void reloadLessons\("), M(CL, r"protected static void fillCategories\("))
code(GUI, "4.5-FoundationsMain-lessonsDatabase.png", "4.5  Opening the lessons table at start up",
    "Why the second connection to the same H2 file does not trigger the \"already being used\" alert.",
    "applicationMain/FoundationsMain.java", R("applicationMain/FoundationsMain.java", r"public void start\(Stage theStage\)", r"System.exit\(0\);"))

# ---- Task 7: ContributorTests_Javadocs -----------------------------------------------------

SUITES = [("testingAutomation/LessonValidationTestingAutomation.java", "Validation"),
          ("testingAutomation/LessonsCrudTestingAutomation.java", "CRUD"),
          ("testingAutomation/LessonsListTestingAutomation.java", "List"),
          ("testingAutomation/LessonsModelTestingAutomation.java", "Model"),
          ("testingAutomation/LessonsManualTestProcedures.java", "Manual")]

TEST_SHOTS = []   # (test ids, filename) for the requirements map
for rel, suite in SUITES:
    cls = os.path.basename(rel)[:-5]
    code(TST, f"7.4-{cls}-class-javadoc.png", f"7.4  {cls}: purpose, requirements, how to read the output",
         "Class Javadoc as written in the source.", rel, HEADER(rel))
    ls = lines_of(rel)
    for i, line in enumerate(ls, start=1):
        m = re.match(r"\tprivate static void ((val|crud|list|mod|man)\d+[A-Za-z0-9]*)\(", line)
        if not m:
            continue
        name = m.group(1)
        prefix = m.group(2).upper()
        nums = re.findall(r"\d+", re.match(r"[a-z]+((\d+)(And\d+)?)", name).group(1))
        ids = [f"{prefix}-{int(n):02d}" for n in nums]
        readable = re.sub(r"^[a-z]+\d+(And\d+)?", "", name)
        fname = f"7.4-{'-'.join(ids)}-{readable}.png"
        code(TST, fname, f"7.4  {' and '.join(ids)}  ({cls}.{name})",
             "The Javadoc gives the requirement, how the test works, and the condition for a Pass; "
             "the code below is what runs.", rel, (doc_start(rel, i), block_end(rel, i)))
        TEST_SHOTS.append((ids, fname, cls))
RUN = "testingAutomation/TP2TestRunner.java"
code(TST, "7.4-TP2TestRunner-class-javadoc.png", "7.4  TP2TestRunner: what each suite covers",
     "Class Javadoc as written in the source.", RUN, HEADER(RUN))
code(TST, "7.4-TP2TestRunner-main.png", "7.4  TP2TestRunner.main",
     "Runs the four suites and adds up their counts.", RUN, M(RUN, r"public static void main\("))
code(TST, "7.4-check-helper-and-refusedWith.png", "7.4  How a result line is produced",
     "check prints the Pass or Failure line; refusedWith makes a negative test pass only for the expected refusal.",
     SUITES[0][0], M(SUITES[0][0], r"private static boolean refusedWith\("), M(SUITES[0][0], r"private static void check\("))


# ------------------------------------------------------------------------------------------
# Rendering
# ------------------------------------------------------------------------------------------

PREAMBLE = r"""\documentclass[multi=snippet,border=14pt]{standalone}
\usepackage[T1]{fontenc}
\usepackage[scaled=0.92]{beramono}
\usepackage[scaled]{helvet}
\usepackage{xcolor,listings}
\definecolor{kw}{HTML}{1F4E9A}\definecolor{cm}{HTML}{2E6B4F}\definecolor{st}{HTML}{9A3412}
\definecolor{num}{HTML}{8A8A8A}\definecolor{rule}{HTML}{C9CED6}\definecolor{bg}{HTML}{F7F8FA}
\lstset{language=Java, basicstyle=\ttfamily\footnotesize, keywordstyle=\color{kw}\bfseries,
  commentstyle=\color{cm}, stringstyle=\color{st}, numbers=left, numberstyle=\tiny\color{num},
  numbersep=8pt, tabsize=4, columns=fixed, keepspaces=true, showstringspaces=false,
  backgroundcolor=\color{bg}, frame=single, rulecolor=\color{rule}, framesep=6pt,
  xleftmargin=22pt, framexleftmargin=22pt, breaklines=true, breakindent=24pt,
  literate={©}{{\textcopyright}}1 {–}{{--}}1 {—}{{---}}1 {•}{{\textbullet}}1}
\newcommand{\shothead}[3]{{\sffamily\bfseries\large #1}\par\vspace{3pt}
  {\sffamily\small\color{num} #2}\par\vspace{4pt}{\sffamily\small #3}\par\vspace{8pt}}
\hyphenpenalty=10000\exhyphenpenalty=10000
\begin{document}
"""


def esc(s):
    rep = {"\\": r"\textbackslash{}", "&": r"\&", "%": r"\%", "$": r"\$", "#": r"\#",
           "_": r"\_", "{": r"\{", "}": r"\}", "~": r"\textasciitilde{}", "^": r"\^{}",
           '"': "''"}
    return "".join(rep.get(c, c) for c in s)


def render(work):
    pages, names = [], []
    for folder, name, title, note, parts in SHOTS:
        total = sum(b - a + 1 for _, (a, b) in parts)
        if total > MAX_LINES:
            print(f"warning: {name} has {total} lines", file=sys.stderr)
        where = "; ".join(f"Foundations-F26/src/{f}, lines {a}-{b}" for f, (a, b) in parts)
        body = [r"\begin{snippet}\begin{minipage}{19.5cm}",
                r"\shothead{%s}{%s}{%s}" % (esc(title), esc(where), esc(note))]
        for f, (a, b) in parts:
            body.append(r"\lstinputlisting[firstline=%d,lastline=%d,firstnumber=%d]{%s}"
                        % (a, b, a, os.path.join(SRC, f)))
        body.append(r"\end{minipage}\end{snippet}")
        pages.append("\n".join(body)); names.append((folder, name))
    for folder, name, title, note, text in TEXTSHOTS:
        tf = os.path.join(work, name + ".txt")
        open(tf, "w").write(text + "\n")
        pages.append("\n".join([r"\begin{snippet}\begin{minipage}{19.5cm}",
            r"\shothead{%s}{%s}{%s}" % (esc(title), "Generated from the submitted source tree", esc(note)),
            r"\lstinputlisting[language={},numbers=none,xleftmargin=0pt,framexleftmargin=0pt]{%s}" % tf,
            r"\end{minipage}\end{snippet}"]))
        names.append((folder, name))
    tex = os.path.join(work, "shots.tex")
    open(tex, "w").write(PREAMBLE + "\n\n".join(pages) + "\n\\end{document}\n")
    subprocess.run(["pdflatex", "-interaction=nonstopmode", "-halt-on-error", "shots.tex"],
                   cwd=work, check=True, stdout=subprocess.DEVNULL)
    subprocess.run(["pdftoppm", "-r", "130", "-png", "shots.pdf", "p"], cwd=work, check=True)
    files = sorted(f for f in os.listdir(work) if f.startswith("p-") and f.endswith(".png"))
    assert len(files) == len(names), (len(files), len(names))
    for f, (folder, name) in zip(files, names):
        os.makedirs(os.path.join(TP2, folder), exist_ok=True)
        shutil.move(os.path.join(work, f), os.path.join(TP2, folder, name))


def javadoc_pages():
    for folder, name, html, height in PAGES:
        url = "file://" + os.path.join(TP2, folder, html)
        out = os.path.join(TP2, folder, name)
        subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars",
                        f"--window-size=1280,{height}", f"--screenshot={out}", url],
                       check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


def output_shots(work):
    """Runs TP2TestRunner and typesets its real console output."""
    binp = os.path.join(work, "bin")
    srcs = [os.path.join(d, f) for d, _, fs in os.walk(SRC) for f in fs if f.endswith(".java")]
    subprocess.run(["javac", "-nowarn", "--module-path", FX, "--add-modules", "javafx.controls",
                    "-cp", H2, "-d", binp] + srcs, check=True, stderr=subprocess.DEVNULL)
    out = subprocess.run(["java", "--module-path", FX + ":" + binp, "--add-modules",
                          "javafx.controls", "-cp", H2, "-m",
                          "FoundationsF26/testingAutomation.TP2TestRunner"],
                         capture_output=True, text=True, cwd=work, check=True).stdout
    blocks = re.split(r"(?=-{60}\n  Lesson)", out)
    head, suites = blocks[0], blocks[1:]
    summary_at = suites[-1].index("\n####")
    last, summary = suites[-1][:summary_at], suites[-1][summary_at:]
    suites[-1] = last
    chunks = [("Validation suite", head + suites[0]), ("CRUD suite", suites[1]),
              ("List suite", suites[2]), ("Model suite", suites[3]), ("Combined summary", summary)]
    for i, (label, text) in enumerate(chunks, start=1):
        TEXTSHOTS.append((TST, f"7.4-TP2TestRunner-output-{i}-{label.split()[0]}.png",
            f"Console output of TP2TestRunner: {label}",
            "Captured from a real run of the submitted code.  Every line names the test id and requirement.",
            text.strip("\n")))
    return out


def main():
    work = tempfile.mkdtemp(prefix="tp2shots")
    try:
        output_shots(work)
        render(work)
        javadoc_pages()
    finally:
        shutil.rmtree(work)
    with open(os.path.join(HERE, "test_shots.tsv"), "w") as f:
        for ids, fname, cls in TEST_SHOTS:
            f.write(f"{','.join(ids)}\t{fname}\t{cls}\n")
    print(f"{len(SHOTS) + len(TEXTSHOTS)} typeset screenshots, {len(PAGES)} Javadoc page screenshots")


if __name__ == "__main__":
    main()
