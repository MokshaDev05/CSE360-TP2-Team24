# TP2 Standup Summary Scripts — Team 24

Course: CSE 360, 2026 Fall C
Team 24: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev

---

## How to use these scripts

Six summaries, one per standup date. Every member presents in at least one.
All six can be recorded in ONE session (TA approved). Each runs 3–5 minutes.

**Before recording each one:**
1. Open the matching `Plan and Status 2026-MM-DD.pdf` from `TP2 Planning and Tracking/`
2. Open Zoom, screen share ON, record to **Cloud**
3. The recorder reads their script while scrolling through the PDF
4. Save as `Status Summary 2026-MM-DD.mp4` and place in `TP2 Planning and Tracking/`

| # | Date | Day | Recorder | File |
|---|---|---|---|---|
| 1 | 2026-10-12 | Mon | Arush Ugal | Status Summary 2026-10-12.mp4 |
| 2 | 2026-10-14 | Wed | Karan Patel | Status Summary 2026-10-14.mp4 |
| 3 | 2026-10-16 | Fri | Anirudh B | Status Summary 2026-10-16.mp4 |
| 4 | 2026-10-19 | Mon | Moksha Dev | Status Summary 2026-10-19.mp4 |
| 5 | 2026-10-22 | Thu | Pranjal Ganvir | Status Summary 2026-10-22.mp4 |
| 6 | 2026-10-24 | Fri | Arush Ugal | Status Summary 2026-10-24.mp4 |

All dates are within the TA-approved 12–24 October window. Each pair is at least two days apart.

---

## Script 1 — Arush Ugal — 12 October

This is Arush, recording the Plan and Status Summary for Team 24, standupdate the twelfth of October. All five were there.

I am showing the Plan and Status PDF for this date on screen now.

On the board today, two tasks are In Progress. I have started the Contributor User Stories document and the TP2 Team Norms PDF. Both are expected done by October thirteenth.

All other tasks are still To Do. We reviewed the rubric together and split the work: Arush owns user stories and the entity class, Karan owns the database and the list GUI, Anirudh owns the list entity class and the home page buttons, Pranjal owns validation and AICoaching, and Moksha owns the editor and detail GUI pages.

The single concern from this standup is that the entity class has to be finished before Anirudh can start the list class. We agreed Arush targets the entity class by October fifteenth so there is no block.

Nothing else is blocking progress. The team understands the deliverables and the schedule.

---

## Script 2 — Karan Patel — 14 October

This is Karan, recording the Plan and Status Summary for the fourteenth of October. All five were there.

I am showing the Plan and Status PDF for October fourteenth.

The Team Norms and User Stories documents are now Done. Arush's LessonsLearned entity class is In Progress and on track for the sixteenth. My LessonsLearnedDatabase class is In Progress: the CREATE TABLE and six ALTER TABLE ADD COLUMN IF NOT EXISTS statements are done, and the CRUD methods are half written.

Pranjal has fixed the 256-character validation bug in LessonInputPolicy and is drafting AICoaching fields three through five.

Moksha has finished the paper sketches for all three lesson GUI pages and is ready to start the View classes as soon as the entity class lands.

No blockers. The team expects the entity class and the database class to both be done by the end of the sixteenth, clearing the way for the GUI work.

---

## Script 3 — Anirudh B — 16 October

This is Anirudh, recording the Plan and Status Summary for the sixteenth of October. All five were there.

I am showing the Plan and Status PDF for October sixteenth.

Three major things are Done as of today. The LessonsLearned entity class is complete with twelve attributes, three constructors, full CRUD methods, ownership checking, and text search. The LessonsLearnedDatabase is complete with all seven database operations and the safe migration that adds new columns without breaking the HW2 database. LessonInputPolicy is complete with six field validators and the corrected long-text ceiling.

I have started LessonsLearnedList. The readAll, createLesson, and deleteLesson methods are working. I have also added the Lessons Learned button to the Role1 and Role2 home pages.

Karan has started guiLessonsList. Moksha has guiLessonEditor nearly done.

No blockers.

---

## Script 4 — Moksha Dev — 19 October

This is Moksha, recording the Plan and Status Summary for the nineteenth of October. All five were there.

I am showing the Plan and Status PDF for October nineteenth.

Since the sixteenth: guiLessonsList is complete, guiLessonEditor is complete, and the role home page buttons are done. The Foundations project compiles cleanly: sixty-seven Java files, zero errors.

AICoaching is done. Five fields, five owners, GUI mockups, requirements, tool citations, all in one clean PDF.

I finished guiLessonDetail's View class today. The Controller is in progress. Pranjal's validation test suite has eighteen cases passing with seven more to write.

No blockers. The GUI work is effectively complete. The next two standups focus on tests, Javadoc, and recording.

---

## Script 5 — Pranjal Ganvir — 22 October

This is Pranjal, recording the Plan and Status Summary for the twenty-second of October. All five were there.

I am showing the Plan and Status PDF for October twenty-second.

The test results are the headline: TP2TestRunner runs all sixty-three test cases and every one of them passes. Twenty-five validation tests, twelve CRUD tests, ten list tests, sixteen GUI model tests. Zero failures. I will show the runner output in the Testing screencast.

All three Javadoc folders are now in progress. Arush is generating Lessons-Learned-Javadocs, Karan and Moksha are generating Contributor-Javadocs, and Anirudh and I are generating ContributorTests-Javadocs. Source code screenshots are being produced alongside each folder.

The Requirements to Tests Map PDF is almost done. I will finish it by tomorrow.

No blockers. We are on schedule to record the screencasts on the twenty-fifth.

---

## Script 6 — Arush Ugal — 24 October

This is Arush, recording the Plan and Status Summary for the twenty-fourth of October. All five were there.

I am showing the Plan and Status PDF for October twenty-fourth.

Everything except the screencasts is Done. The TP2 submission folder contains: the Team Norms PDF with track changes, the AICoaching PDF, the Requirements to Tests Map, all three Javadoc folders with HTML files and screenshots, all six Plan and Status PDFs, and the Foundations-F26 source project with sixty-three passing tests.

The five In Progress items on the board are the three main screencasts, the six status summary screencasts including this one, and the final ZIP and submission.

We are recording all videos in one session today and tomorrow, as the TA approved. Every member will present. Arush and Anirudh are presenting in LessonsLearnedScreencast, Moksha and Karan in PhaseTwoGUIScreencast, Pranjal and Anirudh in TestingScreencast.

After recording, the checklist is: verify each video is under 100 MB, delete dot-DS-Store files, rebuild the bin folder, zip the TP2 folder, upload a safety copy, then submit. Each of us completes the peer evaluation before the deadline.

Nothing is blocking us.
