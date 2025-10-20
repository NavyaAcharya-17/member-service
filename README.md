# MemberService Application

## APIs
- `POST /api/v1/auth/login`
- `POST /api/v1/user/register`
- `GET /api/v1/members`
- `GET /api/v1/members/{id}`
- `POST /api/v1/members`
- `PUT /api/v1/members/{id}`
- `DELETE /api/v1 /members/{id}`

## Features
- CRUD operations for MemberService
- JWT authentication
- Caching using in-memory cache
- Unit test cases with JaCoCo report generation
- Integration testing
- Logging and handling of necessary exceptions
- API documentation using Swagger

## Screenshots

### Swagger API Documentation
<img width="760" height="466" alt="Screenshot 2025-10-19 194625" src="https://github.com/user-attachments/assets/9ad74be4-1dc0-483f-9eca-2b6453e59bec" />

### JaCoCo Test Coverage Report
<img width="953" height="301" alt="image" src="https://github.com/user-attachments/assets/03ef72bb-244f-481c-9e72-c3f8519dd1c8" />

## Technologies, Tools, and Build Tools

### Technologies
- Java 17  
- Spring Boot 3.5.6 (Web, Data JPA, Security, Validation, Cache)  
- PostgreSQL 17.6  
- H2 Database (for testing)  
- Flyway (Database migration)  
- JWT (JSON Web Tokens)  
- Springdoc OpenAPI (Swagger UI)  

### Tools
- Lombok  
- JaCoCo (Code coverage)  
- AssertJ (Unit testing assertions)  
- JUnit 5 / Spring Boot Test / Spring Security Test  
- Postman (for API testing)  
- IntelliJ IDEA  

### Build Tools / Plugins
- Gradle  
- Spring Boot Gradle Plugin 3.5.6  
- Spring Dependency Management Plugin 1.1.7  
- Java Plugin  
- JaCoCo Plugin 0.8.12  
