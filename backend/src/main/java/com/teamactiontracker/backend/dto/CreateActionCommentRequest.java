package com.teamactiontracker.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreateActionCommentRequest {

    @NotNull(message = "Action item ID is required")
    @Positive(message = "Action item ID must be positive")
    private Long actionItemId;

    @NotBlank(message = "Comment cannot be blank")
    @Size(
        max = 5000,
        message = "Comment must be 5000 characters or fewer"
    )
    private String comment;

    public Long getActionItemId() {
        return actionItemId;
    }

    public void setActionItemId(Long actionItemId) {
        this.actionItemId = actionItemId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}