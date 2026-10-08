package com.teamactiontracker.backend.service;

import com.teamactiontracker.backend.dto.CreateActionItemRequest;
import com.teamactiontracker.backend.model.ActionItem;
import com.teamactiontracker.backend.model.User;
import com.teamactiontracker.backend.repository.ActionItemRepository;
import com.teamactiontracker.backend.repository.UserRepository;
import com.teamactiontracker.backend.validation.ActionItemValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionItemService {

    private final ActionItemRepository actionItemRepository;
    private final UserRepository userRepository;
    private final ActionItemValidator validator;

    public ActionItemService(
            ActionItemRepository actionItemRepository,
            UserRepository userRepository,
            ActionItemValidator validator) {
        this.actionItemRepository = actionItemRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public List<ActionItem> getAllActionItems() {
        return actionItemRepository.findAll();
    }

    public ActionItem getActionItemById(Long id) {
        return actionItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action item not found"));
    }

    public ActionItem createActionItem(CreateActionItemRequest request) {

        validator.validate(request);

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new RuntimeException("Assignee not found"));

        User createdBy = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Creator not found"));

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
}