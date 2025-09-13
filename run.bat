@echo off
echo Starting User Service Application...
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

REM Check if Java is available
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 11 or higher and add it to your PATH
    pause
    exit /b 1
)

echo Compiling and running the application...
echo.

REM Compile and run the application
mvn clean compile exec:java -Dexec.mainClass="com.example.userservice.UserServiceApplication"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error occurred while running the application
    pause
    exit /b 1
)

echo.
echo Application finished
pause
