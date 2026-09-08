cd SIH-2026
mkdir -p assets
# copy README.md and assets/logo.png (from the files I shared) into this folder
git add README.md assets/logo.png
git commit -m "Add detailed project README"
git push origin main
cd /home/claude/SIH-2026 && which cloc || pip install --break-system-packages -q pygount 2>&1 | tail -5; echo done
cd /home/claude/SIH-2026 && find . -type f -not -path "./.git/*" -not -name "*.apk" -not -name "*.jar" -not -name "*.png" -not -name "*.jpg" | sed 's/.*\.//' | sort | uniq -c | sort -rn
cd /home/claude/SIH-2026 && for ext in kt xml kts properties toml json pro; do
  count=$(find . -type f -name "*.$ext" -not -path "./.git/*" | xargs cat 2>/dev/null | wc -l)
  echo "$ext: $count lines"
done
