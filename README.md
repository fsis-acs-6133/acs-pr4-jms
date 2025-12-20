# ACS PR3 — RESTful веб‑сервис (Authors & Books)

Этот репозиторий содержит **Практическую работу №3**: REST API для модели *Authors–Books* (продолжение приложения из **ПР2**).  
Приложение предоставляет CRUD для сущностей, поддерживает **JSON и XML**, а для XML добавлено **XSL‑преобразование**, чтобы браузер показывал XML как HTML‑страницы.  
Также подключена документация **Swagger / OpenAPI**.

---

## Содержание

- [Задание 1 — JAX‑RS vs Spring REST](#задание-1--jax-rs-vs-spring-rest)
- [Задание 2 — выбор предыдущего приложения и проектирование REST API](#задание-2--выбор-предыдущего-приложения-и-проектирование-rest-api)
- [Функциональность (что умеет проект)](#функциональность-что-умеет-проект)
- [REST API (эндпоинты)](#rest-api-эндпоинты)
- [JSON и XML (как выбирать формат)](#json-и-xml-как-выбирать-формат)
- [XSL для XML в браузере](#xsl-для-xml-в-браузере)
- [Swagger / OpenAPI](#swagger--openapi)
- [Запуск проекта](#запуск-проекта)
- [Примеры запросов](#примеры-запросов)
- [Скриншоты](#скриншоты)
- [Как загрузить проект в GitHub (acs-pr3-rest)](#как-загрузить-проект-в-github-acs-pr3-rest)

---

## Задание 1 — JAX‑RS vs Spring REST

### JAX‑RS (Jakarta RESTful Web Services)
**Плюсы**
- Стандарт спецификации (аннотации `@Path`, `@GET`, `@POST`, и т.д.), переносимость между реализациями.
- Хорошо подходит, если проект строится вокруг Jakarta EE / MicroProfile.

**Минусы (в контексте этого проекта)**
- Обычно нужен провайдер/реализация (Jersey/RESTEasy) и доп. настройка интеграции со Spring Boot.
- Если проект уже на Spring (ПР2), то переход на JAX‑RS добавляет лишний «слой» конфигурации.

### Spring REST (Spring MVC / Spring Web)
**Плюсы**
- Нативно работает в Spring Boot: `@RestController`, `@RequestMapping`, `ResponseEntity`, валидация `@Valid`.
- Проще интеграция с сервисами/репозиториями (Spring DI), транзакциями, обработкой ошибок, content negotiation.
- Легко подключить Swagger (springdoc-openapi), Jackson для JSON и XML-маршаллинг.

**Минусы**
- Это не “стандарт API” как JAX‑RS, а фреймворк‑подход.

✅ **Выбор для проекта: Spring REST (Spring MVC)**  
**Почему:** базовый проект уже реализован на Spring Boot (ПР2), и расширение до REST в Spring даёт минимальную сложность и максимум практической пользы: меньше конфигурации, быстрее разработка, проще поддерживать JSON/XML + Swagger + XSL.

---

## Задание 2 — выбор предыдущего приложения и проектирование REST API

В качестве базы выбрано приложение из **Практической работы №2**:

- **Author**: `id`, `fullName`, `birthYear`
- **Book**: `id`, `title`, `publishedYear`, `authorId`

На его основе спроектировано REST API, которое предоставляет операции:
- Получение списков и отдельных объектов
- Создание
- Обновление
- Удаление

Дополнительно:
- `GET /api/authors/{id}/books` — книги конкретного автора.

---

## Функциональность (что умеет проект)

### PR2 (MVC страницы)
- `/authors` — список/создание/редактирование/удаление авторов
- `/books` — список/создание/редактирование/удаление книг

### PR3 (REST)
- REST CRUD для Authors и Books
- JSON и XML на вход/выход
- XSL преобразование: при запросе XML добавляется `<?xml-stylesheet ...?>`, чтобы браузер отображал как HTML
- Swagger UI для удобной работы с API через браузер

### Важная особенность (удаление автора)
Книги **не удаляются**, даже если удаляется автор.  
При удалении автора в таблице `books` поле `author_id` становится `NULL` (**ON DELETE SET NULL**).  
Это позволяет сохранять записи о книгах.

---

## REST API (эндпоинты)

### Authors
- `GET  /api/authors` — список авторов  
- `GET  /api/authors/{id}` — автор по id  
- `POST /api/authors` — создать автора  
- `PUT  /api/authors/{id}` — обновить автора  
- `DELETE /api/authors/{id}` — удалить автора  
- `GET /api/authors/{id}/books` — книги автора  

### Books
- `GET  /api/books` — список книг  
- `GET  /api/books/{id}` — книга по id  
- `POST /api/books` — создать книгу  
- `PUT  /api/books/{id}` — обновить книгу  
- `DELETE /api/books/{id}` — удалить книгу  

---

## JSON и XML (как выбирать формат)

Проект поддерживает **оба формата**.

### 1) Через query‑параметр (удобно для браузера)
- `?format=xml` → отдаётся XML (и добавляется XSL PI для красивого HTML в браузере)

Пример:
- `http://localhost:8080/api/authors?format=xml`
- `http://localhost:8080/api/books?format=xml`

### 2) Через заголовок Accept (для Postman/curl/клиентов)
- `Accept: application/json`
- `Accept: application/xml`

---

## XSL для XML в браузере

Чтобы браузер показывал XML как HTML:
1. XSL-файлы лежат в статике:
   - `src/main/resources/static/xsl/authors.xsl`
   - `src/main/resources/static/xsl/author.xsl`
   - `src/main/resources/static/xsl/books.xsl`
   - `src/main/resources/static/xsl/book.xsl`
2. При XML‑ответе сервис добавляет в начало:
   - `<?xml-stylesheet type="text/xsl" href="/xsl/....xsl"?>`

Можно просто открыть в браузере:
- `http://localhost:8080/api/authors?format=xml`
- `http://localhost:8080/api/books?format=xml`

---

## Swagger / OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

⚠️ Если открыть `/v3/api-docs` в браузере, это **не ошибка**, а JSON‑описание спецификации.

---

## Запуск проекта

### Требования
- **Java 17+** (или та версия, которая указана в проекте)
- **Maven 3.9+** (или используйте `mvnw`)
- **PostgreSQL** (локально или в Docker)

### 1) Настройка базы данных
Создайте БД (пример):
- database: `acs_pr3`
- user: `postgres`
- password: `postgres`

Проверьте настройки в:
- `src/main/resources/application.properties`

Пример (проверь/подставь свои значения):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/acs_pr3
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false

spring.flyway.enabled=true
```

### 2) Запуск
Из корня проекта:

```bash
./mvnw spring-boot:run
```

Или собрать jar:
```bash
./mvnw clean package
java -jar target/*.jar
```

После запуска:
- Главная страница: `http://localhost:8080/`
- MVC: `/authors`, `/books`
- REST: `/api/...`
- Swagger: `/swagger-ui.html`

---

## Примеры запросов

### PowerShell (важно!)
В PowerShell `curl` часто является алиасом `Invoke-WebRequest`, поэтому команды в стиле Linux могут «ломаться».

**Вариант A (рекомендуется): `curl.exe`**
```powershell
curl.exe -X POST "http://localhost:8080/api/authors" `
  -H "Content-Type: application/json" `
  -H "Accept: application/json" `
  -d "{\"fullName\":\"Лев Толстой\",\"birthYear\":1828}"
```

**Вариант B: Invoke-RestMethod**
```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/authors" `
  -Headers @{ "Content-Type"="application/json"; "Accept"="application/json" } `
  -Body '{ "fullName":"Лев Толстой", "birthYear":1828 }'
```

### Создать книгу (JSON)
```powershell
curl.exe -X POST "http://localhost:8080/api/books" `
  -H "Content-Type: application/json" `
  -H "Accept: application/json" `
  -d "{\"title\":\"Война и мир\",\"publishedYear\":1869,\"authorId\":1}"
```

### Получить XML (для браузера)
- `http://localhost:8080/api/authors?format=xml`
- `http://localhost:8080/api/books?format=xml`

---

## Скриншоты

> Вставь сюда скрины (после загрузки в репозиторий положи их в папку `docs/screens/`)

### Главная страница
![Главная страница](docs/screens/home.png)

### XML Authors (c XSL)
![Authors XML](docs/screens/authors-xml.png)

### XML Books (c XSL)
![Books XML](docs/screens/books-xml.png)

---

## Как загрузить проект в GitHub (acs-pr3-rest)

Открой терминал **в корне проекта**.

### 1) Инициализировать git (если ещё не инициализирован)
```bash
git init
```

### 2) Добавить файлы и сделать коммит
```bash
git add .
git commit -m "PR3: REST API + XML/XSL + Swagger"
```

### 3) Привязать удалённый репозиторий
Если репозиторий уже создан на GitHub (например `acs-pr3-rest`):
```bash
git remote add origin https://github.com/fsis-acs-6133/acs-pr3-rest.git
```

Если remote уже был, можно заменить:
```bash
git remote set-url origin https://github.com/fsis-acs-6133/acs-pr3-rest.git
```

### 4) Запушить
```bash
git branch -M main
git push -u origin main
```

### 5) Проверка
Убедись, что:
- есть `README.md`
- нет мусора (`target/`, `.idea/`, `*.iml`)
- скриншоты лежат в `docs/screens/`

---

## Если нужны “максимальные баллы”
Рекомендуется показать преподавателю:
1. Swagger UI (CRUD‑запросы в браузере)
2. XML + XSL в браузере (`?format=xml`)
3. Что удаление автора не удаляет книги (ON DELETE SET NULL)
4. Архитектуру слоёв (controller/service/repository)
