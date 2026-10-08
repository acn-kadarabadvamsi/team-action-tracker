# Team Action Tracker – API Specification

## 1. API Overview

The Team Action Tracker backend exposes REST APIs for managing users, action items, comments, status updates, and action history.

The APIs are implemented using Spring Boot and follow standard HTTP methods and response status codes.

### Base URL

text
/api

## 2. API Endpoints

### 2.1 Health Check

| Method | Endpoint | Purpose | Access |
|        |          |         |        |

| GET | `/api/health` | Check whether the backend is running | Public |

**Response:**

json
{
  "status": "UP",
  "message": "Team Action Tracker backend is running"
}

## 5. Action Item Request

### Create Action Item

**Endpoint:**

text
POST /api/action-items