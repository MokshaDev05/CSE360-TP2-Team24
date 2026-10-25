#!/bin/zsh
# Regenerates the three Javadoc folders in TP2 from Foundations-F26/src.
# -private is used so the attributes, and the source and rationale written on each, appear in
# the HTML (Tasks 3.3, 3.4, 4.4) and so every private test method's Javadoc appears (Task 7.3).
set -e
FX=${FX:-$HOME/Projects/ASU-Foundations-F26/tools/javafx-sdk-25.0.4/lib}
H2=${H2:-$HOME/Projects/ASU-Foundations-F26/tools/h2-2.3.232.jar}
TP2=${0:A:h}/../TP2
SRC=$TP2/Foundations-F26/src
CP="$FX/javafx.base.jar:$FX/javafx.graphics.jar:$FX/javafx.controls.jar:$H2"

# javadoc is run in class-path mode on a copy without module-info.java, so that a subset of
# the packages can be documented on its own.
TMP=$(mktemp -d)
cp -R "$SRC/" "$TMP/src"
rm "$TMP/src/module-info.java"
cd "$TMP/src"

gen() {   # gen <folder> <window title> <file...>
	local out=$TP2/$1; local title=$2; shift 2
	find "$out" -mindepth 1 -not -name '*.png' -delete 2>/dev/null || true
	mkdir -p "$out"
	javadoc -quiet -private -author -version -Xdoclint:none \
		-encoding UTF-8 -cp "$CP" -sourcepath . -d "$out" \
		-windowtitle "$title" -doctitle "$title" "$@"
}

gen Lessons_Learned_Javadocs "Lessons Learned Entity Classes - TP2" \
	entityClasses/package-info.java entityClasses/LessonsLearned.java \
	entityClasses/LessonsLearnedList.java database/LessonsLearnedDatabase.java \
	validation/LessonInputPolicy.java validation/InputLimits.java

gen Contributor_Javadocs "Contributor GUI Packages - TP2" \
	guiLessonsList/*.java guiLessonEditor/*.java guiLessonDetail/*.java \
	guiRole1/*.java guiRole2/*.java guiTools/*.java applicationMain/FoundationsMain.java

gen ContributorTests_Javadocs "Contributor Tests - TP2" \
	testingAutomation/package-info.java testingAutomation/TP2TestRunner.java \
	testingAutomation/LessonValidationTestingAutomation.java \
	testingAutomation/LessonsCrudTestingAutomation.java \
	testingAutomation/LessonsListTestingAutomation.java \
	testingAutomation/LessonsModelTestingAutomation.java \
	testingAutomation/LessonsManualTestProcedures.java

cd /
rm -rf "$TMP"
echo "Javadoc regenerated."
