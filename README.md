# User Service

Консольное приложение для управления пользователями с использованием Hibernate и PostgreSQL.

## 🚀 Возможности

- ✅ CRUD операции с пользователями
- ✅ Валидация данных с Lombok @NonNull аннотациями
- ✅ Оптимизированные SQL запросы (один запрос для UPDATE)
- ✅ Комплексное тестирование (JUnit 5, Mockito, Testcontainers)
- ✅ Изолированные тесты
- ✅ Логирование с Logback

## 🛠 Технологии

- **Java 11**
- **Hibernate 6.2.7**
- **PostgreSQL**
- **Lombok**
- **JUnit 5**
- **Mockito**
- **Testcontainers**
- **AssertJ**
- **Maven**

## 📋 Требования

- Java 11 или выше
- Maven 3.6+
- PostgreSQL (для production)
- Docker (для тестов с Testcontainers)

## 🏃‍♂️ Быстрый старт

### 1. Клонирование репозитория
```bash
git clone <repository-url>
cd user-service
```

### 2. Настройка базы данных
Создайте базу данных PostgreSQL:
```sql
CREATE DATABASE user_service;
CREATE USER user_service WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE user_service TO user_service;
```

### 3. Настройка конфигурации
Отредактируйте `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/user_service</property>
<property name="hibernate.connection.username">user_service</property>
<property name="hibernate.connection.password">password</property>
```

### 4. Компиляция и запуск
```bash
# Windows
compile.bat
run.bat

# Linux/Mac
chmod +x compile.sh run.sh
./compile.sh
./run.sh

# Или через Maven
mvn clean compile
mvn exec:java
```

## 🧪 Тестирование

### Запуск всех тестов
```bash
# Windows
test.bat

# Linux/Mac
./test.sh

# Или через Maven
mvn test
```

### Типы тестов
- **Юнит-тесты**: `*UnitTest` - быстрые тесты с Mockito
- **Интеграционные тесты**: `*IntegrationTest` - тесты с реальной БД

Подробнее в [TESTING.md](TESTING.md)

## 📁 Структура проекта

```
user-service/
├── src/main/java/com/example/userservice/
│   ├── console/          # Консольный интерфейс
│   ├── dao/              # Слой доступа к данным
│   ├── entity/           # JPA сущности
│   ├── service/          # Бизнес-логика
│   ├── exception/        # Исключения
│   └── util/             # Утилиты
├── src/main/resources/   # Конфигурация
├── src/test/            # Тесты
├── database/            # SQL скрипты
└── *.bat, *.sh         # Скрипты для запуска
```

## 🎯 Основные компоненты

### UserService
Бизнес-логика для управления пользователями:
- Создание пользователей с валидацией
- Поиск по ID и email
- Обновление информации
- Удаление пользователей

### UserDao
Слой доступа к данным с оптимизированными запросами:
- Один SQL запрос для UPDATE операций
- Транзакционность
- Обработка ошибок

### User Entity
JPA сущность с Lombok аннотациями:
- `@Data` - автоматические геттеры/сеттеры
- `@NonNull` - валидация параметров
- Автоматическая генерация equals/hashCode/toString

## 🔧 Конфигурация

### Hibernate настройки
- Автоматическое создание схемы БД
- Пул соединений
- Логирование SQL запросов

### Логирование
- Logback для структурированного логирования
- Различные уровни логирования для dev/prod

## 📊 Мониторинг и логи

Приложение логирует:
- SQL запросы (в режиме разработки)
- Операции с пользователями
- Ошибки и исключения
- Производительность операций

## 🤝 Вклад в проект

1. Форкните репозиторий
2. Создайте ветку для новой функции (`git checkout -b feature/AmazingFeature`)
3. Зафиксируйте изменения (`git commit -m 'Add some AmazingFeature'`)
4. Отправьте в ветку (`git push origin feature/AmazingFeature`)
5. Откройте Pull Request

## 📝 Лицензия

Этот проект распространяется под лицензией MIT. См. `LICENSE` для подробностей.

## 👥 Авторы

- **Ваше имя** - *Первоначальная работа* - [GitHub](https://github.com/yourusername)

## 🙏 Благодарности

- Hibernate команде за отличный ORM фреймворк
- JUnit команде за мощную систему тестирования
- Testcontainers за упрощение интеграционных тестов
