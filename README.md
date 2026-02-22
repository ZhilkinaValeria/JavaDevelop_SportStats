# **Sport Stats Application** ⚾

## **О приложении**

**Sport Stats Application** - это REST API сервис для управления статистикой спортивных команд и игроков (на примере бейсбольных команд MLB). Приложение позволяет хранить, обрабатывать и анализировать данные о спортсменах, их физических характеристиках и статистические показатели.

Проект разработан в учебных целях и демонстрирует различные подходы к разработке на Java с использованием Spring Boot.

---

## **Функциональность** ✨

### **Основные возможности:**

| Категория | Описание |
|-----------|----------|
| **CRUD операции** | Создание, чтение, обновление и удаление записей об игроках |
| **Фильтрация** | Поиск игроков по команде, позиции, возрасту, росту, весу |
| **Статистика** | Расчет средних показателей, распределение по командам и позициям |
| **Топ-листы** | Самые высокие, самые тяжелые игроки |
| **Аналитика** | Индекс массы тела (BMI), возрастные категории |

### **Модель данных (Player):**

| Поле | Описание | Пример |
|------|----------|--------|
| `id` | Уникальный идентификатор | `BAL_Adam_Donachie` |
| `name` | Имя игрока | Adam Donachie |
| `team` | Код команды | BAL (Baltimore Orioles) |
| `position` | Позиция на поле | Catcher, Pitcher, Outfielder |
| `heightInches` | Рост в дюймах | 74 |
| `weightLbs` | Вес в фунтах | 180 |
| `age` | Возраст | 22.99 |

**Вычисляемые поля:**
- `heightMeters` - рост в метрах (автоматически)
- `weightKg` - вес в килограммах (автоматически)
- `bmi` - индекс массы тела (автоматически)

---

## **Технологический стек** 🛠️

| Компонент | Технология |
|-----------|------------|
| **Язык** | Java 17 |
| **Фреймворк** | Spring Boot 3.1.5 |
| **База данных** | H2 (in-memory) |
| **Доступ к данным** | JDBC, JPA (Hibernate), in-memory |
| **Безопасность** | Spring Security (Basic Auth) |
| **Сборка** | Maven |
| **Тестирование** | JUnit 5, Mockito, Spring Test |
| **Формат данных** | JSON, CSV |

---

## **Архитектура** 🏗️

Приложение реализует многослойную архитектуру:

```
┌────────────────┐     ┌──────────────┐     ┌─────────────────┐     ┌───────────────┐
│   Клиент       │────▶│  Контроллер  │────▶│    Сервис       │────▶│  Репозиторий  │
│  (curl/браузер)│     │  REST API    │     │ Бизнес-логика   │     │    Данные     │
└────────────────┘     └──────────────┘     └─────────────────┘     └───────────────┘
                                                    │
                       ┌────────────────────────────┼─────────────────────────┐
                       │                            │                         │
                 ┌─────▼──────┐              ┌──────▼─────┐            ┌──────▼─────┐
                 │ CSV режим  │              │ JDBC режим │            │  JPA режим │
                 │ (in-memory)│              │  (H2 SQL)  │            │  (H2 JPA)  │
                 └────────────┘              └────────────┘            └────────────┘
```

### **Три режима работы:**

| Профиль | Хранение данных | Описание |
|---------|-----------------|----------|
| **`csv`** | In-memory + CSV | Загружает данные из `players.csv` при старте, хранит в памяти |
| **`jdbc`** | H2 Database | Работает с базой данных через JDBC (ручные SQL запросы) |
| **`jpa`** | H2 Database | Работает с базой данных через JPA (автоматические запросы) |

---

## **Установка и запуск** 🚀

### **Предварительные требования**

- Java 17 или выше
- Maven 3.6+
- Git (опционально)

### **Клонирование и сборка**

```bash
# Клонировать репозиторий
git clone <url-репозитория>
cd JavaProject

# Собрать проект
mvn clean install
```

### **Запуск приложения**

```bash
# Запуск с CSV профилем (данные из файла)
mvn spring-boot:run -Dspring-boot.run.profiles=csv

# Запуск с JDBC профилем (база данных через JDBC)
mvn spring-boot:run -Dspring-boot.run.profiles=jdbc

# Запуск с JPA профилем (база данных через JPA)
mvn spring-boot:run -Dspring-boot.run.profiles=jpa
```

### **Подготовка данных**

Убедитесь, что файл `players.csv` существует в `src/main/resources/`:

```bash
# Проверить наличие файла
ls -la src/main/resources/players.csv

# Если файла нет, создать его с тестовыми данными
cp src/main/resources/sport_teams_data.csv src/main/resources/players.csv
```

---

## **API Endpoints** 📡

В режиме **`jdbc`** и**`jpa`** приложение запускается с пустой БД. Есть два способа создания БД:

1. Создать вручную.
2. Загрузить готовую в формате **CSV**

Для пакетной загрузки данных в БД есть отдельное API, которое работает только в этих двух режимах. 

### API для работы с CSV (только ADMIN для `jdbc` и `jpa`):

#### **1. Получить информацию о текущем профиле**

curl -u admin:admin http://localhost:8080/api/admin/csv/info

#### **2. Скачать шаблон CSV**

curl -u admin:admin -O http://localhost:8080/api/admin/csv/template

#### **3. Проверить CSV файл без загрузки**

curl -u admin:admin -X POST http://localhost:8080/api/admin/csv/validate \
  -F "file=@players.csv"

#### **4. Загрузить CSV файл в базу данных**

curl -u admin:admin -X POST http://localhost:8080/api/admin/csv/upload \
  -F "file=@players.csv"

#### **5. Очистить базу данных**
curl -u admin:admin -X DELETE http://localhost:8080/api/admin/csv/clear

### **Базовая информация**
- **Базовый URL**: `http://localhost:8080/api`
- **Формат данных**: JSON
- **Авторизация**: Basic Auth

### **Пользователи для тестирования**

| Логин | Пароль | Роли | Права |
|-------|--------|------|-------|
| `user` | `password` | USER | Только чтение |
| `admin` | `admin` | ADMIN, USER | Полный доступ |

---

### **1. CRUD операции с игроками**

#### **Получить всех игроков**
```bash
curl -u user:password http://localhost:8080/api/players
```

#### **Получить игрока по ID**
```bash
curl -u user:password http://localhost:8080/api/players/BAL_Adam_Donachie
```

#### **Создать нового игрока** (только ADMIN)
```bash
curl -u admin:admin -X POST http://localhost:8080/api/players \
  -H "Content-Type: application/json" \
  -d '{
    "id": "CHC_New_Player",
    "name": "New Player",
    "team": "CHC",
    "position": "First Baseman",
    "heightInches": 76,
    "weightLbs": 220,
    "age": 27.5
  }'
```

#### **Обновить игрока** (только ADMIN)
```bash
curl -u admin:admin -X PUT http://localhost:8080/api/players/CHC_New_Player \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Player",
    "team": "CHC",
    "position": "First Baseman",
    "heightInches": 77,
    "weightLbs": 225,
    "age": 28.0
  }'
```

#### **Удалить игрока** (только ADMIN)
```bash
curl -u admin:admin -X DELETE http://localhost:8080/api/players/CHC_New_Player
```

---

### **2. Фильтрация и поиск**

#### **Игроки по команде**
```bash
curl -u user:password http://localhost:8080/api/players/team/BAL
```

#### **Игроки по позиции**
```bash
curl -u user:password "http://localhost:8080/api/players/position/Catcher"
```

#### **Игроки по диапазону возраста**
```bash
curl -u user:password "http://localhost:8080/api/players/age-range?minAge=20&maxAge=30"
```

#### **Игроки по минимальному росту**
```bash
curl -u user:password "http://localhost:8080/api/players/min-height?minHeight=75"
```

#### **Игроки по минимальному весу**
```bash
curl -u user:password "http://localhost:8080/api/players/min-weight?minWeight=200"
```

#### **Поиск по имени**
```bash
curl -u user:password "http://localhost:8080/api/players/search?name=Adam"
```

#### **Игроки по команде и позиции**
```bash
curl -u user:password "http://localhost:8080/api/players/team/BAL/position/Catcher"
```

---

### **3. Публичная статистика (без авторизации)**

#### **Средний возраст всех игроков**
```bash
curl http://localhost:8080/api/players/stats/average-age
```

#### **Средний рост всех игроков (в дюймах)**
```bash
curl http://localhost:8080/api/players/stats/average-height
```

#### **Средний вес всех игроков (в фунтах)**
```bash
curl http://localhost:8080/api/players/stats/average-weight
```

---

### **4. Расширенная статистика (требует авторизации)**

#### **Количество игроков по командам**
```bash
curl -u user:password http://localhost:8080/api/players/stats/teams
```

#### **Количество игроков по позициям**
```bash
curl -u user:password http://localhost:8080/api/players/stats/positions
```

#### **Статистика по конкретной команде**
```bash
curl -u user:password http://localhost:8080/api/players/stats/team-composition/BAL
```

#### **Статистика роста (мин/макс)**
```bash
curl -u user:password http://localhost:8080/api/players/stats/height-stats
```

#### **Статистика веса (мин/макс)**
```bash
curl -u user:password http://localhost:8080/api/players/stats/weight-stats
```

#### **Самые молодые игроки**
```bash
curl -u user:password http://localhost:8080/api/players/youngest
```

#### **Самые возрастные игроки**
```bash
curl -u user:password http://localhost:8080/api/players/oldest
```

#### **Игроки с высоким BMI (>30)**
```bash
curl -u user:password http://localhost:8080/api/players/high-bmi?threshold=30
```

#### **Топ-10 самых высоких игроков**
```bash
curl -u user:password http://localhost:8080/api/players/top10/tallest
```

#### **Топ-10 самых тяжелых игроков**
```bash
curl -u user:password http://localhost:8080/api/players/top10/heaviest
```

#### **Полная статистика**
```bash
curl -u user:password http://localhost:8080/api/players/stats/overall
```

---

## **Примеры ответов** 📋

### **Успешный ответ при получении игрока**
```json
{
  "id": "BAL_Adam_Donachie",
  "name": "Adam Donachie",
  "team": "BAL",
  "position": "Catcher",
  "heightInches": 74,
  "weightLbs": 180,
  "age": 22.99,
  "heightMeters": 1.8796,
  "weightKg": 81.65,
  "bmi": 23.1
}
```

### **Статистика по команде**
```json
{
  "team": "BAL",
  "totalPlayers": 34,
  "averageAge": 28.5,
  "averageHeight": 73.2,
  "averageWeight": 198.5
}
```

### **Топ-10 самых высоких**
```json
[
  {
    "id": "NYY_Some_Player",
    "name": "Some Player",
    "team": "NYY",
    "heightInches": 81
  },
  ...
]
```

---

## **H2 Console** 💾

Приложение включает H2 Database Console для прямого доступа к базе данных:

- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:earthquakesdb`
- **User Name**: `sa`
- **Password**: (оставить пустым)

---

## **Тестирование** 🧪

### **Запуск всех тестов**
```bash
mvn test
```

### **Запуск конкретных тестов**
```bash
# Тесты сервисов
mvn test -Dtest=PlayerServiceTest
mvn test -Dtest=PlayerJpaServiceTest

# Тесты контроллеров
mvn test -Dtest=PlayerControllerCsvTest
mvn test -Dtest=PlayerControllerJpaTest
```

### **Покрытие тестов**
- **Модульные тесты**: Mockito (быстрые, изолированные)
- **Интеграционные тесты**: SpringBootTest (с контекстом)
- **Тесты контроллеров**: WebMvcTest

---

## **Структура проекта** 📁

```
src/
├── main/
│   ├── java/com/example/sportstats/
│   │   ├── SportStatsApplication.java
│   │   ├── controller/
│   │   │   └── PlayerController.java
│   │   ├── model/
│   │   │   ├── Player.java
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   ├── CommonRepository.java
│   │   │   ├── CsvRepository.java
│   │   │   ├── PlayerJdbcRepository.java
│   │   │   ├── PlayerJpaRepository.java
│   │   │   └── UserRepository.java
│   │   ├── service/
│   │   │   ├── PlayerService.java
│   │   │   ├── PlayerJpaService.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── DataInitializer.java
│   │   └── util/
│   │       └── CsvParser.java
│   └── resources/
│       ├── application.properties
│       ├── schema.sql
│       └── players.csv
└── test/
    └── java/com/example/sportstats/
        ├── controller/
        │   ├── PlayerControllerCsvTest.java
        │   └── PlayerControllerJpaTest.java
        ├── service/
        │   ├── PlayerServiceTest.java
        │   └── PlayerJpaServiceTest.java
        └── config/
            ├── TestConfig.java
            ├── TestJpaConfig.java
            └── TestSecurityConfig.java
```

---

## **Профили запуска** 🔄

### **Сравнение режимов работы**

| Характеристика | CSV режим | JDBC режим | JPA режим |
|----------------|-----------|------------|-----------|
| **Хранение** | In-memory | H2 Database | H2 Database |
| **Скорость** | ⚡ Очень быстро | 🐢 Средне | 🐢 Средне |
| **Персистентность** | ❌ Нет | ✅ Да | ✅ Да |
| **SQL запросы** | ❌ Нет | ✅ Ручные | 🤖 Авто |
| **Инициализация** | Из CSV файла | Из schema.sql | JPA auto |

---

## **Устранение неполадок** 🔧

### **Приложение не запускается**
```bash
# Проверьте наличие файла players.csv
ls -la src/main/resources/players.csv

# Очистите и пересоберите проект
mvn clean install
```

### **Ошибка "Player not found"**
```bash
# Проверьте, что игрок существует
curl -u user:password http://localhost:8080/api/players | grep "test-1"
```

### **Ошибка авторизации 401**
```bash
# Проверьте правильность логина и пароля
curl -u user:wrongpassword http://localhost:8080/api/players
# Должно вернуть 401 Unauthorized

curl -u user:password http://localhost:8080/api/players
# Должно вернуть 200 OK
```

### **Ошибка доступа 403**
```bash
# Попытка создания игрока с правами USER (должно вернуть 403)
curl -u user:password -X POST http://localhost:8080/api/players ...

# Используйте ADMIN права
curl -u admin:admin -X POST http://localhost:8080/api/players ...
```

---

## **Заключение** 🎯

**Sport Stats Application** демонстрирует:
- Полноценное REST API с CRUD операциями
- Три различных способа хранения данных
- Безопасность с ролевой моделью
- Профили Spring для переключения реализаций
- Комплексное тестирование (unit, integration, mvc)
- Работу с CSV файлами
- Интеграцию с H2 Database

Проект может служить отличной основой для изучения Spring Boot и разработки подобных приложений!

---

**Автор**: Жилкина Валерия Дмитриевна 
**Версия**: 1.0.0  
