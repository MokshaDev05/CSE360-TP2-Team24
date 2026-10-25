#!/bin/zsh
# Rebuilds every generated TP2 artifact from the source: Javadoc, screenshots, and the PDFs.
set -e
HERE=${0:A:h}
TP2=$HERE/../TP2
"$HERE/build_javadoc.sh"
python3 "$HERE/screenshots/make_screenshots.py"
cd "$HERE/latex"
python3 make_plans.py
python3 make_map.py
for f in team_norms aicoaching requirements_map plan_2026-10-*.tex(N); do
	latexmk -pdf -interaction=nonstopmode -halt-on-error -quiet "${f%.tex}.tex" >/dev/null
done
cp team_norms.pdf "$TP2/TP2 Team Norms.pdf"
cp aicoaching.pdf "$TP2/AICoaching.pdf"
cp requirements_map.pdf "$TP2/Requirements to Tests Map.pdf"
for f in plan_2026-10-*.pdf; do
	d=${f#plan_}; cp "$f" "$TP2/TP2 Planning and Tracking/Plan and Status ${d}"
done
echo "All artifacts rebuilt."
