# Project Notes

## Folder Organization

This master folder was organized on **October 25, 2026** to prepare TP2 for final submission.

### What Was Done

1. **Cleaned up duplicate folders** — Consolidated TP2, TP2_FINAL, and working folders
2. **Organized deliverables** — All submission files moved to `SUBMIT_THIS/TP2/`
3. **Separated scripts** — Recording scripts and notes moved to `SCRIPTS_AND_WORKING/`
4. **Removed stale files** — Deleted .DS_Store, .Rhistory, and duplicate copies
5. **Created documentation** — Added README.md and QUICK_START.md guides

### File Locations

**For Canvas Submission:**
- Path: `/Users/arushugal/Desktop/TP2_MASTER/SUBMIT_THIS/TP2/`
- Contains: All deliverables from Tasks 1-9 (except videos)

**Scripts and References:**
- Path: `/Users/arushugal/Desktop/TP2_MASTER/SCRIPTS_AND_WORKING/`
- Contains: Video scripts, draft documents, project notes

**Original Folders (NOT for submission):**
- `/Users/arushugal/Desktop/TP2/` — Old working copy
- `/Users/arushugal/Desktop/TP2_FINAL/` — Previous submission attempt
- `/Users/arushugal/Desktop/TP2 Working Folder (not part of submission)/` — Draft materials

### What Still Needs Work

**9 videos must be recorded** before submission:

Status summaries (Task 8):
- 2026-10-12 standup recap
- 2026-10-14 standup recap
- 2026-10-16 standup recap
- 2026-10-19 standup recap
- 2026-10-22 standup recap
- 2026-10-24 standup recap

Main screencasts (Task 9):
- LessonsLearned feature demo (Arush + Anirudh)
- Contributor GUI walkthrough (Moksha + Karan)
- Testing demonstration (Pranjal + Anirudh)

## Verification Performed

### Code Verification
```
Compiled: 68 class files, 0 errors
Tests: 63 passed, 0 failed
```

### Javadoc Verification
- Lessons_Learned_Javadocs: Complete with 4 screenshots
- Contributor_Javadocs: Complete with 17 screenshots  
- ContributorTests_Javadocs: Complete with 9 screenshots
- All internal links working (5,563 links checked)

### Document Verification
- Team Norms: Has visible tracked changes (blue underline, red strikethrough)
- AI Coaching: Five input fields with mockups and citations
- Requirements Map: All 63 tests documented with HTML references

### Planning Verification
- Six Plan and Status PDFs present (Oct 12, 14, 16, 19, 22, 24)
- Each PDF has 6-12 "In Progress" tasks
- Every team member appears in at least one In Progress row

## Rubric Compliance

| Task | Status | Notes |
|------|--------|-------|
| 1 | ✅ Ready | Folder structure matches requirements |
| 2 | ✅ Ready | Track changes visible in PDF |
| 3 | ✅ Ready | Entity classes implemented with Javadoc |
| 4 | ✅ Ready | GUI packages implemented with Javadoc |
| 5 | ✅ Ready | Five AI coaching fields documented |
| 6 | ✅ Ready | Requirements map completed |
| 7 | ✅ Ready | Test suites with Javadoc |
| 8 | ⚠️ Videos needed | PDFs done, need 6 summary videos |
| 9 | ⚠️ Videos needed | Need 3 main screencasts |
| 10 | ⏳ After videos | Zip and submit |
| 11 | ⏳ After submission | Peer evaluation |
| 12 | ⏳ After submission | Submission confirmation |

## Technical Details

### JavaFX Version
SDK: javafx-sdk-25.0.4 (located at ~/javafx-sdk-25.0.4)

### H2 Database
Version: h2-2.4.240.jar (located at ~/h2/bin/)

### Build Command
```bash
javac --module-path ~/javafx-sdk-25.0.4/lib --add-modules javafx.controls \
  -cp "$HOME/h2/bin/h2-2.4.240.jar" -d bin $(find src -name "*.java")
```

### Test Command
```bash
java -cp bin testingAutomation.TP2TestRunner
```

## Team Responsibilities

**Arush Ugal:**
- LessonsLearned classes (Task 3)
- Team norms updates (Task 2)
- Kanban board hosting (Task 8)
- Create final zip file (Task 10)
- Present in LessonsLearnedScreencast

**Anirudh B:**
- LessonsLearned classes support (Task 3)
- Test iteration support (Task 6)
- Test documentation support (Task 7)
- Present in LessonsLearnedScreencast and TestingScreencast

**Moksha Dev:**
- Contributor GUI packages (Task 4)
- Present in PhaseTwoGUIScreencast

**Karan Patel:**
- Contributor GUI packages (Task 4)
- LessonsLearned support (Task 3)
- Present in PhaseTwoGUIScreencast

**Pranjal Ganvir:**
- AI coaching document (Task 5 lead)
- Test suites (Task 7 lead)
- Requirements testing (Task 6)
- Present in TestingScreencast

## Important Deadlines

- **Noon safety submission:** Monday, October 27, 12:00 PM
- **Hard deadline:** Monday, October 27, 11:59 PM
- **Peer evaluation:** Due same day as submission

## Contact and Resources

- **Rubric:** Team Project Phase 2 (TP2).pdf in ~/Downloads/
- **Canvas:** Assignment page shows Oct 28 availability window (ignore, use Oct 27 deadline)
- **TP1 Reference:** Previous submission can be used as formatting guide

---

**Notes for Next AI Agent:**

All deliverables are complete and verified except videos. The folder structure is clean and ready for submission once videos are recorded. Scripts for all videos are in `SCRIPTS_AND_WORKING/scripts/` directory. Follow the QUICK_START.md guide for final submission steps.
