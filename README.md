# Event Manager

A backend system for managing events, locations, and user registrations. Built as a multi-service application with asynchronous notifications and Redis caching.

## Architecture

```
  Client
    │
    ├── REST :8080 ──► event-manager ──────┐
    │                       │              │
    │                    Kafka             ├── PostgreSQL
    │                  (event-changes)     │
    │                       │              │
    └── REST :8081 ──► event-notificator ──┘
    
  event-manager      ──► Redis (read cache)
  event-notificator  ──► Redis (unread counters)
```

## Tech Stack

- **Java 21**, **Spring Boot 3**
- **Spring Security** — JWT authentication
- **Spring Data JPA** — PostgreSQL persistence
- **Spring Cache + Spring Data Redis** — cache-aside pattern with fallback
- **Apache Kafka** — async event-driven notifications
- **PostgreSQL** — primary data store
- **Redis** — read cache and unread counters
- **Docker Compose** — infrastructure setup

## Services

### event-manager (port 8080)
Core service responsible for:
- User registration and JWT-based authentication
- Events and locations management (CRUD)
- User registrations for events
- Redis cache-aside for hot read endpoints with graceful fallback to DB
- Scheduled status updates (WAIT_START → STARTED → FINISHED) with cache invalidation
- Publishing event changes to Kafka

### event-notificator (port 8081)
Notification service responsible for:
- Consuming event change messages from Kafka
- Storing unread notifications per user
- Unread counter in Redis (INCRBY on create, SET after mark-as-read)
- Graceful Redis fallback — counter errors never break the main flow

### event-common
Shared module containing Kafka message contracts used by both services.

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 23
- Maven

### Run infrastructure

```bash
docker compose up -d
```

This starts PostgreSQL, Redis, Zookeeper, and Kafka.

### Run services

Start both services from IntelliJ IDEA or via Maven:

```bash
mvn -pl event-manager spring-boot:run
mvn -pl event-notificator spring-boot:run
```

### Default users

| Login | Password | Role  |
|-------|----------|-------|
| admin | admin    | ADMIN |
| user  | user     | USER  |

### API Documentation

Swagger UI is available after startup:
- event-manager: `http://localhost:8080/swagger-ui/index.html`
- event-notificator: `http://localhost:8081/swagger-ui/index.html`

## Key API Endpoints

### event-manager

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/users/auth` | Authenticate and get JWT token |
| POST | `/users/register` | Register a new user |
| GET | `/locations` | Get all locations (cached) |
| POST | `/locations` | Create a location |
| PUT | `/locations/{id}` | Update a location |
| DELETE | `/locations/{id}` | Delete a location |
| GET | `/events/{id}` | Get event by id (cached) |
| POST | `/events` | Create an event |
| PUT | `/events/{id}` | Update an event |
| DELETE | `/events/{id}` | Cancel an event |
| POST | `/events/{id}/register` | Register for an event |
| DELETE | `/events/{id}/register` | Cancel registration |

### event-notificator

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/notifications` | Get unread notifications |
| POST | `/notifications` | Mark notifications as read |
