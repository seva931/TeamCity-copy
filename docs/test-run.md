# Тесты: режимы запуска и отчёт

## Режимы запуска

Переключение между локальным браузером и Selenoid делается через Maven profiles в `pom.xml`:
- `local` (по умолчанию)
- `selenoid`

### Команды запуска

```bash
mvn test                                   # local (по умолчанию)
mvn test -Pselenoid                        # через Selenoid

mvn test -Dtest.groups=web                 # только @Tag("web"), local
mvn test -Dtest.groups=api                 # только @Tag("api"), local
mvn test -Pselenoid -Dtest.groups=web      # только @Tag("web"), Selenoid
```

---

## Отчёт Surefire

Сгенерировать HTML-отчёт:

```bash
mvn surefire-report:report-only
```

Отчёт лежит в файле:
- `target/reports/surefire.html`

Если отчёт пустой или не создаётся, сначала выполните тесты:

```bash
mvn test
mvn surefire-report:report-only
```

---

## Отчёт Allure

Сгенерировать статический отчёт:

```bash
mvn allure:report
```

Открыть отчёт локально:

```bash
mvn allure:serve
```

Если `allure-results` пустой, сначала запустите тесты:

```bash
mvn test
mvn allure:report
```

При падении UI-тестов в Allure автоматически прикладывается скриншот с именем `Screenshot on fail`.
