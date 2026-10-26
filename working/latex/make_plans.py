#!/usr/bin/env python3
"""Writes the six Plan and Status .tex files from one table of tasks, so that a task's owner,
planned date, and status cannot disagree from one standup to the next."""
import os

HERE = os.path.dirname(os.path.abspath(__file__))

# Standups: date, weekday, presenter of the Status Summary screencast.
STANDUPS = [("2026-10-12", "Monday", "Arush Ugal"), ("2026-10-14", "Wednesday", "Karan Patel"),
            ("2026-10-16", "Friday", "Anirudh B"), ("2026-10-19", "Monday", "Moksha Dev"),
            ("2026-10-22", "Thursday", "Pranjal Ganvir"), ("2026-10-24", "Saturday", "Arush Ugal")]

# Task id: (task, owner, planned done date, {standup: status}).  A standup that is missing
# from the map carries the last status forward; T = To Do, P = In Progress, D = Done.
TASKS = {
    "REQ": ("Contributor requirements list CS-1 to CS-12", "Arush", "10-13", {"10-12": "P", "10-14": "D"}),
    "NORM": ("TP2 Team Norms (tracked changes)", "Arush", "10-13", {"10-12": "P", "10-14": "D"}),
    "LL": ("LessonsLearned entity class", "Arush", "10-16", {"10-12": "T", "10-14": "P", "10-16": "D"}),
    "LLL": ("LessonsLearnedList entity class", "Anirudh", "10-18", {"10-12": "P", "10-19": "D"}),
    "DB": ("LessonsLearnedDatabase (table, CRUD)", "Karan", "10-16", {"10-12": "P", "10-16": "D"}),
    "POL": ("LessonInputPolicy (HW2 length fix, keywords)", "Pranjal", "10-16", {"10-12": "P", "10-16": "D"}),
    "AIC": ("AICoaching.pdf: one field per member", "All five", "10-19", {"10-12": "P", "10-19": "D"}),
    "MAIN": ("FoundationsMain: open the lessons table", "Arush", "10-16", {"10-12": "T", "10-16": "D"}),
    "GLIST": ("guiLessonsList (Model, View, Controller)", "Karan", "10-20", {"10-12": "T", "10-16": "P", "10-19": "D"}),
    "GEDIT": ("guiLessonEditor (Model, View, Controller)", "Moksha", "10-20", {"10-12": "P", "10-19": "D"}),
    "GDET": ("guiLessonDetail (Model, View, Controller)", "Moksha", "10-21", {"10-12": "T", "10-19": "P", "10-22": "D"}),
    "ROLE": ("Lessons Learned button on Role1 and Role2 home", "Anirudh", "10-19", {"10-12": "T", "10-16": "P", "10-19": "D"}),
    "BUILD": ("Clean compile of the whole project", "Arush", "10-20", {"10-12": "T", "10-16": "P", "10-22": "D"}),
    "VAL": ("Validation suite VAL-01 to VAL-25", "Pranjal", "10-21", {"10-12": "T", "10-14": "P", "10-22": "D"}),
    "CRUD": ("CRUD suite CRUD-01 to CRUD-12", "Anirudh", "10-21", {"10-12": "T", "10-19": "P", "10-22": "D"}),
    "LIST": ("List suite LIST-01 to LIST-10", "Anirudh", "10-22", {"10-12": "T", "10-19": "P", "10-22": "D"}),
    "MOD": ("GUI Model suite MOD-01 to MOD-16", "Karan", "10-22", {"10-12": "T", "10-19": "P", "10-22": "D"}),
    "RUN": ("TP2TestRunner (all suites, one run)", "Pranjal", "10-22", {"10-12": "T", "10-19": "P", "10-22": "D"}),
    "MAP": ("Requirements to Tests Map", "Pranjal", "10-23", {"10-12": "T", "10-19": "P", "10-24": "D"}),
    "J3": ("Lessons\\_Learned\\_Javadocs and screenshots", "Arush", "10-23", {"10-12": "T", "10-22": "P", "10-24": "D"}),
    "J4": ("Contributor\\_Javadocs and screenshots", "Karan, Moksha", "10-23", {"10-12": "T", "10-19": "P", "10-24": "D"}),
    "J7": ("ContributorTests\\_Javadocs and screenshots", "Anirudh, Pranjal", "10-23", {"10-12": "T", "10-22": "P", "10-24": "D"}),
    "SC3": ("LessonsLearnedScreencast", "Arush, Anirudh", "10-25", {"10-12": "T", "10-24": "P"}),
    "SC4": ("PhaseTwoGUIScreencast", "Karan, Moksha", "10-25", {"10-12": "T", "10-24": "P"}),
    "SC7": ("TestingScreencast", "Pranjal, Anirudh", "10-25", {"10-12": "T", "10-24": "P"}),
    "SUM": ("Six Status Summary screencasts", "Presenter of each", "10-25", {"10-12": "T", "10-24": "P"}),
    "ZIP": ("Checklist, ZIP, noon safety upload", "Arush", "10-26", {"10-12": "T", "10-24": "P"}),
    "PEER": ("Peer evaluation and Canvas receipt", "All five", "10-26", {"10-12": "T", "10-24": "P"}),
}

# What each member reported at each standup: (task ids, since last, next, blocking).
DETAILS = {
 "10-12": {
  "Arush Ugal": (["REQ", "NORM"],
    "First TP2 standup. Read the TP2 assignment and rubric; split the Contributor user stories into twelve testable requirements, CS-1 to CS-12 (draft); started the tracked-changes revision of the TP1 norms.",
    "Finish CS-1 to CS-12 and the Team Norms for review; start the LessonsLearned attributes.",
    "The attribute list depends on the team agreeing CS-1 to CS-12, so review is needed by Oct 13."),
  "Karan Patel": (["DB"],
    "Reviewed the HW2 database class and listed the columns TP2 needs: keywords, the two dates, and the three TP3 review attributes.",
    "Write the table creation with ALTER TABLE ADD COLUMN IF NOT EXISTS for each new column, and an in-memory connection for tests.",
    "None."),
  "Anirudh B": (["LLL", "AIC"],
    "Reviewed the HW2 list class and mapped every list operation the GUI will need: read all, search, category filter, contributor filter, categories, and the three persistent changes. Took the Keywords field for AICoaching.",
    "Turn the mapping into method signatures; research keyword extraction tools.",
    "Coding the list waits for the LessonsLearned constructors (Arush, due Oct 16)."),
  "Pranjal Ganvir": (["POL", "AIC"],
    "Reviewed the HW2 LessonInputPolicy and found that descriptions over 256 characters are refused although the limit is 2000. Took the Category field for AICoaching and will assemble the PDF.",
    "Write a failing test for the 256-character defect, then fix it; add keyword validation.",
    "None."),
  "Moksha Dev": (["GEDIT", "AIC"],
    "Reviewed guiRole1, guiNewAccount, and guiUserUpdate to follow the Foundations MVC pattern. Took the Resolution field for AICoaching.",
    "Sketch the editor and detail pages within the 800 by 600 window.",
    "View classes wait for the entity class (Oct 16)."),
 },
 "10-14": {
  "Arush Ugal": (["LL"],
    "CS-1 to CS-12 agreed by the team and Team Norms finished (both Oct 13). LessonsLearned: twelve attributes and the three constructors written and pushed.",
    "Write create, read, update, delete, validate, isOwnedBy, and matches.",
    "None."),
  "Karan Patel": (["DB"],
    "CREATE TABLE and the six ALTER TABLE ADD COLUMN IF NOT EXISTS statements written; connectToDatabase(url) overload added for in-memory test databases.",
    "addLesson with generated keys, getAllLessons ordered by id descending, getLessonById, updateLesson, deleteLesson, deleteAllLessons, getNumberOfLessons.",
    "None."),
  "Anirudh B": (["LLL"],
    "Method signatures for LessonsLearnedList written against Arush's pushed constructors; Keywords research narrowed to KeyBERT.",
    "Implement readAll, createLesson, and deleteLesson, then the subsets.",
    "None."),
  "Pranjal Ganvir": (["POL", "VAL"],
    "Wrote VAL-12 (300-character description); it failed as expected. Root cause: InputLimits.checkLength stops at 256. Added LessonInputPolicy.checkLongText for the two long fields; VAL-12 now passes. Keyword rules drafted.",
    "Finish keyword validation; write VAL cases for every field's required and length rules.",
    "None."),
  "Moksha Dev": (["GEDIT", "AIC"],
    "Paper sketches of the editor and detail pages done; both fit 800 by 600 with the Foundations absolute-position Pane layout. Resolution field concept drafted.",
    "Start ViewLessonEditor once LessonsLearned is finished.",
    "None."),
 },
 "10-16": {
  "Arush Ugal": (["BUILD"],
    "LessonsLearned finished (CRUD, validation, ownership, search). FoundationsMain now opens the lessons table after the user tables; both connections work together.",
    "Clean compile against the JavaFX module path and the H2 jar; then start the Task 3 Javadoc.",
    "None."),
  "Karan Patel": (["GLIST", "AIC"],
    "LessonsLearnedDatabase finished, all seven methods. Started ModelLessonsList and the ViewLessonsList layout. Title field concept drafted for AICoaching.",
    "Finish guiLessonsList: search, category filter, Only my lessons, New Lesson, Open, Back, Quit.",
    "None."),
  "Anirudh B": (["LLL", "ROLE"],
    "LessonsLearnedList: readAll, createLesson, deleteLesson done; searchByText and filterByCategory in progress.",
    "Finish the list class; add the Lessons Learned button to the Role1 and Role2 home pages.",
    "None."),
  "Pranjal Ganvir": (["VAL", "AIC"],
    "LessonInputPolicy finished; the first 12 VAL cases pass. AICoaching: Description, Resolution, and Category sections drafted with mockups.",
    "Collect the Title and Keywords sections; extend the VAL suite.",
    "Waiting for the Title and Keywords drafts (Karan, Anirudh), due Oct 18."),
  "Moksha Dev": (["GEDIT"],
    "ViewLessonEditor laid out with a character counter under every field; contributor shown as a label, not a field.",
    "ControllerLessonEditor and ModelLessonEditor: save, cancel, ownership check.",
    "None."),
 },
 "10-19": {
  "Arush Ugal": (["BUILD"],
    "Reviewed the nine new GUI classes. Fixed two import paths in guiLessonsList and the opens lines in module-info.java.",
    "Finish the clean compile; start the Task 3 Javadoc.",
    "None."),
  "Karan Patel": (["MOD", "J4"],
    "guiLessonsList finished (Oct 19). AICoaching Title section delivered. Started the GUI Model suite and the Task 4 package screenshots.",
    "MOD cases for the list, editor, and detail Models.",
    "None."),
  "Anirudh B": (["CRUD", "LIST"],
    "LessonsLearnedList finished (Oct 18); Lessons Learned button on both home pages (Oct 19). CRUD suite first run: 10 of 12 pass. CRUD-02 and CRUD-06 failed because create and update wrote the row without checking validate's result; Arush fixed both on the same day.",
    "Rerun CRUD; write the List suite.",
    "None."),
  "Pranjal Ganvir": (["VAL", "RUN", "MAP"],
    "AICoaching.pdf assembled from the five sections (Oct 19). VAL suite at 18 cases, all passing.",
    "Finish VAL to 25 cases; write TP2TestRunner; start the Requirements to Tests Map.",
    "None."),
  "Moksha Dev": (["GDET", "J4"],
    "guiLessonEditor finished (Oct 19). ViewLessonDetail laid out; Edit and Delete disabled for non-contributors.",
    "ControllerLessonDetail with delete confirmation, and ModelLessonDetail.",
    "None."),
 },
 "10-22": {
  "Arush Ugal": (["J3"],
    "Whole project compiles clean: 67 source files, 0 errors (Oct 20). Started the Task 3 Javadoc.",
    "Finish Lessons_Learned_Javadocs and the 3.1, 3.3, 3.4, and 3.5 screenshots.",
    "None."),
  "Karan Patel": (["J4"],
    "GUI Model suite finished: 16 cases. First run: MOD-04 failed; statusMessage said nothing matched when only Only my lessons was ticked. Fixed the branch order; 16 of 16 pass. Task 4 Javadoc generated.",
    "Remaining Task 4 screenshots (4.2, 4.4, 4.5).",
    "None."),
  "Anirudh B": (["J7"],
    "CRUD suite 12 of 12 (Oct 21); List suite 10 of 10 (Oct 22). Started the Javadoc for the test classes.",
    "Task 7.4 screenshots of every test's code.",
    "None."),
  "Pranjal Ganvir": (["MAP", "J7"],
    "VAL suite finished at 25 cases (Oct 21). TP2TestRunner runs all four suites: 63 of 63 pass. Map drafted with every requirement's positive and negative tests.",
    "Cross-check every test id in the map against the suites; finish the map PDF.",
    "Map needs the final screenshot names from Anirudh and Karan."),
  "Moksha Dev": (["J4"],
    "guiLessonDetail finished (Oct 21). 4.2 MVC screenshot for the list page captured.",
    "4.4 and 4.5 screenshots for the editor and detail packages.",
    "None."),
 },
 "10-24": {
  "Arush Ugal": (["SC3", "SUM", "ZIP", "PEER"],
    "Lessons_Learned_Javadocs finished with its screenshots (Oct 23).",
    "Record LessonsLearnedScreencast with Anirudh; produce the Oct 12 and Oct 24 Status Summaries; run the checklist and make the noon upload on Oct 26; remind every member to submit the peer evaluation and the Canvas receipt.",
    "None."),
  "Karan Patel": (["SC4", "SUM"],
    "Contributor_Javadocs finished with Moksha (Oct 23).",
    "Record PhaseTwoGUIScreencast with Moksha; produce the Oct 14 Status Summary.",
    "None."),
  "Anirudh B": (["SC3", "SC7", "SUM"],
    "ContributorTests_Javadocs finished with Pranjal (Oct 23).",
    "Present in LessonsLearnedScreencast and TestingScreencast; produce the Oct 16 Status Summary.",
    "Presents in two main screencasts, so his parts are kept short (see the table below)."),
  "Pranjal Ganvir": (["SC7", "SUM"],
    "Requirements to Tests Map finished (Oct 23). Prepared the live TP2TestRunner run for the TestingScreencast.",
    "Record TestingScreencast with Anirudh; produce the Oct 22 Status Summary.",
    "None."),
  "Moksha Dev": (["SC4", "SUM"],
    "All Contributor_Javadocs screenshots finished (Oct 23).",
    "Record PhaseTwoGUIScreencast with Karan; produce the Oct 19 Status Summary.",
    "None."),
 },
}

ORDER = list(TASKS)
STATUS = {"T": r"\ToDo", "P": r"\Doing", "D": r"\Done"}


def status_at(task, day):
    s = None
    for d, _, _ in STANDUPS:
        k = d[5:]
        if k in TASKS[task][3]:
            s = TASKS[task][3][k]
        if k == day:
            return s


def tex_escape(s):
    return s.replace("&", r"\&").replace("%", r"\%").replace("#", r"\#").replace("_", r"\_")


def write(date, weekday, presenter):
    day = date[5:]
    rows = []
    for t in ORDER:
        st = status_at(t, day)
        if st is None:
            continue
        task, owner, planned = TASKS[t][:3]
        rows.append(f"{task} & {owner} & {STATUS[st]} & 2026-{planned} \\\\")
    counts = {k: sum(1 for t in ORDER if status_at(t, day) == k) for k in "TPD"}
    det = []
    for member, (ids, since, nxt, block) in DETAILS[day].items():
        for i in ids:
            assert status_at(i, day) == "P", (day, member, i)
        names = "; ".join(TASKS[i][0] for i in ids)
        det.append(r"""\member{%s}{%s}
\item[Since last standup] %s
\item[Next standup] %s
\item[Blocking] %s
\end{description}""" % (member, names, tex_escape(since), tex_escape(nxt), tex_escape(block)))
    # Every In Progress task must be reported by a member who owns it.
    reported = {i for ids, *_ in DETAILS[day].values() for i in ids}
    for t in ORDER:
        if status_at(t, day) == "P":
            assert t in reported, (day, t)

    extra = ""
    if day == "10-24":
        extra = r"""
\section*{Presenting time plan (Team Norm 11)}
\begin{tabularx}{\linewidth}{@{}X r r r@{}}
\toprule
\textbf{Member} & \textbf{Main screencasts} & \textbf{Summaries} & \textbf{Total (min)} \\
\midrule
Arush Ugal & LessonsLearned 3.5 & 2 $\times$ 3.5 & 10.5 \\
Anirudh B & LessonsLearned 2.5, Testing 5.5 & 1 $\times$ 3.5 & 11.5 \\
Pranjal Ganvir & Testing 6.5 & 1 $\times$ 3.5 & 10 \\
Karan Patel & PhaseTwoGUI 4.5 & 1 $\times$ 3.5 & 8 \\
Moksha Dev & PhaseTwoGUI 4.5 & 1 $\times$ 3.5 & 8 \\
\bottomrule
\end{tabularx}
"""
    body = r"""\documentclass[10pt]{article}
\usepackage{tp2style}
\hypersetup{pdftitle={Plan and Status %(date)s},pdfauthor={CSE 360 Team 24}}
\fancyfoot[L]{\sffamily\scriptsize\color{muted}Plan and Status %(date)s \textperiodcentered{} Team 24}
\newcommand{\member}[2]{\par\smallskip{\sffamily\bfseries #1}\quad{\sffamily\small\color{muted}#2}
\begin{description}[leftmargin=9.5em,labelwidth=9em,labelsep=0.5em,style=sameline,font=\sffamily\small\color{muted},itemsep=0pt]}
\begin{document}
\doctitle{Plan and Status %(date)s}{\team \\ Standup: %(weekday)s, %(date)s \textperiodcentered{} Status Summary presenter: %(presenter)s}

\section*{Task board}
{\small\color{muted}%(d)d done \textperiodcentered{} %(p)d in progress \textperiodcentered{} %(t)d to do.
Planned done dates are the dates agreed when each task was added.}

\begin{small}
\begin{longtable}{@{}p{0.47\linewidth} p{0.19\linewidth} p{0.13\linewidth} l@{}}
\toprule
\textbf{Task} & \textbf{Owner} & \textbf{Status} & \textbf{Planned done} \\
\midrule
\endhead
%(rows)s
\bottomrule
\end{longtable}
\end{small}

\section*{In-progress work, by member}
%(details)s
%(extra)s
\end{document}
""" % {"date": date, "weekday": weekday, "presenter": presenter, "rows": "\n".join(rows),
       "details": "\n".join(det), "extra": extra, "d": counts["D"], "p": counts["P"], "t": counts["T"]}
    with open(os.path.join(HERE, f"plan_{date}.tex"), "w") as f:
        f.write(body)


if __name__ == "__main__":
    for args in STANDUPS:
        write(*args)
    print("wrote", len(STANDUPS), "plan files")
