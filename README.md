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
