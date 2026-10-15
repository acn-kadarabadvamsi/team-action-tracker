**Team Action Tracker**



**Project Overview**



Team Action Tracker is a full-stack application designed to manage team action items, assign responsibilities, track priorities and statuses, and maintain comments and activity history.



**Technology Stack**

* Backend: Java 21, Spring Boot, Maven, Spring Data JPA
* Frontend: Vue.js, Vite, JavaScript
* Database: MySQL 8
* Version Control: Git and GitHub



**Project Structure**



Team Action Tracker/

├── backend/                 # Spring Boot backend

├── frontend/                # Vue.js frontend

├── docs/

│   ├── requirements.md

│   ├── high level design.md

│   ├── database design.md

│   ├── api specification.md

│   └── daily progress.md

└── README.md



**Prerequisites**

* Java 21
* MySQL 8
* Node.js and npm
* Git



**Database Setup**

* Start the MySQL service.
* Create the database:



CREATE DATABASE team\_action\_tracker;



Configure the database password using the DB\_PASSWORD environment variable. The backend reads its database connection settings from backend/src/main/resources/application.properties.



**Run the Backend**



Open PowerShell:



cd "C:\\Team Action Tracker\\backend"

$env:JAVA\_HOME="C:\\Program Files\\Microsoft\\jdk-21.0.10.7-hotspot"

$env:Path="$env:JAVA\_HOME\\bin;$env:Path"

$env:DB\_PASSWORD="YOUR\_MYSQL\_PASSWORD"

.\\mvnw.cmd spring-boot:run



Replace YOUR\_MYSQL\_PASSWORD with your actual local MySQL password. Do not commit passwords to Git.



Run the Frontend



Open a separate terminal:



cd "C:\\Team Action Tracker\\frontend"

npm install

npm run dev



Open the local URL printed by Vite, normally http://localhost:5173/.



**Current API Endpoints**



|Method|Endpoint|Purpose|
|-|-|-|
|GET|/api/health|Check backend health|
|GET|/api/action-items|Retrieve action items|
|GET|/api/action-items/{id}|Retrieve one action item|
|POST|/api/action-items|Create an action item|







**Documentation**

Project requirements, high-level design, database design, API specifications, diagrams, and daily progress are maintained in the docs/ directory.



**Current Development Status**

The initial project setup, database configuration, core action-item entities, initial APIs, and frontend scaffold have been developed. Further work is required to complete and verify frontend-backend integration and any additional functionality.



Refer to docs/daily progress.md for the recorded progress and pending tasks.

