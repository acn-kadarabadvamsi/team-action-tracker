# Team Action Tracker - High-Level Solution Design

## 1. Solution Overview

The Team Action Tracker will be implemented as a web-based three-tier application.

The solution will consist of the following major layers:

1. Presentation Layer - Vue.js frontend responsible for the user interface and user interactions.
2. Application Layer - Java Spring Boot backend responsible for REST APIs, business logic, validation, authorization, and error handling.
3. Data Layer - MySQL database responsible for persistent storage of users, action items, comments, and action-item history.

The frontend will communicate with the backend through REST APIs over HTTP. The backend will process requests, apply business rules, and interact with the MySQL database.

The solution is designed using a layered architecture to keep presentation, business logic, and data-access responsibilities separated. This improves maintainability, testability, and future extensibility.


## 2. Technology Stack

| Layer | Technology | Purpose |
|       |            |         |

| Frontend | Vue.js | User interface and client-side application |

| Frontend Tooling | Vite | Development server and frontend build tooling |

| Backend | Java 21 | Backend programming language |

| Backend Framework | Spring Boot | REST API and application development |

| Data Access | Spring Data JPA | Database access and persistence |

| Database | MySQL | Relational data storage |

| API | REST | Frontend-backend communication |

| API Testing | Postman | REST API testing |

| Source Control | Git | Version control |

| Repository | GitHub | Remote source-code hosting |

| Diagramming | draw.io | Architecture and design diagrams |

## 3. Application Architecture

The application will follow a three-tier layered architecture.

### 3.1 Presentation Layer

The presentation layer will be implemented using Vue.js.

Responsibilities:

- Display application screens and dashboards.
- Capture user input.
- Perform basic client-side validation.
- Send HTTP requests to the backend REST APIs.
- Display API responses, validation messages, and errors.
- Manage frontend navigation and application state where required.

### 3.2 Application Layer

The application layer will be implemented using Java Spring Boot.

The backend will follow a layered structure:

text Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL Database


### 3.3 Data Layer

The data layer will use MySQL as the relational database.

The database will store the core information required by the Team Action Tracker:

- User information and roles
- Action items
- Action-item comments
- Action-item status history

The database will use primary keys to uniquely identify records and foreign keys to establish relationships between related entities.

The backend will access the database through Spring Data JPA. Database operations will be handled by the repository layer rather than directly from controllers.

The database schema will be designed to maintain data consistency and avoid unnecessary duplication of information.

### 3.4 Request Flow

The communication between the frontend, backend, and database will follow this flow:

1. The user performs an action through the Vue.js frontend.
2. The frontend sends an HTTP request to the appropriate REST API.
3. The Spring Boot controller receives the request.
4. The request data is validated.
5. The service layer applies the required business rules.
6. The repository layer performs the required database operation.
7. MySQL returns the requested data or operation result.
8. The result is passed back through the service and controller layers.
9. The backend returns an appropriate HTTP response.
10. The Vue.js frontend displays the result to the user.

Example:

text User
  ↓
Vue.js Frontend
  ↓
REST API Request
  ↓
Spring Boot Controller
  ↓
Service Layer
  ↓
Repository
  ↓
MySQL
  ↓
Repository
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

The application will support two roles:

- Manager
- Team Member

Authorization will be applied according to the user's role.

### Manager Permissions

Managers will be able to:

- Create action items.
- Assign action items to Team Members.
- Edit action items.
- View action items across the team.
- Close action items.
- Reopen completed action items.
- View action-item history.

### Team Member Permissions

Team Members will be able to:

- View action items assigned to them.
- Update the progress/status of their assigned action items.
- Add progress comments to their assigned action items.

### Authorization Approach

The backend will be responsible for enforcing authorization rules. The frontend may hide or disable controls that a user is not permitted to use, but frontend restrictions will not be treated as the primary security mechanism.

The backend will validate the authenticated user's role before performing restricted operations.

Database credentials and other sensitive configuration values will not be committed to the Git repository. They will be supplied through application configuration or environment-specific settings.