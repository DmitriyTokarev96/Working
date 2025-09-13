# Быстрый старт User Service

## 🚀 Запуск за 5 минут

### 1. Установка зависимостей

**Windows:**
```cmd
# Скачайте и установите:
# - Java 11+: https://adoptium.net/
# - Maven: https://maven.apache.org/download.cgi
# - PostgreSQL: https://www.postgresql.org/download/windows/
```

**Linux/macOS:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-11-jdk maven postgresql

# macOS
brew install openjdk@11 maven postgresql
```

### 2. Настройка базы данных

```bash
# Подключитесь к PostgreSQL
psql -U postgres

# Создайте базу данных
CREATE DATABASE user_service_db;

# Выйдите
\q
```

### 3. Настройка приложения

Отредактируйте `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.password">ваш_пароль_postgres</property>
```

### 4. Запуск

```bash
# Сборка и запуск
mvn clean compile exec:java -Dexec.mainClass="com.example.userservice.UserServiceApplication"

# Или используйте скрипт
# Windows: run.bat
# Linux/macOS: ./run.sh
```

## 🎯 Первые шаги

1. **Создайте пользователя:**
   - Выберите опцию 1
   - Введите имя: `John Doe`
   - Введите email: `john@example.com`
   - Введите возраст: `30`

2. **Найдите пользователя:**
   - Выберите опцию 2
   - Введите ID: `1`

3. **Посмотрите всех пользователей:**
   - Выберите опцию 4

## 🔧 Решение проблем

### Maven не найден
```bash
# Добавьте Maven в PATH
export PATH=$PATH:/path/to/maven/bin
```

### Ошибка подключения к БД
```bash
# Проверьте, что PostgreSQL запущен
sudo systemctl start postgresql  # Linux
brew services start postgresql   # macOS
```

### Ошибка компиляции
```bash
# Очистите и пересоберите
mvn clean compile
```

## 📚 Дополнительная информация

- **Полная документация**: README.md
- **Примеры использования**: EXAMPLES.md
- **Инструкция по установке**: INSTALL.md
- **Проверка готовности**: CHECKLIST.md

## 🆘 Поддержка

При возникновении проблем:
1. Проверьте логи в папке `logs/`
2. Убедитесь, что все зависимости установлены
3. Проверьте настройки подключения к БД
4. Обратитесь к документации
