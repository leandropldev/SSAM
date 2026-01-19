# Secure Shared Asset Manager (SSAM)
The Secure Shared Asset Manager (SSAM) is a backend service built with Spring Boot, designed to manage secure digital access tokens.
Each token belongs to an owner and can be shared with friends through a controlled slot‑based mechanism.
The system ensures data security, lifecycle management, and automatic cleanup of expired tokens.
This project exposes a REST API that allows clients to:
- Create new secure asset tokens
- List all tokens
- Share a token with a friend (slot allocation)
- Suspend a token
- Terminate a token (with cascading revocation to child tokens)
The application uses PostgreSQL as its database and provides full OpenAPI/Swagger documentation.


🚀 Features
- AES‑256 encryption for confidential token data
- Bitmask slot allocation for sharing tokens
- Lifecycle management (ACTIVE, SUSPENDED, TERMINATED, etc.)
- Cascading termination of child tokens
- Automatic cleanup of expired tokens via scheduled tasks
- OpenAPI documentation available at /swagger-ui.html
- Docker‑ready for local development


📦 Running the Application Locally

Below are the steps required to run the SSAM application on your machine using Docker.

1. Clone the Repository
- git clone https://github.com/leandropldev/SSAM.git
- cd SSAM

2. Prepare Docker Environment
Ensure you have the following installed:
- Docker Engine
- Docker Compose v2+
Verify installation:
docker --version
docker compose version

3. Run the Application with Docker Compose
From the root directory of the project, run:

- docker compose up --build

This will:
- Start a PostgreSQL 16 database container
- Build and start the SSAM Spring Boot application
- Install and start the SSAM Angular Client
- Expose:
- API on http://localhost:8080
- Client on http://localhost:4200/
- Swagger UI on http://localhost:8080/swagger-ui.html

📘 API Documentation
Once the application is running, you can access:

Swagger UI
http://localhost:8080/swagger-ui.html

OpenAPI JSON
http://localhost:8080/v3/api-docs


🧪 Running Tests

To execute unit tests locally:
- mvn clean test
- npx jest



🛠️ Technologies Used
- Java 21
- Typescript 5.9.2
- Angular 21
- Signals
- Forms
- Spring Boot 3
- Spring Data JPA
- PostgreSQL 16
- Docker & Docker Compose
- Springdoc OpenAPI
- JUnit 5 & Mockito
- Jest
