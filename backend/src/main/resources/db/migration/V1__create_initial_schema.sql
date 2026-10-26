CREATE TABLE users (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    active_status BIT(1) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    email VARCHAR(150) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    password_hash VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE action_items (
    action_item_id BIGINT NOT NULL AUTO_INCREMENT,
    completed_at DATETIME(6) DEFAULT NULL,
    created_at DATETIME(6) NOT NULL,
    description TEXT,
    due_date DATE NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    assignee_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    PRIMARY KEY (action_item_id),
    KEY idx_action_items_assignee (assignee_id),
    KEY idx_action_items_created_by (created_by),
    CONSTRAINT fk_action_items_assignee
        FOREIGN KEY (assignee_id) REFERENCES users (user_id),
    CONSTRAINT fk_action_items_created_by
        FOREIGN KEY (created_by) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE action_comments (
    comment_id BIGINT NOT NULL AUTO_INCREMENT,
    comment TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    action_item_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (comment_id),
    KEY idx_action_comments_action_item (action_item_id),
    KEY idx_action_comments_user (user_id),
    CONSTRAINT fk_action_comments_action_item
        FOREIGN KEY (action_item_id)
        REFERENCES action_items (action_item_id),
    CONSTRAINT fk_action_comments_user
        FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE action_history (
    history_id BIGINT NOT NULL AUTO_INCREMENT,
    comment TEXT,
    new_status VARCHAR(30) NOT NULL,
    previous_status VARCHAR(30) DEFAULT NULL,
    updated_at DATETIME(6) NOT NULL,
    action_item_id BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    PRIMARY KEY (history_id),
    KEY idx_action_history_action_item (action_item_id),
    KEY idx_action_history_updated_by (updated_by),
    CONSTRAINT fk_action_history_action_item
        FOREIGN KEY (action_item_id)
        REFERENCES action_items (action_item_id),
    CONSTRAINT fk_action_history_updated_by
        FOREIGN KEY (updated_by) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;