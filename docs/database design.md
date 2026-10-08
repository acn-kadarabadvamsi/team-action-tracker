# Team Action Tracker - Database Design

## 1. Users Table

The `users` table stores the users who can access and interact with the Team Action Tracker.

| Column | Data Type | Constraints | Description |
|        |           |             |             |

| user_id | BIGINT | Primary Key, Auto Increment | Unique identifier for the user |
| name | VARCHAR(100) | NOT NULL | User's display name |
| email | VARCHAR(150) | NOT NULL, UNIQUE | User's email address |
| role | VARCHAR(30) | NOT NULL | User role: MANAGER or TEAM_MEMBER |
| active_status | BOOLEAN | NOT NULL | Indicates whether the user is active |
| created_at | TIMESTAMP | NOT NULL | Date and time when the user was created |
| updated_at | TIMESTAMP | NOT NULL | Date and time when the user was last updated |

### User Roles

The application will support two roles:

- `MANAGER`
- `TEAM_MEMBER`

The `user_id` will be referenced by other tables when an action item, comment, or history record is associated with a user.