# Team Action Tracker — High-Level Solution Design

## 1. Solution Overview

The Team Action Tracker is designed as a web-based, three-tier application for managing team action items, assignments, progress updates, and status history.

The solution consists of three major layers:

1. Presentation Layer: Vue.js frontend responsible for the user interface and user interactions.
2. Application Layer: Java Spring Boot backend responsible for REST APIs, business logic, validation, authorization, and error handling.
3. Data Layer: MySQL database responsible for persistent storage of users, action items, comments, and action-item history.

The frontend communicates with the backend through REST APIs over HTTP. The backend processes requests, applies business rules, and interacts with the MySQL database.

The layered architecture separates presentation, business logic, and data-access responsibilities to improve maintainability, testability, and future extensibility.

## 2. Technology Stack

| Layer | Technology | Purpose |
|---|---|---|
| Frontend | Vue.js | User interface and client-side application |
| Frontend Tooling | Vite | Development server and frontend build tooling |
| Backend Language | Java 21 | Backend programming language |
| Backend Framework | Spring Boot | REST API and application development |
| Data Access | Spring Data JPA | Database access and persistence |
| Database | MySQL | Relational data storage |
| API Communication | REST | Frontend-backend communication |
| API Testing | Postman | Manual REST API testing |
| Source Control | Git | Version control |
| Repository Hosting | GitHub | Remote source-code hosting |
| Diagramming | draw.io | Architecture and design diagrams |

## 3. Application Architecture

The application follows a three-tier, layered architecture.

### 3.1 Presentation Layer

The presentation layer is implemented using Vue.js.

Responsibilities:

- Display application screens and dashboards.
- Capture user input.
- Perform basic client-side validation.
- Send HTTP requests to backend REST APIs.
- Display API responses, validation messages, and errors.
- Manage frontend navigation and application state where required.

### 3.2 Application Layer

The application layer is implemented using Java Spring Boot.

The backend follows a layered structure:

text
Controller
    ↓
Service
    ↓
Repository
    ↓

Responsibilities:

- Expose REST endpoints.
- Validate incoming requests.
- Apply business rules.
- Enforce authentication and authorization.
- Coordinate database operations.
- Handle exceptions and generate appropriate HTTP responses.
- Maintain action-item status and history.

### 3.3 Data Layer

The data layer uses MySQL as the relational database.

The database stores:

- User information and roles.
- Action items.
- Action-item comments.
- Action-item status history.

Primary keys uniquely identify records, while foreign keys establish relationships between related entities.

The backend accesses the database through Spring Data JPA. Database operations are handled by the repository layer rather than directly by controllers.

The database schema is designed to maintain data consistency and avoid unnecessary duplication.

### 3.4 Request Flow

The communication between the frontend, backend, and database follows this sequence:

1. The user performs an action through the Vue.js frontend.
2. The frontend sends an HTTP request to the appropriate REST API.
3. The Spring Boot controller receives the request.
4. The request data is validated.
5. The service layer applies the required business rules.
6. The repository layer performs the required database operation.
7. MySQL processes the operation and returns the result.
8. The result passes back through the service and controller layers.
9. The backend returns an appropriate HTTP response.
10. The Vue.js frontend displays the result to the user.

text
User
  ↓
Vue.js Frontend
  ↓
REST API Request
  ↓
Spring Boot Controller
  ↓
Service Layer
  ↓
Repository Layer
  ↓
JPA / Hibernate
  ↓
MySQL Database
  ↓
Repository Layer
  ↓
Service Layer
  ↓
Controller
  ↓
REST API Response
  ↓
Vue.js Frontend
  ↓
User

## 4. Security and Authorization

The application supports two user roles:

- `MANAGER`
- `TEAM_MEMBER`

Authorization is applied according to the user's role and permitted access.

### 4.1 Manager Permissions

Managers are intended to be able to:

- Create action items.
- Assign action items to team members.
- Edit action items.
- View action items across the team.
- Close action items.
- Reopen completed action items.
- View action-item history.

### 4.2 Team Member Permissions

Team members are intended to be able to:

- View action items assigned to them.
- Update the progress and status of their assigned action items.
- Add progress comments to their assigned action items.

### 4.3 Authorization Approach

The backend is responsible for enforcing authorization rules.

The frontend may hide or disable controls that a user is not permitted to use, but frontend restrictions are not considered the primary security mechanism.

The backend validates the authenticated user's role and permissions before performing restricted operations.

Database credentials and other sensitive configuration values must not be committed to the Git repository. They should be supplied through environment variables or appropriate environment-specific configuration.

## 5. Validation and Error Handling

The application validates input at appropriate layers.

### 5.1 Frontend Validation

The frontend provides basic validation to improve the user experience, including:

- Required-field validation.
- Valid date selection.
- Accepted priority and status values.
- Meaningful validation messages.

### 5.2 Backend Validation

The backend performs authoritative validation before processing requests.

Examples include:

- Required fields must be present.
- The assignee must be a valid user.
- Priority must contain an accepted value.
- Status must contain an accepted value.
- The due date must satisfy the defined business rules.
- Users must have the required permissions for restricted operations.

### 5.3 Error Handling

The backend returns appropriate HTTP status codes and meaningful error responses.

| HTTP Status | Meaning |
|---|---|
| `200 OK` | Successful request |
| `201 Created` | Resource successfully created |
| `400 Bad Request` | Invalid request or validation failure |
| `401 Unauthorized` | Authentication required or invalid authentication |
| `403 Forbidden` | User is not authorized |
| `404 Not Found` | Requested resource not found |
| `500 Internal Server Error` | Unexpected server-side error |

## 6. Logging and Monitoring

The backend uses application logging to record important application events, warnings, and errors.

Logging supports:

- Troubleshooting application failures.
- Investigating request-processing issues.
- Identifying unexpected application behavior.
- Supporting development and testing.

Sensitive information, including passwords, database credentials, and other confidential values, must not be written to application logs.

A health-check endpoint is provided to check backend availability:

`GET /api/health`

## 7. Testing Strategy

Testing is performed at multiple levels.

### 7.1 Backend Unit Testing

JUnit is used to test important service-layer business logic independently.

Examples include:

- Action-item validation.
- Status-transition rules.
- Authorization-related business rules.

### 7.2 Backend Integration Testing

Integration tests verify interactions between the REST API, application layers, and database where applicable.

Examples include:

- Creating an action item.
- Retrieving an action item.
- Updating an action-item status.
- Adding a comment.
- Retrieving action-item history.

### 7.3 Frontend Testing

Important Vue.js components and user interactions are tested where applicable.

### 7.4 API Testing

Postman is used during development to manually verify REST API requests and responses.

Testing results and supporting evidence are recorded in the project documentation.

## 8. Configuration Management

Application configuration is separated from business logic wherever appropriate.

Environment-specific settings include:

- Database URL.
- Database username.
- Database password.
- Server port.

These values must be configurable without modifying application business logic.

Sensitive configuration values must not be committed to the Git repository.

Sample configuration and setup instructions should be provided so that another developer can reproduce the application locally.

## 9. Database Interaction

The backend uses Spring Data JPA and Hibernate to communicate with MySQL.

Database interaction follows the repository pattern:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA / Hibernate
    ↓
MySQL Database
```

The controller receives requests, the service applies business logic, and the repository performs persistence operations through JPA/Hibernate.

This separation improves code organization, maintainability, and testability.

## 10. Design Considerations and Constraints

- Separation of concerns: Each application layer has clearly defined responsibilities.
- Data integrity: Primary keys, foreign keys, and validation maintain consistent data.
- Security: Backend authorization protects restricted operations.
- Maintainability: Layered components can be updated and tested independently.
- Configuration safety: Sensitive credentials remain outside source control.
- Traceability: Action-item history records support auditing of status changes.
- Testability: Unit, integration, and API tests validate application behavior.
- Reproducibility: Setup instructions and configuration guidance support local development.

## 11. Conclusion

The Team Action Tracker uses a three-tier architecture consisting of a Vue.js frontend, a Java Spring Boot backend, and a MySQL database.

Spring Data JPA manages database persistence, REST APIs support frontend-backend communication, and backend validation and authorization enforce application rules.

The design provides a foundation for maintainable development, reliable data storage, secure access, and traceable action-item management.

Implementation details and testing outcomes should be verified against the actual source code and environment before this document is treated as the final as-built design.