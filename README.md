📋 Task Tracker CRUD
Task Tracker CRUD — это приложение на Spring Boot, реализующее базовые операции CRUD (создание, чтение, обновление, удаление) для управления задачами, с использованием PostgreSQL и Docker для удобного развертывания.

🚀 Возможности
📝 CRUD-операции: создание, просмотр, редактирование и удаление задач.
🗄 Интеграция с базой данных: для хранения данных используется PostgreSQL.
🐳 Docker: быстрое развертывание приложения через Docker Compose.
📘 Документация API: OpenAPI (Swagger) для описания доступных API-эндпоинтов.

🛠 Стек технологий
Java 11+ (Spring Boot, Spring Data JPA)
PostgreSQL
Docker и Docker Compose

🔧 Установка и настройка
Предварительные требования
Docker и Docker Compose
Java JDK 11 или выше

Установка
Клонируйте репозиторий:

git clone https://github.com/luckyhunterzzz/task_tracker_crud.git

cd task_tracker_crud

Запустите приложение через Docker Compose:

docker-compose up -d

Доступ к API возможен по адресу http://localhost:8080.

💾 Настройка базы данных
Конфигурация базы данных включена в docker-compose.yml, создавая PostgreSQL-инстанс с инициализацией схемы из файла mypostgres.sql.

📬 API Эндпоинты
Проект поддерживает REST API для работы с задачами:

GET /tasks - получить список всех задач
POST /tasks - создать новую задачу
PUT /tasks/{id} - обновить существующую задачу
DELETE /tasks/{id} - удалить задачу
Документация доступна по адресу /swagger-ui.html (если подключен Swagger).

📁 Структура проекта
src/main/java — исходный код Java
docker-compose.yml — Docker-настройка для быстрого развертывания
mypostgres.sql — SQL-файл для инициализации базы данных
