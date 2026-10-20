
package com.teamactiontracker.backend.dto;

import com.teamactiontracker.backend.model.ActionItem;
import com.teamactiontracker.backend.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ActionItemResponse(
        Long actionItemId,
        String title,
        String description,
        UserSummary assignee,
        String priority,
        String status,
        LocalDate dueDate,
        UserSummary createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt
) {

    public static ActionItemResponse from(ActionItem item) {
        return new ActionItemResponse(
                item.getActionItemId(),
                item.getTitle(),
                item.getDescription(),
                UserSummary.from(item.getAssignee()),
                item.getPriority(),
                item.getStatus(),
                item.getDueDate(),
                UserSummary.from(item.getCreatedBy()),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                item.getCompletedAt()
        );
    }

    public record UserSummary(
            Long userId,
            String name,
            String email,
            String role
    ) {
        public static UserSummary from(User user) {
            if (user == null) {
                return null;
            }

            return new UserSummary(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            );
        }
    }
}
