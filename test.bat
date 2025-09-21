@echo off
echo Запуск тестов User Service...
echo.

echo Проверка Maven...
mvn --version
if %errorlevel% neq 0 (
    echo Ошибка: Maven не найден. Убедитесь, что Maven установлен и добавлен в PATH.
    pause
    exit /b 1
)

echo.
echo Компиляция проекта...
mvn clean compile
if %errorlevel% neq 0 (
    echo Ошибка при компиляции проекта.
    pause
    exit /b 1
)

echo.
echo Запуск всех тестов...
mvn test
if %errorlevel% neq 0 (
    echo Ошибка при выполнении тестов.
    pause
    exit /b 1
)

echo.
echo Все тесты выполнены успешно!
pause
