# Docker Compose: локальное окружение TeamCity

## Что поднимаем

Через `docker compose` поднимаются сервисы:
- `teamcity-server`
- `teamcity-agent-1`
- `teamcity-agent-2`
- `swagger-ui` (опционально, через профиль `swagger`)
- `nginx` (опционально, через профиль `swagger`; reverse proxy для `swagger-ui` и `teamcity-server` REST API)
- `selenoid` (опционально, через профиль)
- `selenoid-ui` (опционально, через профиль)

Файлы инфраструктуры лежат в директории `infra/`.

---

## Предварительные требования

- Docker Desktop (с поддержкой Docker Compose)
- Java 21
- Maven 3.9+

---

## Конфигурационные файлы

```text
infra/
├── docker-compose.yml
├── browsers.json
├── nginx/
│   └── default.conf
├── .env.example
└── .env
```

### Как работать с `.env`

`.env.example` хранится в git как шаблон.
Локально используйте `.env`:

```bash
cp infra/.env.example infra/.env
```

Текущие переменные:

```properties
TEAMCITY_VERSION=2025.11.2
TEAMCITY_PORT=8111
SWAGGER_UI_PORT=8082
SELENOID_PORT=4444
SELENOID_UI_PORT=8080
TEAMCITY_AGENT_1_NAME=tc-agent-1
TEAMCITY_AGENT_2_NAME=tc-agent-2
```

---

## Первый запуск (локальный режим)

```bash
cd infra
docker compose up -d
docker compose ps
```

Проверить в браузере:
- TeamCity: `http://localhost:8111`

Если это первый запуск на этой машине, пройти визард TeamCity:
1. выбрать `Internal (HSQLDB)`
2. принять лицензию
3. создать пользователя (для учебного стенда можно `admin/admin`)

Важно: пока не пройден initial setup TeamCity, часть REST/Swagger-эндпоинтов может быть недоступна или требовать авторизацию.

---

## Профиль `swagger`

Профиль `swagger` включает сервисы:
- `swagger-ui`
- `nginx`

Без профиля `swagger` endpoint `http://localhost:8082` не поднимается.

Запуск:

```bash
cd infra
docker compose --profile swagger up -d
docker compose --profile swagger ps
```

Проверки:
- Swagger UI: `http://localhost:8082`

---

## Профиль `selenoid`

Профиль `selenoid` включает сервисы:
- `selenoid`
- `selenoid-ui`

Без профиля `selenoid` endpoints `http://localhost:4444` и `http://localhost:8080` не поднимаются.

Подготовка (один раз):

```bash
docker pull selenoid/vnc_chrome:128.0
```

Запуск:

```bash
cd infra
docker compose --profile selenoid up -d
docker compose --profile selenoid ps
```

Проверки:
- Selenoid status: `http://localhost:4444/status`
- Selenoid UI: `http://localhost:8080`

Проверка сессии через curl:

```bash
curl -X POST "http://localhost:4444/wd/hub/session" \
  -H "Content-Type: application/json" \
  -d '{
    "capabilities": {
      "alwaysMatch": {
        "browserName": "chrome",
        "browserVersion": "128.0",
        "selenoid:options": {
          "enableVNC": true
        }
      }
    }
  }'
```
---

## Матрица режимов запуска

| Команда | Что поднимается |
| --- | --- |
| `docker compose up -d` | TeamCity server + 2 агента |
| `docker compose --profile swagger up -d` | TeamCity server + 2 агента + Swagger UI (`swagger-ui` + `nginx`) |
| `docker compose --profile selenoid up -d` | TeamCity server + 2 агента + Selenoid (`selenoid` + `selenoid-ui`) |
| `docker compose --profile swagger --profile selenoid up -d` | Все сервисы вместе |

---

## Управление окружением

Из директории `infra`:

```bash
docker compose stop
```
Останавливает контейнеры, данные сохраняются.

```bash
docker compose restart
```
Перезапускает контейнеры, данные сохраняются.

```bash
docker compose down
```
Удаляет контейнеры и сеть, volume сохраняются.

```bash
docker compose --profile selenoid down -v
```
Полный сброс (включая volume). После этого TeamCity нужно настраивать заново.
