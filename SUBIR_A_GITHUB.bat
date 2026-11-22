@echo off
cd /d "%~dp0"
echo ========================================================
echo   Actualizando y Subiendo Sleep Timer a GitHub...
echo ========================================================
git add .
git commit -m "fix: workflow line endings and release publication"
git tag -a v1.0.0 -m "Release v1.0.0" 2>nul
git tag -a 1.0.0 -m "Release v1.0.0" 2>nul
git push origin main --tags
echo.
echo ========================================================
echo   Listo! GitHub Actions ya esta compilando tu APK.
echo ========================================================
pause
