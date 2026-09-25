[![Java CI with Gradle](https://github.com/Meruduya/UI-automation-card-transfer-tests-with-Selenide/actions/workflows/gradle.yml/badge.svg)](https://github.com/Meruduya/UI-automation-card-transfer-tests-with-Selenide/actions/workflows/gradle.yml)

# UI automation: перевод денег между картами

Автотесты для учебного банковского приложения, в котором пользователь переводит деньги между своими картами.

**Stack:** `Java 11` · `JUnit 5` · `Gradle` · `Selenide` · `Lombok` · `GitHub Actions`

## Что проверяется

- перевод с первой карты на вторую и со второй на первую;
- перевод суммы, превышающей баланс, с проверкой сообщения об ошибке;
- неизменность балансов после неуспешного перевода;
- перевод суммы 0.

## Структура

```
src/test/java/ru/netology/web/
├── data/DataHelper.java        тестовые данные: пользователь, код, карты
├── page/                       Page Object: вход, подтверждение, личный кабинет, перевод
└── test/MoneyTransferTest.java тестовые сценарии
```

## Запуск

1. Запустить тестируемое приложение (оно лежит в папке `artifacts`):

```
   java -jar ./artifacts/app-ibank-build-for-testers.jar
```

2. В другом терминале запустить тесты:

```
   ./gradlew clean test
```

   Для запуска без открытия окна браузера:

```
   ./gradlew clean test -Dselenide.headless=true
```

Тесты также автоматически запускаются в GitHub Actions при каждом push.
