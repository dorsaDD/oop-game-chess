@echo off
setlocal
cd /d "%~dp0"

if not exist out mkdir out

echo Compiling...
javac -d out -sourcepath src src\main\main.java src\main\*.java src\main\rules\*.java src\main\hazards\*.java src\piece\*.java
if errorlevel 1 (
    echo Compile failed. Make sure Java JDK is installed and on PATH.
    pause
    exit /b 1
)

echo Starting Dorsa Chess Game...
java -cp out main.main
