# Anomaly Detection Platform

Distributed microservices platform for real-time anomaly detection. Spring Boot backend API, FastAPI ML inference service, Kafka event streaming, and Next.js monitoring dashboard — all containerized with Docker.

## Architecture

```
┌─────────────┐    ┌──────────────────┐    ┌──────────────┐
│   Frontend   │    │   Backend API    │    │  ML Service   │
│  (Next.js)   │◄──►│ (Spring Boot)    │◄──►│  (FastAPI)    │
└─────────────┘    └──────┬───────────┘    └──────┬───────┘
                          │                       │
                   ┌──────┴───────────────────────┴──────┐
                   │            Apache Kafka              │
                   └──────┬──────────────────────────────┘
                          │
                   ┌──────┴──────┐    ┌──────────────────┐
                   │  PostgreSQL  │    │  Data Simulator   │
                   └─────────────┘    └──────────────────┘
```

## Services

| Service | Tech | Port | Description |
|---------|------|------|-------------|
| backend-api | Java 17, Spring Boot | 8080 | REST API with JWT auth, anomaly CRUD, severity scoring |
| ml-service | Python, FastAPI | 8000 | Isolation Forest model training and inference |
| ingestion-service | Python, FastAPI | 8001 | Kafka consumer, schema validation, Bloom-filter dedup |
| frontend | Next.js, TypeScript | 3000 | Monitoring dashboard with real-time anomaly feeds |
| data-simulator | Python | — | Generates synthetic telemetry data for testing |

## Tech Stack

- **Backend:** Java 17, Spring Boot, REST APIs, JWT authentication
- **ML/Data:** Python, FastAPI, scikit-learn (Isolation Forest), pandas
- **Messaging:** Apache Kafka (producer/consumer workflows)
- **Database:** PostgreSQL with Flyway migrations, composite indexing
- **Frontend:** Next.js, TypeScript, Tailwind CSS
- **Infrastructure:** Docker, Docker Compose

## Features

- Real-time anomaly detection with configurable severity scoring
- Kafka producer/consumer workflows for event-driven data ingestion
- Bloom-filter deduplication to prevent duplicate event processing
- Schema validation on incoming data streams
- JWT-protected REST APIs with structured error handling
- PostgreSQL persistence with Flyway-managed migrations
- Health check endpoints across all services
- Dockerized deployment with single docker-compose up

## Getting Started

```bash
# Clone the repo
git clone https://github.com/yashlohade21/anomaly-detection-platform.git
cd anomaly-detection-platform

# Start all services
docker-compose up --build

# Services available at:
# Backend API:  http://localhost:8080
# ML Service:   http://localhost:8000
# Ingestion:    http://localhost:8001
# Frontend:     http://localhost:3000
```

## Project Structure

```
anomaly-detection-platform/
├── backend-api/          # Spring Boot REST API
├── ml-service/           # FastAPI ML inference
├── ingestion-service/    # Kafka consumer + validation
├── frontend/             # Next.js dashboard
├── data-simulator/       # Test data generator
├── docker-compose.yml    # Multi-service orchestration
└── Architecture.pdf      # System design document
```
