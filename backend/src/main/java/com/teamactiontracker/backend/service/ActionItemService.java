
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
import java.util.Locale;
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

    @Transactional(readOnly = true)
    public List<ActionItem> getAllActionItems() {
        User currentUser = getCurrentUser();
        String role = normalizedRole(currentUser);

        if ("MANAGER".equals(role)) {
            return actionItemRepository.findAll();
        }

        if ("TEAM_MEMBER".equals(role)) {
            return actionItemRepository.findByAssignee_UserId(
                    currentUser.getUserId());
        }

        throw new AccessDeniedException(
                "You do not have permission to view action items");
    }

    @Transactional(readOnly = true)
    public ActionItem getActionItemById(Long id) {
        User currentUser = getCurrentUser();

        ActionItem actionItem = actionItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found"));

        authorizeForActionItem(currentUser, actionItem);
        return actionItem;
    }

    @Transactional
    public ActionItem createActionItem(
            CreateActionItemRequest request,
            Authentication authentication) {

        User createdBy = getCurrentUser(authentication);

        if (!"MANAGER".equals(normalizedRole(createdBy))) {
            throw new AccessDeniedException(
                    "Only Managers can create action items");
        }

        validator.validate(request);

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Assignee not found"));

        if (!Boolean.TRUE.equals(assignee.getActiveStatus())) {
            throw new IllegalArgumentException(
                    "Cannot assign an action item to an inactive user");
        }

        ActionItem actionItem = new ActionItem();
        actionItem.setTitle(request.getTitle().trim());
        actionItem.setDescription(request.getDescription());
        actionItem.setAssignee(assignee);
        actionItem.setPriority(request.getPriority());
        actionItem.setStatus(
                request.getStatus() == null
                        ? "NOT_STARTED"
                        : request.getStatus()
        );
        actionItem.setDueDate(request.getDueDate());

        // Use the authenticated manager, not the client-supplied createdBy ID.
        actionItem.setCreatedBy(createdBy);

        return actionItemRepository.save(actionItem);
    }

    @Transactional
    public ActionItem updateActionItem(
            Long actionItemId,
            CreateActionItemRequest request) {

        User currentUser = getCurrentUser();

        if (!"MANAGER".equals(normalizedRole(currentUser))) {
            throw new AccessDeniedException(
                    "Only managers can edit action items");
        }

        validator.validate(request);

        ActionItem item = actionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Action item not found: " + actionItemId));

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Assignee not found: " + request.getAssigneeId()));

        if (!Boolean.TRUE.equals(assignee.getActiveStatus())) {
            throw new IllegalArgumentException("Assignee is inactive");
        }

        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription());
        item.setAssignee(assignee);
        item.setPriority(request.getPriority());
        item.setDueDate(request.getDueDate());

        // Status changes must use updateStatus() to preserve action history.
        if (request.getStatus() != null
                && !request.getStatus().equals(item.getStatus())) {
            throw new IllegalArgumentException(
                    "Use the status update endpoint to change action status");
        }

        return actionItemRepository.save(item);
    }
    @Transactional
    public ActionItem updateStatus(
            Long actionItemId,
            UpdateActionItemStatusRequest request) {

        if (request == null
                || request.getStatus() == null
                || !ALLOWED_STATUSES.contains(request.getStatus())) {

            throw new IllegalArgumentException(
                    "Invalid action item status");
        }

        User updatedBy = getCurrentUser();
        ActionItem actionItem = actionItemRepository
                .findById(actionItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found"));

        authorizeForActionItem(updatedBy, actionItem);

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
        User currentUser = getCurrentUser();

        ActionItem actionItem = actionItemRepository.findById(actionItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found"));

        authorizeForActionItem(currentUser, actionItem);

        return actionHistoryRepository
                .findByActionItem_ActionItemIdOrderByUpdatedAtDesc(
                        actionItemId);
    }

    private User getCurrentUser() {
        return getCurrentUser(
                SecurityContextHolder.getContext().getAuthentication());
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {

            throw new AccessDeniedException(
                    "Authentication is required");
        }

        User user = userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Authenticated user was not found"));

        if (!Boolean.TRUE.equals(user.getActiveStatus())) {
            throw new AccessDeniedException(
                    "This user account is inactive");
        }

        return user;
    }

    private void authorizeForActionItem(
            User user,
            ActionItem actionItem) {

        String role = normalizedRole(user);

        if ("MANAGER".equals(role)) {
            return;
        }

        if ("TEAM_MEMBER".equals(role)) {
            User assignee = actionItem.getAssignee();

            if (assignee != null
                    && assignee.getUserId() != null
                    && assignee.getUserId().equals(user.getUserId())) {
                return;
            }

            throw new AccessDeniedException(
                    "Team Members can access only action items assigned to them");
        }

        throw new AccessDeniedException(
                "You do not have permission to access this action item");
    }

    private String normalizedRole(User user) {
        return user.getRole() == null
                ? ""
                : user.getRole().trim().toUpperCase(Locale.ROOT);
    }
}
