![Logo](docs/img/logo.png)
# My Task Planner - Backend

My Task Planner - Backend is a Spring Boot–powered RESTful API that serves as the server-side component of the My Task Planner application. It manages core functionality such as user authentication, task and sprint management, and data persistence. The service exposes secure endpoints for clients to create, read, update, and delete tasks and related resources. Built with Spring Boot, it is designed for extensibility and maintainability, leveraging industry best practices in modern Java development.


## Features

- **User Authentication & Authorization:** Secure user login and registration (implemented via Spring Security and JWT tokens).
- **RESTful API:** Well-defined REST endpoints that allow frontend to interact with the system. The API enforces access control and input validation.
- **Data Persistence:** Utilizes Spring Data JPA and Hibernate to map entities to a relational database. Repository interfaces handle all database interactions.
- **Validation & Error Handling:** Robust request validation on incoming data and consistent error response structure. Business and data logic is validated in the service layer before persisting.
- **Interactive API Documentation with Swagger (OpenAPI 3)** for seamless endpoint exploration and testing.
- **Audit-Friendly Entity Management**: All entities are timestamped (`created_at`, `updated_at`, `deleted_at`) to facilitate auditing and data traceability.
- **Soft Deletes**: Logical deletion strategy for preserving data history and improving system traceability.


## Tech Stack

**Spring Boot:** Provides a fast, opinionated, and production-grade framework for building RESTful APIs with minimal configuration.

**Java 17 (LTS):** Long-term support, performance optimizations, and modern language features.

**Spring Security + JWT:** Enables robust, customizable authentication and authorization mechanisms.

**PostgreSQL:** Reliable, open-source relational database with strong support for JSON.

**Swagger (SpringDoc OpenAPI):** Automatically generates interactive API documentation for improved developer experience and client integration.

**JUnit & Mockito:** Ensures reliable, testable code with a focus on unit and integration testing for service and controller layers.


## API Reference

This backend is fully documented using **Swagger (OpenAPI 3)**. You can access the interactive documentation at:

https://my-task-planner-backend.onrender.com/swagger-ui/index.html


## Deployment

This backend service is deployed and maintained in a production environment with automated CI/CD pipelines. 

The live API is accessible at:

https://my-task-planner-backend.onrender.com/

The frontend client is available at:

https://my-task-planner-frontend.vercel.app/

This project is intended to be used in production as a deployed web client. Manual build or local run is not required for end users.

## Roadmap

- Implement role-based board sharing capabilities to enhance collaboration and access control.

- Introduce due dates for tasks along with daily reminders to improve task tracking and user productivity.


## Authors

[**Diego Bustos**](https://github.com/DiegoBustos16)