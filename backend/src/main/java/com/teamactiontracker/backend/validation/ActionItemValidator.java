package com.teamactiontracker.backend.validation;

import com.teamactiontracker.backend.dto.CreateActionItemRequest;
import org.springframework.stereotype.Component;

@Component
public class ActionItemValidator {

    public void validate(CreateActionItemRequest request) {

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (request.getTitle().length() > 200) {
            throw new IllegalArgumentException("Title must not exceed 200 characters");
        }

        if (request.getAssigneeId() == null) {
            throw new IllegalArgumentException("Assignee is required");
        }

        if (request.getCreatedBy() == null) {
            throw new IllegalArgumentException("Created by is required");
        }

        if (request.getPriority() == null ||
                !request.getPriority().matches("LOW|MEDIUM|HIGH|CRITICAL")) {
            throw new IllegalArgumentException(
                    "Priority must be LOW, MEDIUM, HIGH, or CRITICAL");
        }

        if (request.getStatus() != null &&
                !request.getStatus().matches(
                        "NOT_STARTED|IN_PROGRESS|BLOCKED|COMPLETED")) {
            throw new IllegalArgumentException(
                    "Invalid action item status");
        }

        if (request.getDueDate() == null) {
            throw new IllegalArgumentException("Due date is required");
        }
    }
}