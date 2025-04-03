@echo off
setlocal enabledelayedexpansion

REM 記錄檢查過的檔案
set "checkedFiles="

REM 編譯 Java 檔案
echo Compiling all Java files...
mvn compile

REM 遍歷所有 Java 檔案，執行 STRANGE
echo Running STRANGE on all source files...

for /R src\main\java\edu\uob %%F in (*.java) do (
    set "file=%%F"
    echo Checking !file! ...
    call mvnw exec:java@strange -Dexec.args="!file!"
    set "checkedFiles=!checkedFiles! !file!"
)

echo ----------------------------------------
echo Summary of Checked Files:
for %%F in (!checkedFiles!) do (
    echo   - %%F
)

echo All checks complete!