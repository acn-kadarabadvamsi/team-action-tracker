package com.teamactiontracker.backend.service;

import com.teamactiontracker.backend.model.ActionItem;
import com.teamactiontracker.backend.repository.ActionItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionItemService {

    private final ActionItemRepository actionItemRepository;

    public ActionItemService(ActionItemRepository actionItemRepository) {
        this.actionItemRepository = actionItemRepository;
    }

    public List<ActionItem> getAllActionItems() {
        return actionItemRepository.findAll();
    }

    public ActionItem getActionItemById(Long id) {
        return actionItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action item not found"));
    }

    public ActionItem createActionItem(ActionItem actionItem) {
        return actionItemRepository.save(actionItem);
    }

    public ActionItem updateActionItem(Long id, ActionItem updatedActionItem) {
        ActionItem existing = getActionItemById(id);

        existing.setTitle(updatedActionItem.getTitle());
        existing.setDescription(updatedActionItem.getDescription());
        existing.setAssignee(updatedActionItem.getAssignee());
        existing.setPriority(updatedActionItem.getPriority());
        existing.setStatus(updatedActionItem.getStatus());
        existing.setDueDate(updatedActionItem.getDueDate());
        existing.setCompletedAt(updatedActionItem.getCompletedAt());

        return actionItemRepository.save(existing);
    }

    public void deleteActionItem(Long id) {
        actionItemRepository.deleteById(id);
    }
}