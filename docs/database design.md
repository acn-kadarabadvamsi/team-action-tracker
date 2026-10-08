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

## 2. Action Items Table

The `action_items` table stores the tasks or actions that need to be completed by team members.

| Column | Data Type | Constraints | Description |
|        |           |             |             |

| action_item_id | BIGINT | Primary Key, Auto Increment | Unique identifier for the action item |
| title | VARCHAR(200) | NOT NULL | Short title of the action item |
| description | TEXT | NULL | Detailed description of the action item |
| assignee_id | BIGINT | NOT NULL, Foreign Key | User responsible for completing the action item |
| priority | VARCHAR(20) | NOT NULL | Priority of the action item |
| status | VARCHAR(30) | NOT NULL | Current status of the action item |
| due_date | DATE | NOT NULL | Date by which the action item should be completed |
| created_by | BIGINT | NOT NULL, Foreign Key | User who created the action item |
| created_at | TIMESTAMP | NOT NULL | Date and time when the action item was created |
| updated_at | TIMESTAMP | NOT NULL | Date and time when the action item was last updated |
| completed_at | TIMESTAMP | NULL | Date and time when the action item was completed |

### Priority Values

The following priority values will be supported:

- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

### Status Values

The following status values will be supported:

- `NOT_STARTED`
- `IN_PROGRESS`
- `BLOCKED`
- `COMPLETED`

### Relationships

- `assignee_id` references `users.user_id`.
- `created_by` references `users.user_id`.

A user may be assigned multiple action items.

A user may also create multiple action items.

### Business Rules

- An action item must have an assignee.
- An action item must have a due date.
- The due date must not be earlier than the creation date.
- Only authorized users can create or reassign action items.
- When an action item changes to `COMPLETED`, `completed_at` shall be populated.
- When a completed action item is reopened, `completed_at` shall be cleared.

## 3. Action Comments Table

The `action_comments` table stores progress updates and comments added to action items by users.

| Column | Data Type | Constraints | Description |
|        |           |             |             |

| comment_id | BIGINT | Primary Key, Auto Increment | Unique identifier for the comment |
| action_item_id | BIGINT | NOT NULL, Foreign Key | Action item associated with the comment |
| user_id | BIGINT | NOT NULL, Foreign Key | User who added the comment |
| comment | TEXT | NOT NULL | Content of the progress comment |
| created_at | TIMESTAMP | NOT NULL | Date and time when the comment was added |
| updated_at | TIMESTAMP | NOT NULL | Date and time when the comment was last updated |

### Relationships

- `action_item_id` references `action_items.action_item_id`.
- `user_id` references `users.user_id`.

One action item can have multiple comments.

One user can add comments to multiple action items.

### Business Rules

- A comment must be associated with an existing action item.
- A comment must have a non-empty message.
- The user adding the comment must be an authenticated user.
- Users may only add comments according to their permitted access to the associated action item.

## 4. Action History Table

The `action_history` table maintains an audit trail of important status changes made to action items.

| Column | Data Type | Constraints | Description |
|        |           |             |             |

| history_id | BIGINT | Primary Key, Auto Increment | Unique identifier for the history record |
| action_item_id | BIGINT | NOT NULL, Foreign Key | Action item associated with the history entry |
| previous_status | VARCHAR(30) | NULL | Status before the change |
| new_status | VARCHAR(30) | NOT NULL | Status after the change |
| comment | TEXT | NULL | Optional explanation for the change |
| updated_by | BIGINT | NOT NULL, Foreign Key | User who made the status change |
| updated_at | TIMESTAMP | NOT NULL | Date and time of the status change |

### Relationships

- `action_item_id` references `action_items.action_item_id`.
- `updated_by` references `users.user_id`.

One action item can have multiple history records.

One user can create multiple history records.

### Business Rules

- A history record shall be created whenever an action item's status changes.
- The history record shall identify the previous and new status.
- The history record shall identify the user who performed the change.
- History records should be retained and should not be modified as part of normal task updates.

## 5. Entity Relationships

The primary relationships between the entities are:

text
USERS
  │
  ├─────────────── 1 : Many ──────────────── ACTION_ITEMS
  │                                          │
  │                                          ├── 1 : Many ── ACTION_COMMENTS
  │                                          │
  │                                          └── 1 : Many ── ACTION_HISTORY
  │
  ├── Creates ACTION_ITEMS through created_by
  ├── Assigned to ACTION_ITEMS through assignee_id
  ├── Creates ACTION_COMMENTS through user_id
  └── Creates ACTION_HISTORY through updated_by