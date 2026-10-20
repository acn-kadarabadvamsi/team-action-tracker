package com.teamactiontracker.backend.dto;

import com.teamactiontracker.backend.model.ActionComment;

import java.time.LocalDateTime;

public record ActionCommentResponse(
        Long commentId,
        String comment,
        LocalDateTime createdAt,
        ActionItemSummary actionItem,
        UserSummary user) {

    public static ActionCommentResponse from(
            ActionComment comment) {

        return new ActionCommentResponse(
                comment.getCommentId(),
                comment.getComment(),
                comment.getCreatedAt(),
                new ActionItemSummary(
                        comment.getActionItem().getActionItemId(),
                        comment.getActionItem().getTitle()),
                new UserSummary(
                        comment.getUser().getUserId(),
                        comment.getUser().getName())
        );
    }

    public record ActionItemSummary(
            Long actionItemId,
            String title) {
    }

    public record UserSummary(
            Long userId,
            String name) {
    }
}