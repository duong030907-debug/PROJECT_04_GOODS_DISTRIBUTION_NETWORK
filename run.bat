@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javac.exe" set "PATH=%JAVA_HOME%\bin;%PATH%"
    )
)
where javac >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Khong tim thay javac. Hay cai JDK 17+ va them vao PATH hoac dat JAVA_HOME.
    exit /b 1
)

if not exist bin mkdir bin
if exist sources.txt del sources.txt
(for /r src %%f in (*.java) do (
    set "sourcePath=%%f"
    echo "!sourcePath:\=/!"
)) > sources.txt

javac --release 17 -encoding UTF-8 -d bin @sources.txt
if errorlevel 1 (
    echo [ERROR] Bien dich that bai.
    del sources.txt
    exit /b 1
)
del sources.txt

java -cp bin Main %*
exit /b %errorlevel%
