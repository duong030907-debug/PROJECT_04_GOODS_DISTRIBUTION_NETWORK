@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

where javac >nul 2>nul
if not errorlevel 1 goto java_ready

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" set "PATH=%JAVA_HOME%\bin;%PATH%"
)
where javac >nul 2>nul
if not errorlevel 1 goto java_ready

rem Thu tim JDK trong cac thu muc cai dat pho bien tren Windows.
if exist "%ProgramFiles%\Android\Android Studio\jbr\bin\javac.exe" (
    set "PATH=%ProgramFiles%\Android\Android Studio\jbr\bin;%PATH%"
)
for /d %%d in (
    "%USERPROFILE%\Java\graalvm-jdk-*"
    "%USERPROFILE%\Java\jdk-*"
    "%ProgramFiles%\Java\jdk-*"
    "%ProgramFiles%\Eclipse Adoptium\jdk-*"
) do (
    if exist "%%~d\bin\javac.exe" set "PATH=%%~d\bin;!PATH!"
)

where javac >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Khong tim thay javac. Hay cai JDK 17+ va them vao PATH hoac dat JAVA_HOME.
    exit /b 1
)

:java_ready
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
