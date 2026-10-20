# Team Action Tracker — API Specification

## 1. Overview

The Team Action Tracker backend exposes REST APIs for authentication, user management, action-item management, comments, status updates, and action history.

**Base URL:** `http://localhost:8080`

**API prefix:** `/api`

**API documentation:**
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 2. Authentication and Authorization

Authentication uses a server-side session. Clients must retain the session cookie after login.

The application supports two roles:

- **MANAGER:** Can manage users and create action items; can access action-item and comment functionality subject to the configured rules.
- **TEAM_MEMBER:** Can access permitted action-item, status-update, history, and comment functionality.

CSRF protection is enabled. Clients must obtain a CSRF token and send it with requests that require CSRF validation.

## 3. Authentication Endpoints

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| POST | `/api/auth/login` | Authenticate a user | Public |
| GET | `/api/auth/csrf` | Obtain a CSRF token | Public |

## 4. Health Endpoint

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| GET | `/api/health` | Check backend health | Public |

## 5. User Management

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| GET | `/api/users` | Retrieve active users | MANAGER |
| POST | `/api/users` | Create a user | MANAGER |

## 6. Action-Item Management

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| GET | `/api/action-items` | Retrieve action items | Authenticated user |
| GET | `/api/action-items/{id}` | Retrieve one action item | Authenticated user |
| POST | `/api/action-items` | Create an action item | MANAGER |
| PATCH | `/api/action-items/{id}/status` | Update action-item status | MANAGER or TEAM_MEMBER, subject to service authorization |
| GET | `/api/action-items/{id}/history` | Retrieve action-item history | Authenticated user, subject to service authorization |

### Action-item request and response

Action-item responses include the item ID, title, description, assignee, priority, status, due date, creator, creation and update timestamps, and completion timestamp.

The create request uses `CreateActionItemRequest`. The status-update request uses `UpdateActionItemStatusRequest`.

Refer to Swagger UI for the exact request schemas and required fields.

## 7. Comments

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| GET | `/api/comments?actionItemId={id}` | Retrieve comments for an action item | Authenticated user, subject to service authorization |
| POST | `/api/comments` | Add a comment to an action item | Authenticated user, subject to service authorization |

Comment creation uses `CreateActionCommentRequest`, containing the action-item ID and comment text. The authenticated user's identity is derived from the session.

## 8. Validation and Error Handling

Requests are validated using Jakarta Bean Validation where configured.

The application includes centralized exception handling. Responses for invalid requests, missing resources, authentication failures, and authorization failures should be checked against the actual API behavior.

## 9. Database

The application uses MySQL. JPA entities represent users, action items, comments, and action history.

Flyway is enabled with a baseline configuration. The original database schema has not yet been fully captured in versioned migration scripts.

## 10. Testing

Automated tests cover selected comment validation, comment service behavior, and action-item validation scenarios. Application-context startup is also tested.

Additional API integration and authorization coverage remains to be verified.

## 11. Known Limitations and Verification Items

- Confirm dashboard metrics against the PIP requirements.
- Verify search, filtering, sorting, and pagination.
- Verify action-item editing and reassignment.
- Complete and verify versioned database migrations.
- Expand automated API integration tests.
- Confirm all documented response codes and request schemas against Swagger and runtime behavior.
