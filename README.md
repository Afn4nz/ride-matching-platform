# 🚕Ride-Matching Platform

A microservices playground for a ride-hailing workflow built with **Spring Boot**, **Spring Cloud (Eureka + Gateway)**, **PostgreSQL**, **RabbitMQ**, and **MinIO** — all orchestrated with **Docker Compose v3.9**.  
---

## 🧭Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Local URLs](#local-urls)
- [Swagger / OpenAPI](#swagger--openapi)
- [Services](#services)
  - [discovery-server (Eureka) — :8761](#discovery-server-eureka--8761)
  - [api-gateway — :8080](#api-gateway--8080)
  - [auth-service — :8081](#auth-service--8081)
  - [driver-service — :8082](#driver-service--8082)
  - [ride-service — :8083](#ride-service--8083)
  - [Infrastructure (RabbitMQ / PostgreSQL / MinIO)](#infrastructure-rabbitmq--postgresql--minio)
- [Messaging Topology](#messaging-topology)
- [End-to-End Flows](#end-to-end-flows)
- [Security (JWT)](#security-jwt)
- [Health & Observability](#health--observability)
- [Future Work](#future-work)

---

## 💡Overview

- **Riders** request, cancel, and complete rides.  
- **Drivers** manage profiles, documents, and availability.  
- **Services** communicate asynchronously via **RabbitMQ** (events).  
- **Relational data** in **PostgreSQL**; **documents/images** in **MinIO**.  
- **Access control** uses **JWT** with roles: `RIDER`, `DRIVER`, `ADMIN`. ✅

---

## 🏗Architecture

```
Client → API Gateway :8080 (JWT validation, routing)
│
▼
Eureka :8761 (Service Discovery)
┌───────────┼───────────┐
▼     ▼     ▼
Auth :8081  Driver :8082  Ride :8083
JWT   Profiles, Requests, Matching,
      MinIO, Publishes, Consumes
      AMQP                 AMQP
└───────────┬───────────┘
            ▼
      RabbitMQ :5672/:15672

PostgreSQL :5432   MinIO :9000/:9090
```

---

## 📂Project Structure

```
.
├─ discovery-server/   # Eureka server (Spring Boot)
├─ api-gateway/        # Spring Cloud Gateway
├─ auth-service/       # JWT auth + users (Liquibase)
├─ driver-service/     # Drivers, MinIO, RabbitMQ Publisher + RabbitMQ consumer
├─ ride-service/       # Rides, RabbitMQ Publisher RabbitMQ consumer
└─ docker-compose.yml  # Infra + services wiring
```

---

## ▶️How to Run

### Prerequisites
- Docker Desktop / Engine  

### Build & Start
```bash
docker compose build
docker compose up -d
```

### Stop / Clean
```bash
docker compose down                # stop
docker compose down -v             # stop + remove volumes (⚠ wipes data)
docker compose logs -f ride-service
```

---
## 📦Postman Collection

You can import the ready-made Postman collection into your Postman app.

**Download here:** [Ride-Matching-Platform_collection.json](postmaan/Ride-Matching-Platform.postman_collection.json)

---

## 🌐Local URLs

- **Eureka**: [http://localhost:8761](http://localhost:8761)  
- **API Gateway**: [http://localhost:8080](http://localhost:8080)  
- **RabbitMQ UI**: [http://localhost:15672](http://localhost:15672) (guest/guest)  
- **MinIO Console**: [http://localhost:9090](http://localhost:9090) (minioadmin/minioadmin)  
- **MinIO S3 API**: [http://localhost:9000](http://localhost:9000)  

---

## 📘Swagger / OpenAPI

### Direct service UIs
- [Auth](http://localhost:8081/swagger-ui/index.html) (JSON: `/v3/api-docs`)  
- [Driver](http://localhost:8082/swagger-ui/index.html) (JSON: `/v3/api-docs`)  
- [Ride](http://localhost:8083/swagger-ui/index.html) (JSON: `/v3/api-docs`)  

---

## 🧩Services

### 🌐discovery-server (Eureka) — :8761
- **Role**: Service registry for discovery.  
- **UI**: [http://localhost:8761](http://localhost:8761)  

---

### 🚪api-gateway — :8080
- **Role**: Single ingress for clients; routes to downstream services via Eureka.  
- **Typical routes**:  
  - `/api/auth/**` → auth-service  
  - `/drivers/**` → driver-service  
  - `/rides/**` → ride-service  

---

### 🔐auth-service — :8081
- **Role**: Authentication/authorization. Issues and validates JWTs.  
- **DB**: PostgreSQL schema `auth` (Liquibase migrations).  
- **Base path**: `/api/auth`  

**Endpoints**:
- `POST /api/auth/register` → create user  
- `POST /api/auth/login` → returns JWT  

---

### 🚘driver-service — :8082
- **Role**: Manage driver profiles, documents (MinIO), availability; publish assignments to RabbitMQ, and listen to ride events.  
- **DB**: PostgreSQL schema `driver`  
- **Messaging**:  
  - Consumes: `ride.events` (ride.canceled, ride.completed)  
  - Publishes: `ride-service.assignment`  

**Base path**: `/drivers`  

**Endpoints**:
- `POST /drivers` (Role: DRIVER, multipart upload)  
- `GET /drivers/{id}` (Role: ADMIN)  
- `GET /drivers?isVerified=true` (Role: ADMIN, paginated, support filteration)  
- `PATCH /drivers/{id}/status` (Role: DRIVER)  
- `POST /drivers/{id}/update-documents` (Role: DRIVER)  

---

### 🧭ride-service — :8083
- **Role**: Manage ride lifecycle (request, cancel, complete).  
- **DB**: PostgreSQL schema `ride`  
- **Messaging**:  
  - Publishes: `ride.events` (requested, canceled, completed)  
  - Consumes: `ride-service.assignment`  

**Base path**: `/rides`  

**Endpoints**:
- `POST /rides` (Role: RIDER) → request ride  
- `POST /rides/{id}/cancel` (Role: RIDER) → cancel ride  
- `POST /rides/{id}/end?driverId=x` (Role: DRIVER) → complete ride  

---

### Infrastructure (RabbitMQ / PostgreSQL / MinIO)
- 🐇 **RabbitMQ** — :5672 (AMQP), :15672 (UI)  
- 🐘 **PostgreSQL** — :5432 (schemas: `auth`, `driver`, `ride`)  
- 📦 **MinIO** — :9000 (S3 API), :9090 (Console)  

---

## 📨Messaging Topology

**Exchange**: `ride.events` (topic)  
- Keys: `ride.requested`, `ride.canceled`, `ride.completed`  

**Queues**:
- `driver-service.matching` → consumes `ride.requested`  
- `ride-service.assignment` → direct queue (assignments)  
- `driver-service.availability` → consumes `ride.canceled`, `ride.completed`  

---

## 🔁End-to-End Flows

1. **Request → Assign**  
   Rider requests ride → `ride.requested` → driver-service assigns → publishes assignment → ride-service consumes → ride assigned.  

2. **Cancel**  
   Rider cancels → `ride.canceled` → driver-service updates availability.  

3. **Complete**  
   Driver completes → `ride.completed` → driver-service updates availability.  

---

## 🔐Security (JWT)

- Register/Login via auth-service → obtain JWT  
- Add header:  
  ```
  Authorization: Bearer <JWT>
  ```
- Roles:  
  - **RIDER**: request/cancel rides  
  - **DRIVER**: register/update/complete rides  
  - **ADMIN**: view drivers  

---

## 🩺Health & Observability

- Api-Geteway : `http://localhost:8080/actuator/health`
- Auth-Service : `http://localhost:8081/actuator/health`
- Driver-Service  : `http://localhost:8082/actuator/health`
- Ride-Service : `http://localhost:8083/actuator/health`

  ---
  
## 🔮Future Work 

- Auth ↔ Domain linkage (explicitly requested):

- When a Rider requests their first ride, create a Rider profile/record using the sub/userId from JWT.

- When a Driver registers, link the created driver record to the auth userId parsed from the token (source of truth: auth schema).

- Payments: trigger a payment workflow upon ride.completed.

- Retry/Outbox: add Transactional Outbox + Debezium (or publisher confirms + DLQ) for guaranteed delivery.
  
- Ride Rating.
