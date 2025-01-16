# OpenFDA API Application

This application provides endpoints to search and store drug application data, integrating with the OpenFDA API, a PostgreSQL database, and utilizing caching and resilience mechanisms.  It also includes OpenAPI documentation.

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Running the Application Locally](#running-the-application-locally)
- [Building and Testing](#building-and-testing)
- [API Documentation](#api-documentation)
- [Endpoints](#endpoints)
    - [Search Drug Applications](#search-drug-applications)
    - [Retrieve Stored Drug Applications](#retrieve-stored-drug-applications)
    - [Create a Drug Application](#create-a-drug-application)
- [Database Migrations](#database-migrations)
- [Caching](#caching)
- [Resilience](#resilience)
- [Directory Structure](#directory-structure)
- [Troubleshooting](#troubleshooting)


## Overview

The OpenFDA API Application allows users to search for drug applications using the OpenFDA API and store these results in a local PostgreSQL database. It leverages caching for improved performance and resilience mechanisms for handling external API failures.


## Prerequisites

- **Java 17:** Ensure you have a compatible JDK installed.
- **Docker:** Required for running the application and its dependencies.
- **Docker Compose:** Used for orchestrating the application and database.
- **Gradle:** Used for building and managing the project.  You can install it via your package manager (e.g., `brew install gradle`, `apt-get install gradle`, `choco install gradle`).



## Running the Application Locally

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/vdonets-dev/fda-home-assignment-04
   cd fda-home-assignment-04
   
2. **Set Up Environment Variables:**

Create a .env file in the root directory with the following content:
```
OPENFDA_APP_PORT=8080
POSTGRES_DB=openfda_db
POSTGRES_HOST=localhost for native run or db for docker
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
OPENFDA_APP_PORT=8080
PGPORT=5432
POSTGRES_VOLUME=./data/postgres
```

3. **Start Services:**
```
docker-compose up --build
```

4. **Access the Application:**
```
API Base URL: http://localhost:8080
```

## Building and Testing
Build the Application:
```
./gradlew build
```
Run Tests:
```
./gradlew test
```

## API Documentation

| Feature          | Description                                                                                                                         |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| Specification    | OpenAPI specification is located at `src/main/resources/openapi/openapi.yaml`. View this file or use Swagger UI after starting the app. |


## Endpoints

| Method | URL                     | Description                                           | Parameters                                                                                                             | Request Body Example                                                                                                         |
|--------|--------------------------|-------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `GET`  | `/api/drugs/search`      | Searches for drug applications.                     | `manufacturer_name` (required), `brand_name` (optional), `page` (optional, default 0), `size` (optional, default 10) | N/A                                                                                                                      |
| `GET`  | `/api/drugs`            | Retrieves stored drug applications.                 | N/A                                                                                                                       | N/A                                                                                                                      |
| `POST` | `/api/drugs`            | Creates a new drug application.                     | N/A                                                                                                                       | ```json { "application_number": "ANDA203300", "manufacturer_names": ["Pfizer", "Hikma"], "substance_names": ["Atorvastatin"], "product_numbers": ["001"] } ``` |


## Database Migrations

| Task               | Description                                                                                                                                           | Command Example                                   |
|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| Adding Migrations | Add a SQL file in `src/main/resources/db/migration/` with a name like `V1__description.sql`.                                                          | N/A                                             |
| Running Migrations | Use Flyway to apply database migrations.                                                                                                              | `docker-compose up flyway`                       |


## Caching

| Provider | Configuration Location | Configuration Example                                                                                                           |
|----------|-------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| Caffeine  | `application.yml`       | ```yaml caching: caches: openFdaCache: expire-after-write-seconds: 120 initial-capacity: 50 maximum-size: 500 ``` |


## Resilience

| Mechanism        | Configuration Location | Configuration Example                                                                                                             |
|-----------------|-------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| Retry           | `application.yml`       | ```yaml openfda: api: retry: max-attempts: 3 backoff-interval: 1000 ```                                                     |
| Circuit Breaker | `application.yml`       | ```yaml openfda: api: circuit-breaker: name: openfdaCircuitBreaker ```                                                          |
