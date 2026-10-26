# Team Action Tracker

A full-stack application for managing team action items, assigning responsibilities, tracking priorities and statuses, and maintaining comments and activity history.

## Technology Stack

- **Backend:** Java 21, Spring Boot, Maven, Spring Data JPA, Spring Security
- **Frontend:** Vue 3, JavaScript, Vite
- **Database:** MySQL 8
- **Migrations:** Flyway
- **Testing:** JUnit, Vitest, Vue Test Utils, Playwright
- **Version control:** Git and GitHub

## Project Structure

```text
Team Action Tracker/
├── backend/                 # Spring Boot REST API
├── frontend/                # Vue/Vite application
│   ├── e2e/                 # Playwright tests
│   └── src/                 # Vue source and unit tests
├── database/migrations/     # Database migration notes
├── docs/                    # Requirements, design, API and progress
└── README.md
```

## Prerequisites

- Java 21
- MySQL 8
- Node.js and npm
- Git

## Database Setup

1. Start MySQL.
2. Create the database if it does not already exist:

   ```sql
   CREATE DATABASE team_action_tracker;
   ```

3. Set the `DB_PASSWORD` environment variable to your local MySQL password.
4. Check `backend/src/main/resources/application.properties` for the JDBC URL and username.
5. Flyway migrations are located in `backend/src/main/resources/db/migration/`.

Do not drop or recreate a database containing data you need to preserve.

## Run the Backend

Open PowerShell:

```powershell
cd "C:\Team Action Tracker\backend"
$env:JAVA_HOME="C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
.\mvnw.cmd spring-boot:run
```

Adjust `JAVA_HOME` if Java is installed elsewhere. Replace `YOUR_MYSQL_PASSWORD` with your actual local password. Never commit real credentials.

The backend normally runs at `http://localhost:8080`.

## Run the Frontend

Open a separate PowerShell terminal:

```powershell
cd "C:\Team Action Tracker\frontend"
npm install
npm run dev
```

Open the local URL printed by Vite, normally `http://localhost:5173`.

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

The API is served under `/api`. Authentication and authorization may be required.

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

Swagger UI, if enabled, is normally available at `http://localhost:8080/swagger-ui/index.html`.

## Testing and Production Build

Run these commands from the frontend directory.

### Unit tests

```powershell
npm test
```

### End-to-end tests

Install Chromium if needed:

```powershell
npx playwright install chromium
```

Run the E2E tests:

```powershell
npm run test:e2e
```

The current E2E tests check the login screen and invalid-login behavior. The invalid-login test requires the backend to be running.

### Production build

```powershell
npm run build
```

## Documentation

Requirements, high-level design, database design, API specifications and progress notes are maintained in `docs/`.

## Security Notes

- Keep passwords and other secrets out of source control.
- Do not commit `node_modules`, generated build output or generated test reports.
- Use test accounts and non-production data when validating workflows.
