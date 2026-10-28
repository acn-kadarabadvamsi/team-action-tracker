# Team Action Tracker

A full-stack application for managing team action items, assigning responsibilities, tracking priorities and statuses, and maintaining comments and activity history.

## Technology Stack

- Backend: Java 21, Spring Boot, Maven, Spring Data JPA, Spring Security
- Frontend: Vue 3, JavaScript, Vite
- Database: MySQL 8
- Migrations: Flyway
- Testing: JUnit, Vitest, Vue Test Utils, Playwright
- Version control: Git and GitHub

## Project Structure

text
Team Action Tracker/
├── backend/                 # Spring Boot REST API
├── backups/                 # Database backups
├── database/
│   └── migrations/          # Database migration notes
├── frontend/
│   ├── e2e/                 # Playwright tests
│   └── src/                 # Vue source and unit tests
├── docs/                    # Requirements, design, API and progress
├── team_action_tracker_backup.sql
├── team_action_tracker_backup_before_mysql_recovery.sql
└── README.md

## Prerequisites

- Java 21
- MySQL 8
- Node.js and npm
- Git

Verify the installations:

powershell
java -version
node --version
npm --version
git --version

## Database Setup

1. Start MySQL.
2. Create the database if it does not already exist:

   sql
   CREATE DATABASE IF NOT EXISTS team_action_tracker;

3. Set the `DB_PASSWORD` environment variable to your local MySQL password.
4. Check `backend/src/main/resources/application.properties` for the JDBC URL and username.
5. Flyway migrations are located in `backend/src/main/resources/db/migration/`.

For PowerShell, set the password in the terminal used to run the backend:

powershell
$env:DB_PASSWORD = Read-Host "Enter your MySQL password"


Do not drop or recreate a database containing data you need to preserve. Keep database backups secure.

## Run the Backend

Open PowerShell:

powershell
cd "C:\Team Action Tracker\backend"
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:DB_PASSWORD = Read-Host "Enter your MySQL password"
.\mvnw.cmd spring-boot:run


Adjust `JAVA_HOME` if Java is installed elsewhere. The password is entered interactively and should never be committed to source control.

The backend normally runs at `http://localhost:8080`.

If the health endpoint is enabled and accessible, verify it at:

`http://localhost:8080/api/health`

Keep the backend terminal open while using the frontend.

## Run the Frontend

Open a separate PowerShell terminal:

powershell
cd "C:\Team Action Tracker\frontend"
npm install
npm run dev


Open the local URL printed by Vite, normally `http://localhost:5173`.

The frontend API base URL is currently configured as:

javascript
const API_BASE = 'http://localhost:8080/api';


Ensure the backend is running and the API URL matches your environment.

## Application Features

The current frontend implementation includes:

- Session-based login and CSRF handling
- Dashboard counts for action statuses and overdue items
- Action-item search, filtering, sorting and pagination
- Status updates
- Action history viewing
- Comments viewing and adding
- Manager-only user and action-item creation

Access depends on the authenticated user's role and backend authorization rules.

## API Endpoints

The API is served under `/api`. Authentication, authorization, and CSRF protection may apply.

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Backend health check |
| GET | `/api/auth/csrf` | Obtain a CSRF token |
| POST | `/api/auth/login` | Sign in |
| GET | `/api/users` | Retrieve users |
| POST | `/api/users` | Create a user (Manager) |
| GET | `/api/action-items` | Retrieve action items |
| GET | `/api/action-items/{id}` | Retrieve one action item |
| POST | `/api/action-items` | Create an action item (Manager) |
| PATCH | `/api/action-items/{id}/status` | Update status |
| GET | `/api/action-items/{id}/history` | Retrieve action history |
| GET | `/api/comments?actionItemId={id}` | Retrieve action comments |
| POST | `/api/comments` | Add a comment |

Confirm the actual controller mappings if an endpoint returns `404 Not Found`.

Swagger UI, if enabled, is normally available at:

`http://localhost:8080/swagger-ui/index.html`

## Testing and Production Build

### Backend tests

Run these commands from the backend directory:

powershell
cd "C:\Team Action Tracker\backend"
.\mvnw.cmd test

**Latest verified results:**

- Tests: 39
- Failures: 0
- Errors: 0

### Frontend unit tests

Run these commands from the frontend directory:

powershell
cd "C:\Team Action Tracker\frontend"
npm test -- --pool=forks --maxWorkers=1

The single-worker forks configuration was verified successfully after the default Vitest worker pool encountered a startup timeout.

**Latest verified results:**

- Tests: 2 passed
- Login screen rendering
- Required-field validation

### End-to-end tests

Ensure the backend is running before executing the tests.

Install Chromium if needed:

powershell
npx playwright install chromium

Run the Playwright tests:

powershell
npm run test:e2e

**Latest verified results:**

- Tests: 2 passed
- Login screen displays required fields.
- Invalid login stays on the login screen.

These tests cover the listed login scenarios. They do not verify the complete action-item workflow.

### Production build

Run the following from the frontend directory:

powershell
npm run build

The production build completed successfully during the latest verification. Vite generates the output in `frontend/dist/`.

## End-to-End Acceptance Checklist

Before declaring the application fully accepted, validate the following workflow using an appropriate test account:

- [ ] Log in with valid credentials.
- [ ] Verify dashboard counts.
- [ ] Create an action item and assign it to a user.
- [ ] Search for the action item.
- [ ] Test available filters, sorting, and pagination.
- [ ] Update the action item's status.
- [ ] Add a comment and view action history.
- [ ] Refresh the page and verify that changes persist.
- [ ] Confirm role-based restrictions.
- [ ] Verify validation messages for invalid input.
- [ ] Check loading, empty, and error states.

Compare the displayed data with the database where practical. Record the results of these checks before declaring full acceptance.

## Documentation

Requirements, high-level design, database design, API specifications, and progress notes are maintained in `docs/`.

Keep these documents aligned with the current implementation and verified behavior.

## Security Notes

- Keep passwords, tokens, and other secrets out of source control.
- Do not commit `.env` files containing credentials.
- Do not expose database credentials in frontend code.
- Use authorized test accounts and non-production data during validation.
- Enforce authorization on the backend, not only in the frontend.
- Preserve CSRF protection for applicable session-authenticated requests.
- Protect database backups because they may contain sensitive information.
- Use appropriately restricted database accounts in production.
- Review database migration and Hibernate schema-update settings before production deployment.
- Do not commit `node_modules`, generated build output, or generated test reports unless explicitly required.

## Known Limitations

- The current automated end-to-end tests cover login-screen rendering and invalid-login behavior.
- The complete workflow involving action creation, assignment, status updates, comments, history, and persistence still requires acceptance testing.
- Role restrictions and all search, filtering, sorting, and pagination scenarios require functional validation beyond the currently verified tests.
- The frontend unit-test suite contains only two tests, so broader component coverage remains desirable.
- Swagger/OpenAPI availability depends on the backend configuration.
- The application uses local development configuration and requires appropriate security and deployment review before production use.

## Troubleshooting

### MySQL access denied

If the backend reports `Access denied for user 'root'@'localhost'`, verify that the password entered for `DB_PASSWORD` is correct.

Test the credentials using the MySQL client:

powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p

If the password is accepted, enter the same password when setting `DB_PASSWORD` in the backend terminal.

### Backend cannot connect to MySQL

Check that:

- The MySQL service is running.
- The `team_action_tracker` database exists.
- The JDBC URL and username are correct.
- `DB_PASSWORD` is set in the backend terminal.
- The configured MySQL port is correct.

### Frontend cannot reach the backend

Check that:

- The backend is running on port `8080`.
- The frontend API base URL is correct.
- The browser developer console and Network panel show no unexpected errors.
- Authentication cookies and CSRF tokens are handled correctly.

A `401 Unauthorized` or `403 Forbidden` response may indicate an authentication, authorization, or CSRF issue rather than a connectivity failure.

### Vitest worker startup timeout

If the default frontend test command times out while starting a worker, run:

powershell
npm test -- --pool=forks --maxWorkers=1

This configuration passed during the latest verification.

### Port already in use

If a port is occupied, identify the process using it before stopping anything. Avoid terminating unrelated processes.

## Project Status

| Verification | Latest result |
|---|---|
| Backend tests | 39 tests; 0 failures; 0 errors |
| Frontend unit tests | 2 passed |
| Playwright end-to-end tests | 2 passed |
| Frontend production build | Successful |
| Full workflow acceptance | Pending confirmation |

The project should be considered fully accepted after the remaining functional and role-based acceptance checks have been completed and documented.