# User Service

Консольное приложение для управления пользователями, использующее Hibernate и PostgreSQL.

## Описание

Приложение предоставляет базовые CRUD операции (Create, Read, Update, Delete) для управления сущностью User с полями:
- `id` - уникальный идентификатор
- `name` - имя пользователя
- `email` - электронная почта (уникальная)
- `age` - возраст
- `created_at` - дата создания

## Технологии

- **Java 11+** - основной язык программирования
- **Maven 3.6+** - управление зависимостями и сборка
- **Hibernate 6.2.7** - ORM для работы с базой данных
- **PostgreSQL 12+** - основная база данных
- **H2** - in-memory база данных для тестов
- **Logback 1.2.12** - логирование
- **JUnit 5** - unit-тестирование
- **Jakarta EE** - стандарт для JPA аннотаций

## Структура проекта

```
user-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/userservice/
│   │   │       ├── entity/
│   │   │       │   └── User.java
│   │   │       ├── dao/
│   │   │       │   └── UserDao.java
│   │   │       ├── service/
│   │   │       │   └── UserService.java
│   │   │       ├── console/
│   │   │       │   └── UserConsole.java
│   │   │       ├── util/
│   │   │       │   └── HibernateUtil.java
│   │   │       ├── exception/
│   │   │       │   ├── UserServiceException.java
│   │   │       │   └── DatabaseException.java
│   │   │       └── UserServiceApplication.java
│   │   └── resources/
│   │       ├── hibernate.cfg.xml
│   │       └── logback.xml
│   └── test/
│       ├── java/
│       │   └── com/example/userservice/
│       │       └── UserServiceTest.java
│       └── resources/
│           └── hibernate-test.cfg.xml
├── pom.xml
└── README.md
```

## Настройка

### 1. Требования

- Java 11 или выше
- Maven 3.6 или выше
- PostgreSQL 12 или выше

### 2. Настройка базы данных

1. Установите PostgreSQL
2. Создайте базу данных:
   ```sql
   CREATE DATABASE user_service_db;
   ```
3. Создайте пользователя (опционально):
   ```sql
   CREATE USER user_service_user WITH PASSWORD 'password';
   GRANT ALL PRIVILEGES ON DATABASE user_service_db TO user_service_user;
   ```

### 3. Настройка подключения

Отредактируйте файл `src/main/resources/hibernate.cfg.xml`:

```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/user_service_db</property>
<property name="hibernate.connection.username">postgres</property>
<property name="hibernate.connection.password">password</property>
```

## Сборка и запуск

### Сборка проекта

```bash
mvn clean compile
```

### Запуск приложения

```bash
mvn exec:java -Dexec.mainClass="com.example.userservice.UserServiceApplication"
```

Или скомпилируйте и запустите:

```bash
mvn clean package
java -cp target/classes:target/dependency/* com.example.userservice.UserServiceApplication
```

### Запуск тестов

```bash
mvn test
```

## Использование

После запуска приложения откроется консольное меню с опциями:

1. **Create User** - создание нового пользователя
2. **Get User by ID** - поиск пользователя по ID
3. **Get User by Email** - поиск пользователя по email
4. **Get All Users** - получение списка всех пользователей
5. **Update User** - обновление информации о пользователе
6. **Delete User** - удаление пользователя
7. **Search Users** - поиск пользователей по имени или возрасту
0. **Exit** - выход из приложения

## Примеры использования

### Создание пользователя
```
Enter name: John Doe
Enter email: john.doe@example.com
Enter age (optional, press Enter to skip): 30
```

### Поиск пользователя
```
Enter user ID: 1
```

### Обновление пользователя
```
Enter user ID to update: 1
Name [John Doe]: John Updated
Email [john.doe@example.com]: john.updated@example.com
Age [30]: 31
```

## Логирование

Логи сохраняются в:
- `logs/user-service.log` - общие логи
- `logs/user-service-error.log` - только ошибки

Уровень логирования можно настроить в `src/main/resources/logback.xml`.

## Тестирование

Проект включает unit-тесты, использующие H2 in-memory базу данных для изоляции тестов.

Запуск тестов:
```bash
mvn test
```

## Архитектура

Приложение использует следующие паттерны:

- **DAO Pattern** - для работы с базой данных
- **Service Layer** - для бизнес-логики
- **Singleton** - для Hibernate SessionFactory
- **Exception Handling** - для обработки ошибок

## Возможные проблемы

1. **Ошибка подключения к БД** - проверьте настройки в `hibernate.cfg.xml`
2. **Ошибка компиляции** - убедитесь, что используется Java 11+
3. **Ошибка Maven** - проверьте наличие Maven в PATH

## Лицензия

Этот проект создан в учебных целях.
