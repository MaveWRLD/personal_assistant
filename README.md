# Personal Assistant

Spring Boot backend for AI personal assistant. Chat interface (LangChain4j + OpenAI) drives tool-calling over task and note management.

## Features

- **Chat assistant** — conversational endpoint backed by LangChain4j, calls tools to manage tasks/notes.
- **Task management** — CRUD, status/priority, search/filter via specifications.
- **Note management** — CRUD, retrieval by creation date.
- **Security** — Spring Security config.

## Tech Stack

- Java 25, Spring Boot 4.0.5
- Spring Web MVC, Spring Data JPA, PostgreSQL
- LangChain4j (core, open-ai, spring-boot-starter)
- MapStruct, Lombok
- Jackson JSR-310

## Project Structure

```
src/main/java/org/mave/personal_assistant/
├── core/models/              # Task, Note domain models + enums
├── features/
│   ├── communication/        # Chat controller/service, DTOs
│   ├── note_management/      # Note controller/service/repo/tool
│   └── task_management/      # Task controller/service/repo/tool/spec
├── infrastructure/config/    # LangChain, Assistant config
└── shared/                   # Exceptions, security config
```

## Prerequisites

- JDK 25
- Docker (for Postgres)
- OpenAI API key

## Setup

1. Copy env file and fill in values:
   ```bash
   cp .env.example .env
   ```
   Set `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, and your OpenAI API key (per `application.properties`/`langchain4j` config).

2. Start Postgres:
   ```bash
   docker-compose up -d
   ```

3. Run app:
   ```bash
   ./mvnw spring-boot:run
   ```

## Testing

```bash
./mvnw test
```
