@echo off
echo Starting User Service...
echo.

echo Checking Java version...
java -version
echo.

echo Building application...
call mvn clean compile
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Starting Spring Boot application...
echo Application will be available at: http://localhost:8080
echo API documentation: http://localhost:8080/api/users
echo.
echo Press Ctrl+C to stop the application
echo.

call mvn spring-boot:run

pause
