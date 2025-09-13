@echo off
echo Compiling User Service Application...
echo.

REM Create directories
if not exist "target\classes" mkdir target\classes
if not exist "target\lib" mkdir target\lib

REM Download dependencies manually (simplified approach)
echo Downloading dependencies...
echo Note: This is a simplified compilation script.
echo For full functionality, install Maven and use: mvn clean compile
echo.

REM Check if Java is available
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 11 or higher and add it to your PATH
    pause
    exit /b 1
)

echo Compiling source files...
echo Note: This requires Hibernate and PostgreSQL JAR files to be in target/lib/
echo.

REM Compile main source files
javac -cp "target/lib/*" -d target/classes src/main/java/com/example/userservice/entity/User.java
if %ERRORLEVEL% NEQ 0 (
    echo Error compiling User.java
    pause
    exit /b 1
)

javac -cp "target/classes;target/lib/*" -d target/classes src/main/java/com/example/userservice/util/HibernateUtil.java
if %ERRORLEVEL% NEQ 0 (
    echo Error compiling HibernateUtil.java
    pause
    exit /b 1
)

javac -cp "target/classes;target/lib/*" -d target/classes src/main/java/com/example/userservice/dao/UserDao.java
if %ERRORLEVEL% NEQ 0 (
    echo Error compiling UserDao.java
    pause
    exit /b 1
)

javac -cp "target/classes;target/lib/*" -d target/classes src/main/java/com/example/userservice/service/UserService.java
if %ERRORLEVEL% NEQ 0 (
    echo Error compiling UserService.java
    pause
    exit /b 1
)

javac -cp "target/classes;target/lib/*" -d target/classes src/main/java/com/example/userservice/console/UserConsole.java
if %ERRORLEVEL% NEQ 0 (
    echo Error compiling UserConsole.java
    pause
    exit /b 1
)

javac -cp "target/classes;target/lib/*" -d target/classes src/main/java/com/example/userservice/UserServiceApplication.java
if %ERRORLEVEL% NEQ 0 (
    echo Error compiling UserServiceApplication.java
    pause
    exit /b 1
)

echo Compilation completed successfully!
echo.
echo To run the application, you need to:
echo 1. Install Maven: https://maven.apache.org/download.cgi
echo 2. Run: mvn clean compile exec:java -Dexec.mainClass="com.example.userservice.UserServiceApplication"
echo.
pause
