
package com.teamactiontracker.backend.service;

import com.teamactiontracker.backend.dto.CreateActionItemRequest;
import com.teamactiontracker.backend.dto.UpdateActionItemStatusRequest;
import com.teamactiontracker.backend.model.ActionHistory;
import com.teamactiontracker.backend.model.ActionItem;
import com.teamactiontracker.backend.model.User;
import com.teamactiontracker.backend.repository.ActionHistoryRepository;
import com.teamactiontracker.backend.repository.ActionItemRepository;
import com.teamactiontracker.backend.repository.UserRepository;
import com.teamactiontracker.backend.validation.ActionItemValidator;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ActionItemService {

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "NOT_STARTED",
            "IN_PROGRESS",
            "BLOCKED",
            "COMPLETED"
    );

    private final ActionItemRepository actionItemRepository;
    private final UserRepository userRepository;
    private final ActionHistoryRepository actionHistoryRepository;
    private final ActionItemValidator validator;

    public ActionItemService(
            ActionItemRepository actionItemRepository,
            UserRepository userRepository,
            ActionHistoryRepository actionHistoryRepository,
            ActionItemValidator validator) {

        this.actionItemRepository = actionItemRepository;
        this.userRepository = userRepository;
        this.actionHistoryRepository = actionHistoryRepository;
        this.validator = validator;
    }

    public List<ActionItem> getAllActionItems() {
        return actionItemRepository.findAll();
    }

    public ActionItem getActionItemById(Long id) {
        return actionItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found"));
    }

    public ActionItem createActionItem(CreateActionItemRequest request) {
        validator.validate(request);

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Assignee not found"));

        User createdBy = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() ->
                        new IllegalArgumentException("Creator not found"));

        ActionItem actionItem = new ActionItem();
        actionItem.setTitle(request.getTitle());
        actionItem.setDescription(request.getDescription());
        actionItem.setAssignee(assignee);
        actionItem.setPriority(request.getPriority());
        actionItem.setStatus(
                request.getStatus() == null
                        ? "NOT_STARTED"
                        : request.getStatus()
        );
        actionItem.setDueDate(request.getDueDate());
        actionItem.setCreatedBy(createdBy);

        return actionItemRepository.save(actionItem);
    }

    @Transactional
    public ActionItem updateStatus(
            Long actionItemId,
            UpdateActionItemStatusRequest request) {

        // 1. Validate the requested status.
        if (request == null
                || request.getStatus() == null
                || !ALLOWED_STATUSES.contains(request.getStatus())) {

            throw new IllegalArgumentException(
                    "Invalid action item status");
        }

        // 2. Identify the authenticated user.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {

            throw new AccessDeniedException(
                    "Authentication is required to update status");
        }

        User updatedBy = userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Authenticated user was not found"));

        // 3. Retrieve the action item.
        ActionItem actionItem = actionItemRepository
                .findById(actionItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found"));

        // 4. Enforce role and assignment permissions.
        String role = updatedBy.getRole();

        if ("MANAGER".equals(role)) {
            // Managers can update any action item.
        } else if ("TEAM_MEMBER".equals(role)) {

            User assignee = actionItem.getAssignee();

            if (assignee == null
                    || !assignee.getUserId()
                            .equals(updatedBy.getUserId())) {

                throw new AccessDeniedException(
                        "Team Members can update only action items "
                                + "assigned to them");
            }

        } else {
            throw new AccessDeniedException(
                    "You do not have permission to update action items");
        }

        // 5. Update status and completion date.
        String previousStatus = actionItem.getStatus();
        String newStatus = request.getStatus();

        actionItem.setStatus(newStatus);

        if ("COMPLETED".equals(newStatus)) {

            if (!"COMPLETED".equals(previousStatus)) {
                actionItem.setCompletedAt(LocalDateTime.now());
            }

        } else {
            actionItem.setCompletedAt(null);
        }

        ActionItem savedActionItem =
                actionItemRepository.save(actionItem);

        // 6. Record the status change in action history.
        ActionHistory history = new ActionHistory();
        history.setActionItem(savedActionItem);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setComment(request.getComment());
        history.setUpdatedBy(updatedBy);

        actionHistoryRepository.save(history);

        return savedActionItem;
    }

    @Transactional(readOnly = true)
    public List<ActionHistory> getActionItemHistory(Long actionItemId) {

        if (!actionItemRepository.existsById(actionItemId)) {
            throw new IllegalArgumentException(
                    "Action item not found");
        }

        return actionHistoryRepository
                .findByActionItem_ActionItemIdOrderByUpdatedAtDesc(
                        actionItemId);
    }
}
