# Инструкция по установке и запуску User Service

## Требования

- Java 11 или выше
- Maven 3.6 или выше
- PostgreSQL 12 или выше

## Установка Maven

### Windows

1. Скачайте Maven с официального сайта: https://maven.apache.org/download.cgi
2. Распакуйте архив в папку, например: `C:\Program Files\Apache\maven`
3. Добавьте переменную окружения `MAVEN_HOME` со значением пути к Maven
4. Добавьте `%MAVEN_HOME%\bin` в переменную `PATH`
5. Проверьте установку: `mvn --version`

### Linux/macOS

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install maven

# macOS с Homebrew
brew install maven

# Проверка установки
mvn --version
```

## Установка PostgreSQL

### Windows

1. Скачайте PostgreSQL с официального сайта: https://www.postgresql.org/download/windows/
2. Запустите установщик и следуйте инструкциям
3. Запомните пароль для пользователя postgres
4. Убедитесь, что служба PostgreSQL запущена

### Linux

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# Запуск службы
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### macOS

```bash
# С Homebrew
brew install postgresql
brew services start postgresql
```

## Настройка базы данных

1. Подключитесь к PostgreSQL:
   ```bash
   psql -U postgres
   ```

2. Создайте базу данных:
   ```sql
   CREATE DATABASE user_service_db;
   ```

3. (Опционально) Создайте пользователя:
   ```sql
   CREATE USER user_service_user WITH PASSWORD 'password';
   GRANT ALL PRIVILEGES ON DATABASE user_service_db TO user_service_user;
   ```

4. Выйдите из psql:
   ```sql
   \q
   ```

## Настройка приложения

1. Отредактируйте файл `src/main/resources/hibernate.cfg.xml`:
   ```xml
   <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/user_service_db</property>
   <property name="hibernate.connection.username">postgres</property>
   <property name="hibernate.connection.password">ваш_пароль</property>
   ```

## Сборка и запуск

### С помощью Maven (рекомендуется)

```bash
# Сборка проекта
mvn clean compile

# Запуск приложения
mvn exec:java -Dexec.mainClass="com.example.userservice.UserServiceApplication"

# Или создание JAR файла
mvn clean package
java -jar target/user-service-1.0.0.jar
```

### С помощью скриптов

**Windows:**
```cmd
run.bat
```

**Linux/macOS:**
```bash
chmod +x run.sh
./run.sh
```

## Тестирование

```bash
# Запуск тестов
mvn test

# Запуск тестов с подробным выводом
mvn test -X
```

## Возможные проблемы

### Ошибка "Maven не найден"
- Убедитесь, что Maven установлен и добавлен в PATH
- Перезапустите командную строку после изменения PATH

### Ошибка подключения к базе данных
- Проверьте, что PostgreSQL запущен
- Убедитесь, что настройки подключения в `hibernate.cfg.xml` корректны
- Проверьте, что база данных `user_service_db` существует

### Ошибка "Java не найдена"
- Убедитесь, что Java 11+ установлена
- Проверьте переменную JAVA_HOME

### Ошибки компиляции
- Убедитесь, что используется Java 11 или выше
- Проверьте, что все зависимости загружены: `mvn dependency:resolve`

## Структура проекта

```
user-service/
├── src/main/java/          # Исходный код
├── src/main/resources/     # Ресурсы (конфигурация, логи)
├── src/test/java/          # Тесты
├── target/                 # Скомпилированные файлы
├── pom.xml                 # Maven конфигурация
├── run.bat                 # Скрипт запуска для Windows
├── run.sh                  # Скрипт запуска для Linux/macOS
└── README.md               # Документация
```

## Логи

Логи приложения сохраняются в папке `logs/`:
- `user-service.log` - общие логи
- `user-service-error.log` - только ошибки

## Поддержка

При возникновении проблем:
1. Проверьте логи в папке `logs/`
2. Убедитесь, что все требования выполнены
3. Проверьте настройки подключения к базе данных
