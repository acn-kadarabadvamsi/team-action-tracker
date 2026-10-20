package com.teamactiontracker.backend.service;

import com.teamactiontracker.backend.dto.ActionCommentResponse;
import com.teamactiontracker.backend.dto.CreateActionCommentRequest;
import com.teamactiontracker.backend.model.ActionComment;
import com.teamactiontracker.backend.model.ActionItem;
import com.teamactiontracker.backend.model.User;
import com.teamactiontracker.backend.repository.ActionCommentRepository;
import com.teamactiontracker.backend.repository.ActionItemRepository;
import com.teamactiontracker.backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ActionCommentService {

    private final ActionCommentRepository actionCommentRepository;
    private final ActionItemRepository actionItemRepository;
    private final UserRepository userRepository;

    public ActionCommentService(
            ActionCommentRepository actionCommentRepository,
            ActionItemRepository actionItemRepository,
            UserRepository userRepository) {

        this.actionCommentRepository = actionCommentRepository;
        this.actionItemRepository = actionItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ActionCommentResponse> getComments(
            Long actionItemId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        ActionItem actionItem = actionItemRepository
                .findById(actionItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found"));

        authorizeForActionItem(currentUser, actionItem);

        return actionCommentRepository
                .findByActionItem_ActionItemIdOrderByCreatedAtAsc(
                        actionItemId)
                .stream()
                .map(ActionCommentResponse::from)
                .toList();
    }

    @Transactional
    public ActionCommentResponse addComment(
            CreateActionCommentRequest request,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        ActionItem actionItem = actionItemRepository
                .findById(request.getActionItemId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found"));

        authorizeForActionItem(currentUser, actionItem);

        ActionComment comment = new ActionComment();
        comment.setActionItem(actionItem);
        comment.setUser(currentUser);
        comment.setComment(request.getComment().trim());

        ActionComment savedComment =
                actionCommentRepository.save(comment);

        return ActionCommentResponse.from(savedComment);
    }

    private User getCurrentUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null) {

            throw new AccessDeniedException(
                    "Authentication is required");
        }

        String email =
                authentication.getPrincipal().toString();

        User user = userRepository
                .findByEmailIgnoreCase(email)
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

        // Managers can access comments on all action items.
        if ("MANAGER".equalsIgnoreCase(user.getRole())) {
            return;
        }

        // Team Members can access comments only on tasks
        // assigned to them.
        boolean isAssignedTeamMember =
                "TEAM_MEMBER".equalsIgnoreCase(user.getRole())
                && actionItem.getAssignee() != null
                && Objects.equals(
                        actionItem.getAssignee().getUserId(),
                        user.getUserId());

        if (!isAssignedTeamMember) {
            throw new AccessDeniedException(
                    "You are not authorized to access comments "
                    + "for this action item");
        }
    }
}