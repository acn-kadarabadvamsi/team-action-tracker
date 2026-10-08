## 1. Project Overview
The Team Action Tracker is a web-based application designed to help project teams create, assign, update, and monitor action items arising from meetings.
The application supports two user roles: Manager and Team Member. Managers can create and manage action items, while Team Members can view their assigned tasks and update their progress.
The application consists of a Vue.js frontend, a Java Spring Boot REST backend, and a MySQL relational database.

## 2. Functional Requirements
### 2.1 User Management
- The system shall support two roles: Manager and Team Member.
- Team Members shall be able to view action items assigned to them.
- Team Members shall be able to update the progress of their assigned action items.
- Managers shall be able to create action items.
- Managers shall be able to assign action items to Team Members.
- Managers shall be able to edit action items.
- Managers shall be able to close and reopen action items.
- Authentication may use predefined test users for this bounded exercise.

### 2.2 Action-Item Management
- The system shall allow Managers to create an action item and assign it to a Team Member.
- Each action item shall have a title, description, assignee, priority, status, and due date.
- Users shall be able to add progress comments to action items.
- Authorized users shall be able to edit permitted fields.
- Managers shall be able to close or reopen action items.
- The system shall maintain an update history for action items.
- The supported statuses shall be:
 - Not Started
 - In Progress
 - Blocked
 - Completed

### 2.3 Dashboard
The dashboard shall display:
- Total action items
- Open action items
- Completed action items
- Overdue action items
- Action items grouped by status
- Action items assigned to the signed-in user

### 2.4 Search and Filtering
The system shall support filtering by:
- Assignee
- Status
- Priority
- Due-date range
The system shall also support sorting and pagination.

### 2.5 Validation and Business Rules
- Title, assignee, priority, and due date shall be mandatory.
- A due date shall not be earlier than the creation date.
- Only authorized users shall be allowed to create or reassign action items.
- A completion date shall be recorded when an action item is completed.
- Every status change shall create a history record.
- Invalid requests shall return meaningful validation messages.

## 3. Non-Functional Requirements
### 3.1 Security
- The application shall restrict functionality based on the user's role.
- Only authorized users shall be allowed to create or reassign action items.
- Users shall only be able to perform actions permitted for their role.
- Sensitive configuration such as database credentials shall not be committed to source control.

### 3.2 Performance
- The application should provide responsive user interactions under normal usage.
- API requests should return within a reasonable time under the expected development workload.
- Database queries should retrieve only the data required by the requested operation.

### 3.3 Reliability
- The application shall handle invalid requests without crashing.
- Meaningful error responses shall be returned when an operation cannot be completed.
- Database operations shall maintain data consistency.

### 3.4 Maintainability
- The backend shall follow a layered architecture consisting of controllers, services, and data-access components.
- Frontend components shall be organized according to their responsibilities.
- The codebase shall follow consistent naming and coding conventions.
- Configuration shall be separated from application logic where appropriate.

### 3.5 Testability
- Backend business logic shall be covered by unit tests where applicable.
- REST APIs shall have integration test coverage for important flows.
- Important frontend components shall be testable.
- Tests shall be executable using documented project commands.

### 3.6 Usability
- The user interface shall provide clear navigation and understandable labels.
- Validation errors shall be presented in a meaningful manner.
- Task status, priority, due date, and assignee shall be clearly visible to users.

### 3.7 Logging and Monitoring
- The backend shall log important application events and errors.
- Logs shall provide sufficient information to investigate failures without exposing sensitive information.
- Application health shall be verifiable through a health-check endpoint.