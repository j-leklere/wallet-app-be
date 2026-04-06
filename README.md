# wallet-api

Personal finance management REST API built with Spring Boot.

## Requirements

- Java 21
- Maven 3.9+
- MySQL 8+

## Setup

1. Create the database:
   ```sql
   CREATE DATABASE wallet_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Configure environment variables (or rely on defaults for local dev):
   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=wallet_db
   DB_USERNAME=root
   DB_PASSWORD=your_password
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

## Database migrations

Flyway manages the schema. Migration scripts go in `src/main/resources/db/migration/` using the naming convention `V{version}__{description}.sql` (e.g., `V1__create_users_table.sql`).
