# TeamCity Test Automation

API- и UI-тесты для TeamCity на Java 21.

**Стек:** JUnit 5, RestAssured, Selenide, AssertJ, Lombok, Jackson.

---

## Быстрый старт: как написать тест

### API-тест за 5 минут

```java
@ApiTest       // подключает создание юзера + тег "api"
@WithBuild     // подключает создание проекта и билд-конфигурации
public class MyTest extends BaseTest {

    @Test
    void myTest(
            @User CreateUserResponse user,       // юзер создаётся автоматически
            @Project ProjectResponse project,     // проект создаётся автоматически
            @Build CreateBuildTypeResponse build   // билд создаётся автоматически
    ) {
        // act — тестируемый запрос (всегда inline)
        ProjectResponse response = new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(project.getId());

        // assert
        softly.assertThat(response.getId()).isEqualTo(project.getId());
    }
    // cleanup происходит автоматически — удалять ничего не нужно
}
```

### UI-тест за 5 минут

```java
@WebTest       // подключает создание юзера + логин в браузер через cookie
@WithBuild     // подключает создание проекта и билд-конфигурации
public class MyUITest extends BaseUITest {

    @Test
    void myTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        // к моменту выполнения теста браузер уже залогинен под user
        new ProjectsPage().open().shouldContainProjectId(project.getId());
    }
}
```

---

## Аннотации — что за ними стоит

Аннотации автоматически создают тестовые данные **перед** тестом и удаляют **после**.

### Мета-аннотации (ставятся на класс)

| Аннотация | Что делает |
|-----------|-----------|
| `@ApiTest` | Подключает `UserExtension` (поддержка `@User`) + тег `api` |
| `@WebTest` | Подключает `UserExtension` + `UiAuthExtension` (логин в браузер) + тег `web` |
| `@WithBuild` | Подключает `ProjectExtension` + `BuildExtension` (поддержка `@Project` и `@Build`) |
| `@WithProject` | Подключает только `ProjectExtension` (поддержка `@Project`) |

### Аннотации параметров (ставятся на параметры метода)

| Аннотация | Тип параметра | Что создаёт | Что удаляет после теста |
|-----------|--------------|------------|----------------------|
| `@User` | `CreateUserResponse` | Пользователя через API | Удаляет пользователя |
| `@Project` | `ProjectResponse` | Проект через API | Удаляет проект |
| `@Build` | `CreateBuildTypeResponse` | Билд-конфигурацию через API | Удаляет билд-конфигурацию |

### Параметры аннотаций

```java
// Юзер с конкретной ролью (по умолчанию SYSTEM_ADMIN)
@User(role = RoleId.PROJECT_VIEWER) CreateUserResponse user

// Проект с конкретным именем/id
@Project(projectName = "MyProject") ProjectResponse project

// Билд с конкретным именем
@Build(buildName = "MyBuild") CreateBuildTypeResponse build
```

Если параметры не указаны — данные генерируются случайно.

### Аннотация @WithAgent (ставится на метод)

```java
@Test
@WithAgent(count = 1)
void testWithAgent(Agent[] agents) { ... }
```

Проверяет доступность агентов, блокирует их на время теста (через lock),
после теста восстанавливает состояние (enable + authorize).

### Важно: порядок параметров

`@Build` зависит от `@Project` — проект должен быть создан раньше.

```java
// Правильно: @Project перед @Build
void test(@User ... user, @Project ... project, @Build ... build)

// Неправильно: @Build не найдёт проект
void test(@User ... user, @Build ... build)
```

Если нужен `@Build` без автоматического `@Project`, укажите `projectId` явно:
```java
void test(@Build(projectId = "MyProject") CreateBuildTypeResponse build)
```

---

## Как устроены запросы

### Два класса для HTTP-запросов

| Класс | Возвращает | Когда использовать |
|-------|-----------|-------------------|
| `CrudRequester` | `ValidatableResponse` (сырой ответ) | Когда нужен доступ к телу/статусу вручную |
| `ValidatedCrudRequester<T>` | `T` (десериализованный объект) | Когда нужен готовый объект |

Оба принимают три аргумента: **кто** запрашивает, **куда**, **какой ответ ожидаем**.

```java
new CrudRequester(
    RequestSpecs.authAsUser(user),     // кто: авторизация
    Endpoint.PROJECTS,                  // куда: URL
    ResponseSpecs.ok()                  // ожидаемый статус: 200
).get();

new ValidatedCrudRequester<ProjectResponse>(
    RequestSpecs.authAsUser(user),
    Endpoint.PROJECT_ID,
    ResponseSpecs.requestReturnsOk()
).get(project.getId());               // подставляет id в URL
```

### Конвенция: степы vs inline-запросы

- **Степы** (`AdminSteps`, `BuildManageSteps`, ...) — для **arrange и assert**.
  Подготовка данных, проверка состояния. Детали запроса не важны.

- **Inline `new CrudRequester(...)`** — для **act**.
  Тестируемое действие. Детали запроса видны, потому что это суть теста.

```java
// arrange — через степы (детали скрыты)
BuildManageSteps.createBuildType(project.getId());

// act — inline (детали видны, это то, что мы тестируем)
new CrudRequester(
    RequestSpecs.authAsUser(user),
    Endpoint.BUILD_TYPES_ID,
    ResponseSpecs.noContent()
).delete(buildId);

// assert — через степы
boolean exists = BuildManageSteps.getAllBuildTypes().stream()
    .anyMatch(b -> b.getId().equals(buildId));
assertFalse(exists);
```

### RequestSpecs — авторизация

| Метод | Описание |
|-------|---------|
| `RequestSpecs.authAsUser(user)` | Запрос от имени тестового юзера (JSON) |
| `RequestSpecs.authAsUser(user, ContentType.TEXT)` | Запрос от имени юзера с другим content type |
| `RequestSpecs.adminSpec()` | Запрос от имени суперадмина из конфига |

### ResponseSpecs — ожидаемый ответ

| Метод | Статус |
|-------|--------|
| `ResponseSpecs.ok()` / `requestReturnsOk()` | 200 |
| `ResponseSpecs.noContent()` | 204 |
| `ResponseSpecs.badRequest()` | 400 |
| `ResponseSpecs.notFound()` | 404 |
| `ResponseSpecs.forbidden()` | 403 |
| `ResponseSpecs.unauthorized()` | 401 |
| `ResponseSpecs.deletesQuietly()` | 200 / 204 / 404 (для cleanup) |
| `ResponseSpecs.badRequestWithErrorText(text)` | 400 + проверка текста ошибки |
| `ResponseSpecs.notFoundWithErrorText(text)` | 404 + проверка текста ошибки |

### Endpoint — справочник URL

```java
Endpoint.PROJECTS           // /projects
Endpoint.PROJECT_ID         // /projects/id:{id}
Endpoint.BUILD_TYPES        // /buildTypes
Endpoint.BUILD_TYPES_ID     // /buildTypes/id:{id}
Endpoint.USERS              // /users
Endpoint.AGENTS             // /agents
Endpoint.BUILD_QUEUE        // /buildQueue
// ... полный список в Endpoint.java
```

URL с параметрами (содержат `%s`) подставляются автоматически
при вызове `.get(id)`, `.delete(id)` и т.д.

---

## Структура проекта

```
src/main/java/
├── api/
│   ├── configs/          Config — чтение config.properties
│   ├── models/           DTO-классы (request/response)
│   ├── requests/
│   │   ├── skeleton/     CrudRequester, ValidatedCrudRequester, Endpoint
│   │   └── steps/        Степы (AdminSteps, BuildManageSteps, ...)
│   └── specs/            RequestSpecs, ResponseSpecs
├── common/
│   ├── data/             Enum'ы и константы (RoleId, ApiAtributesOfResponse, ...)
│   └── generators/       TestDataGenerator, RandomModelGenerator
├── jupiter/
│   ├── annotation/       @User, @Project, @Build, @WithAgent
│   │   └── meta/         @ApiTest, @WebTest, @WithBuild, @WithProject
│   └── extension/        JUnit-расширения (UserExtension, ProjectExtension, ...)
└── ui/
    ├── component/        UI-компоненты (LeftNavigationMenu)
    └── pages/            Page Object'ы (ProjectsPage, BasePage, ...)

src/test/java/
├── api/                  API-тесты
└── ui/                   UI-тесты
```

---

## Конфигурация

Файл `src/main/resources/config.properties`:

```properties
BaseUrl=http://localhost:8111
api=/app/rest

admin.login=admin
admin.password=admin

ui.baseUrl=http://host.docker.internal:8111
uiRemote=http://localhost:4444/wd/hub
browser=chrome
browser.size=1920x1080
```

Тесты запускаются **параллельно** (5 потоков), настройка в `junit-platform.properties`.

---

## Чеклист: пишу новый API-тест

1. Поставить на класс `@ApiTest` (или `@ApiTest` + `@WithBuild`/`@WithProject`)
2. Наследоваться от `BaseTest`
3. Добавить в параметры метода нужные аннотации (`@User`, `@Project`, `@Build`)
4. Порядок: `@User` → `@Project` → `@Build`
5. Arrange/assert — через степы, act — через inline `CrudRequester`/`ValidatedCrudRequester`
6. Использовать `softly.assertThat(...)` для ассертов (из `BaseTest`)
7. Cleanup не писать — он автоматический

## Чеклист: пишу новый UI-тест

1. Поставить на класс `@WebTest` (+ `@WithBuild`/`@WithProject` если нужны данные)
2. Наследоваться от `BaseUITest`
3. Добавить `@User` — логин в браузер произойдёт автоматически через cookie
4. Использовать Page Object'ы из `ui.pages`
