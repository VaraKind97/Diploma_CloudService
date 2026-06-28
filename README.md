# Дипломная работа: "Облачное хранилище: Cloud_Service"
REST-сервис для хранения файлов с JWT-аутентификацией.

Проект выполнен в рамках дипломной работы курса **Java-разработчик** (Нетология).

## Стек технологий

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Liquibase
- JWT (jjwt)
- Docker / Docker Compose
- JUnit 5
- Mockito
- Testcontainers
- Maven

---

## Возможности

- регистрация пользователя (REST API);
- авторизация пользователя;
- загрузка файлов;
- скачивание файлов;
- переименование файлов;
- удаление файлов;
- получение списка файлов пользователя;
- JWT-аутентификация;
- хранение информации о файлах в PostgreSQL;
- хранение файлов в локальной файловой системе.

---

## Запуск проекта

### Клонирование репозитория

```bash
git clone https://github.com/VaraKind97/Diploma_CloudService.git
cd CloudService
```

### Запуск

```bash
docker compose up --build
```

После запуска будут автоматически:

- создана база PostgreSQL;
- выполнены миграции Liquibase;
- создан тестовый пользователь.

Backend будет доступен по адресу:

```
http://localhost:8080
```

Frontend:

```
http://localhost:8081
```

---

## ВАЖНО!!!

Интеграционный тест и JPA тесты (FileRepositoryTest.java, UserRepositoryTest.java и CloudServiceIntegrationTest.java) изначально закомментированы, поскольку с ними не получится выполнить maven-package, а также собрать docker-контейнер. После сборки docker-контейнера требуется раскомментировать и запустить данные тесты.

## Тестовый пользователь

```
login: user
password: password
```

---

## API

### Авторизация

```
POST /login
```

### Загрузка файла

```
POST /file
```

### Получение списка файлов

```
GET /list
```

### Скачивание файла

```
GET /file
```

### Переименование файла

```
PUT /file
```

### Удаление файла

```
DELETE /file
```

### Выход из системы

```
POST /logout
```

---

> **Примечание**
>
> В проекте также реализован эндпоинт `POST /register`, который предназначен для регистрации пользователей при разработке и тестировании. В работе с предоставленным Frontend регистрация не используется.

## Тестирование

Проект содержит:

- unit-тесты (JUnit 5 + Mockito);
- интеграционные тесты (Testcontainers).

> Интеграционные тесты требуют установленного Docker.

---

## Структура проекта

```text
src
├── main
│   ├── java
│   │   └── ru
│   │       └── netology
│   │           └── cloudservice
│   │               ├── config
│   │               ├── controller
│   │               ├── dto
│   │               │   ├── request
│   │               │   └── response
│   │               ├── exceptions
│   │               ├── jwt
│   │               ├── model
│   │               ├── repository
│   │               ├── service
│   │               └── CloudServiceApplication.java
│   │
│   └── resources
│       ├── application.properties
│       └── db
│           └── changelog
│               ├── db.changelog-master.yaml
│               └── migrations
│                   ├── 001-create-users.sql
│                   ├── 002-create-files.sql
│                   └── test-user.sql
│
└── test
    └── java
        └── ru
            └── netology
                └── cloudservice
                    ├── controller
                    ├── jwt
                    └── service
```

## Docker
Для запуска используются:
- Docker;
- Docker Compose.
Контейнеры:
- PostgreSQL 16;
- CloudService.


## Автор

**Варакин Дмитрий Сергеевич**,

студент Нетологии курса "Java-разработчик с нуля" 
