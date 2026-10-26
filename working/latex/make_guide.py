#!/usr/bin/env python3
"""Writes screencast_guide.tex: for each main screencast, the deliverable requirements it must
show and the exact HTML pages and screenshots that satisfy each one, read from the TP2 folders
so the names on screen always match the submission.  Not part of the submission."""
import os, re
import make_plans as P

HERE = os.path.dirname(os.path.abspath(__file__))
TP2 = os.path.normpath(os.path.join(HERE, "..", "..", "TP2"))


def spoken_date(d):
    months = {"10": "October"}
    return f"{months[d[5:7]]} {int(d[8:])}, {d[:4]}"


def write_status_scripts():
    """One spoken script per standup, built from the same data as the Plan and Status PDFs."""
    out = []
    days = [d for d, _, _ in P.STANDUPS]
    for n, (date, weekday, presenter) in enumerate(P.STANDUPS):
        k = date[5:]
        prev = days[n - 1][5:] if n else None
        nxt = days[n + 1] if n + 1 < len(days) else None
        counts = {c: sum(1 for t in P.ORDER if P.status_at(t, k) == c) for c in "TPD"}
        finished = [t for t in P.ORDER if P.status_at(t, k) == "D" and (prev is None or P.status_at(t, prev) != "D")]
        out.append(r"\clearpage")
        out.append(r"\section*{Script: Status Summary %s}" % date)
        out.append(r"\scriptmeta{Presenter: %s. Target 3.5 minutes (allowed 3--5), about 40 seconds per "
                   r"member; pause on each slide for a few seconds before speaking. Do not show your face. Show the slide each member showed at the standup, and "
                   r"the Plan and Status %s PDF. File name: \texttt{Status Summary %s.mp4}, in "
                   r"\texttt{TP2 Planning and Tracking}.}" % (presenter, date, date))
        out.append(r"\speaker{%s}{about 3.5 minutes}" % presenter)
        out.append(r"\act{Open \texttt{Plan and Status %s.pdf}, page 1, the task board.}" % date)
        intro = (f"This is the status summary for the Team 24 standup on {weekday}, {spoken_date(date)}. "
                 f"I'm {presenter.split()[0]}. The board for this standup shows {counts['D']} tasks done, "
                 f"{counts['P']} in progress, and {counts['T']} still to do. ")
        if prev is None:
            intro += "This was our first TP2 standup, so nothing had been completed before it. "
        elif finished:
            names = [f"{P.TASKS[t][0].replace(chr(92) + '_', '_')} ({P.TASKS[t][1]}, planned for October {int(P.TASKS[t][2][3:])})" for t in finished]
            intro += ("Since the last standup, these tasks were completed and marked Done: "
                      + "; ".join(names) + ". ")
        intro += "I'll go through each member's in-progress work as they showed it."
        out.append(r"\say{%s}" % P.tex_escape(intro))
        for member, (ids, since, nx, block) in P.DETAILS[k].items():
            first = member.split()[0]
            names = [P.TASKS[i][0].replace("\\_", "_") for i in ids]
            tasks = " and ".join(names) if len(names) < 3 else ", ".join(names[:-1]) + ", and " + names[-1]
            blocking = "Nothing is blocking this work." if block.strip() == "None." else "Blocking or concern: " + block
            board = "; ".join(f"{n} is In Progress, planned to be done by October {int(P.TASKS[i][2][3:])}"
                              for n, i in zip(names, ids))
            text = (f"{first} is working on {tasks}. On the board, {board}. Since the last standup: {since} "
                    f"For the next standup: {nx} {blocking}")
            out.append(r"\act{Show %s's standup slide, then %s's block under ``In-progress work, by member''.}" % (first, first))
            out.append(r"\say{%s}" % P.tex_escape(text))
        close = (f"The next standup is on {spoken_date(nxt)}." if nxt else
                 "This was the last TP2 standup before submission.") + " That's the summary."
        out.append(r"\act{Return to the task board.}")
        out.append(r"\say{%s}" % close)
    open(os.path.join(HERE, "screencast_scripts_status.tex"), "w").write("\n".join(out) + "\n")


def pngs(folder, prefix):
    return sorted(f for f in os.listdir(os.path.join(TP2, folder)) if f.startswith(prefix) and f.endswith(".png"))


def html(folder, *pkgs):
    out = []
    for p in pkgs:
        d = os.path.join(TP2, folder, p)
        out += [f"{p}/{f}" for f in sorted(os.listdir(d)) if f.endswith(".html") and f != "package-tree.html"]
    return out


def esc(s):
    return s.replace("_", r"\_").replace("#", r"\#")


def files(items):
    return r"\newline ".join(r"\texttt{%s}" % esc(i) for i in items) or "--"


def table(rows):
    body = "\n".join(r"%s & %s & {\scriptsize %s} \\ \addlinespace[3pt]" % (a, b, files(c)) for a, b, c in rows)
    return (r"\setlength{\tabcolsep}{4pt}\begin{longtable}{@{}>{\raggedright\arraybackslash}p{0.07\linewidth} "
            r">{\raggedright\arraybackslash}p{0.33\linewidth} >{\raggedright\arraybackslash}p{0.54\linewidth}@{}}"
            "\n\\toprule\\textbf{Item} & \\textbf{Requirement} & \\textbf{Show these files} \\\\\\midrule\\endhead\n"
            + body + "\n\\bottomrule\\end{longtable}")


LL, GUI, TST = "Lessons_Learned_Javadocs", "Contributor_Javadocs", "ContributorTests_Javadocs"
ll = [
 ("3.1", "Both classes are in the entityClasses package (screenshot).", pngs(LL, "3.1") + ["entityClasses/package-summary.html"]),
 ("3.2", "Both classes fully implement CRUD, as shown by the Javadoc.", pngs(LL, "3.2") + ["entityClasses/LessonsLearned.html", "entityClasses/LessonsLearnedList.html", "database/LessonsLearnedDatabase.html"]),
 ("3.3", "The attributes cover the Contributor stories, the TP3 epics, and other requirements, explained in the Javadoc.", pngs(LL, "3.3") + ["entityClasses/LessonsLearned.html (Field Summary)"]),
 ("3.4", "Purpose, attributes, source and rationale of each, operations, and how they enable the stories.", pngs(LL, "3.4")),
 ("3.5", "Internal comments showing which lines satisfy which requirement or why code is not obvious.", pngs(LL, "3.5") + ["validation/LessonInputPolicy.html"]),
]
gui = [
 ("4.1", "New GUI packages named like the Foundations packages; the classes each contains (screenshots).", pngs(GUI, "4.1")),
 ("4.2", "The packages use MVC in line with the Foundations GUI packages.", pngs(GUI, "4.2")),
 ("4.3", "The packages cover every Contributor story (CS-1 to CS-12: see the package-info pages).", [f"{p}/package-summary.html" for p in ("guiLessonsList", "guiLessonEditor", "guiLessonDetail")]),
 ("4.4", "Purpose of packages and classes, attributes with source and rationale, operations.", pngs(GUI, "4.4") + html(GUI, "guiLessonsList", "guiLessonEditor", "guiLessonDetail")),
 ("4.5", "Internal comments showing which lines satisfy which requirement or why code is not obvious.", pngs(GUI, "4.5")),
]
tst = [
 ("7.1", "A list of all TP2 requirements, each with HTML files and screenshots.", ["../Requirements to Tests Map.pdf (pages 1-2)"]),
 ("7.2", "Two or more tests per requirement, positive and negative, documented in the Javadoc.", ["../Requirements to Tests Map.pdf (Requirements and their tests)"] + html(TST, "testingAutomation")),
 ("7.3", "Each test's Javadoc names the requirement, how the code satisfies it, how to read the output.", ["Method Detail section of each suite page, e.g. testingAutomation/LessonsCrudTestingAutomation.html#crud11And12PersistAcrossReopen()"]),
 ("7.4", "Each test's commented source code (one screenshot per test method).", pngs(TST, "7.4-")),
]

tex = r"""\documentclass[10pt]{article}
\usepackage{tp2style}
\fancyfoot[L]{\sffamily\scriptsize\color{muted}Screencast Guide (working document, not submitted)}
\newcommand{\actfile}[1]{{\ttfamily\hyphenchar\font=`\-\relax #1}}
\newcommand{\act}[1]{\par\smallskip{\raggedright\sffamily\small\color{accent}\let\texttt\actfile[#1]\par}}
\newcommand{\say}[1]{\par\vspace{1pt}\leftskip=1.2em #1\par\leftskip=0pt}
\newcommand{\speaker}[2]{\par\medskip{\sffamily\bfseries\large #1}\quad{\sffamily\small\color{muted}#2}\par\vspace{2pt}{\color{rulegray}\hrule}\par}
\newcommand{\scriptmeta}[1]{{\small\color{muted}#1}\par}
\begin{document}
\doctitle{Screencast Guide}{\team \\ Working document for recording; not part of the submission. Generated from the TP2 folders on October 25, 2026.}

\section*{Every screencast}
MP4 or MOV, under 100 MB, recorded to the Zoom cloud, readable text (zoom the browser to 125\%%).
Two presenters each; times follow the presenting plan in Plan and Status 2026-10-24. Start each
main screencast by showing its requirements table below; it is the ``list of deliverable
requirements'' the rubric asks for (9.1.1, 9.2.1, 9.3.1).

\begin{tabularx}{\linewidth}{@{}l l l X@{}}
\toprule
\textbf{File} & \textbf{Length} & \textbf{Presenters} & \textbf{Shows} \\
\midrule
LessonsLearnedScreencast & 5--10 min (aim 6) & Arush 3.5, Anirudh 2.5 & Lessons\_Learned\_Javadocs \\
PhaseTwoGUIScreencast & 5--10 min (aim 9) & Karan 4.5, Moksha 4.5 & Contributor\_Javadocs, the running GUI \\
TestingScreencast & 8--15 min (aim 12) & Pranjal 6.5, Anirudh 5.5 & ContributorTests\_Javadocs, a live run of every test \\
Status Summary 2026-MM-DD (6) & 3--5 min & see each Plan and Status & that standup's slides and PDF \\
\bottomrule
\end{tabularx}

\section*{LessonsLearnedScreencast: Task 3 items}
%(ll)s

\section*{PhaseTwoGUIScreencast: Task 4 items}
%(gui)s

\section*{TestingScreencast: Task 7 items}
Run \texttt{TP2TestRunner} on screen after showing the Javadoc; scroll through each suite and
say, for two or three tests per suite, which requirement the line proves and why a Pass means
it holds. Then carry out MAN-01 to MAN-07 in the running application with
\texttt{LessonsManualTestProcedures} open beside it.

%(tst)s

\input{screencast_scripts_main}
\input{screencast_scripts_status}
\end{document}
""" % {"ll": table(ll), "gui": table(gui), "tst": table(tst)}
open(os.path.join(HERE, "screencast_guide.tex"), "w").write(tex)
write_status_scripts()
print("guide written")
