package com.teamactiontracker.backend.controller;

import com.teamactiontracker.backend.model.ActionItem;
import com.teamactiontracker.backend.service.ActionItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/action-items")
public class ActionItemController {

    private final ActionItemService actionItemService;

    public ActionItemController(ActionItemService actionItemService) {
        this.actionItemService = actionItemService;
    }

    @GetMapping
    public List<ActionItem> getAllActionItems() {
        return actionItemService.getAllActionItems();
    }

    @GetMapping("/{id}")
    public ActionItem getActionItemById(@PathVariable Long id) {
        return actionItemService.getActionItemById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActionItem createActionItem(@RequestBody ActionItem actionItem) {
        return actionItemService.createActionItem(actionItem);
    }

    @PutMapping("/{id}")
    public ActionItem updateActionItem(
            @PathVariable Long id,
            @RequestBody ActionItem actionItem) {
        return actionItemService.updateActionItem(id, actionItem);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActionItem(@PathVariable Long id) {
        actionItemService.deleteActionItem(id);
    }
}