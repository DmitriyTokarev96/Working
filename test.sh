#!/bin/bash

echo "Запуск тестов User Service..."
echo

echo "Проверка Maven..."
if ! command -v mvn &> /dev/null; then
    echo "Ошибка: Maven не найден. Убедитесь, что Maven установлен и добавлен в PATH."
    exit 1
fi

mvn --version
echo

echo "Компиляция проекта..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "Ошибка при компиляции проекта."
    exit 1
fi

echo
echo "Запуск всех тестов..."
mvn test
if [ $? -ne 0 ]; then
    echo "Ошибка при выполнении тестов."
    exit 1
fi

echo
echo "Все тесты выполнены успешно!"



