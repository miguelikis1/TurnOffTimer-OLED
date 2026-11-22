@echo off
cd /d "%~dp0"
echo ========================================================
echo   Compilando y Subiendo Sleep Timer v1.0.0 a GitHub...
echo ========================================================

echo [1/4] Compilando APK Release con la nueva funcionalidad...
call gradlew.bat assembleRelease --no-daemon
if errorlevel 1 (
    echo Error durante la compilacion del APK.
    pause
    exit /b 1
)

echo [2/4] Actualizando APK en carpeta release...
if not exist "release" mkdir "release"
copy /y "app\build\outputs\apk\release\app-release.apk" "release\SleepTimer-v1.0.0.apk" >nul

echo [3/4] Registrando commit y tags de version 1.0.0...
git add .
git commit -m "feat: exact custom time picker (hours and minutes) + CI/CD workflow fix"
git tag -d v1.0.0 2>nul
git tag -d 1.0.0 2>nul
git tag -a v1.0.0 -m "Release v1.0.0"
git tag -a 1.0.0 -m "Release v1.0.0"

echo [4/4] Subiendo a GitHub y publicando Release...
git push origin main --force
git push origin --tags --force

echo.
echo ========================================================
echo   Completado con exito!
echo   El codigo y la Release de GitHub ya estan en camino.
echo ========================================================
pause
