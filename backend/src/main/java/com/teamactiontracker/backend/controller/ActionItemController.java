
package com.teamactiontracker.backend.controller;

import com.teamactiontracker.backend.dto.ActionHistoryResponse;
import com.teamactiontracker.backend.dto.ActionItemResponse;
import com.teamactiontracker.backend.dto.CreateActionItemRequest;
import com.teamactiontracker.backend.dto.UpdateActionItemStatusRequest;
import com.teamactiontracker.backend.model.ActionHistory;
import com.teamactiontracker.backend.model.ActionItem;
import com.teamactiontracker.backend.service.ActionItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/action-items")
@Tag(
    name = "Action Items",
    description = "Create, retrieve, update and track team action items"
)
public class ActionItemController {

    private final ActionItemService actionItemService;

    public ActionItemController(ActionItemService actionItemService) {
        this.actionItemService = actionItemService;
    }

    @GetMapping
    @Operation(
        summary = "Get all action items",
        description = "Retrieves the action items available through the current user's access."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Action items retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public List<ActionItemResponse> getAllActionItems() {
        return actionItemService.getAllActionItems()
                .stream()
                .map(ActionItemResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get an action item by ID",
        description = "Retrieves a specific action item."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Action item retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Action item not found")
    })
    public ActionItemResponse getActionItemById(
            @Parameter(description = "ID of the action item", example = "1")
            @PathVariable Long id) {

        ActionItem item = actionItemService.getActionItemById(id);

        return ActionItemResponse.from(item);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create an action item",
        description = "Creates a new action item using the authenticated manager's identity."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Action item created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Only authorized users may create action items")
    })
    public ActionItemResponse createActionItem(
            @Valid @RequestBody CreateActionItemRequest request,
            Authentication authentication) {

        ActionItem item =
                actionItemService.createActionItem(request, authentication);

        return ActionItemResponse.from(item);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update action item status",
        description = """
            Updates the status of an existing action item.
            The service handles status validation and history recording.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status or request data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Action item not found")
    })
    public ActionItemResponse updateStatus(
            @Parameter(description = "ID of the action item", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateActionItemStatusRequest request) {

        ActionItem item = actionItemService.updateStatus(id, request);

        return ActionItemResponse.from(item);
    }

    @GetMapping("/{id}/history")
    @Operation(
        summary = "Get action item history",
        description = "Retrieves the history records associated with an action item."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Action item not found")
    })
    public List<ActionHistoryResponse> getActionItemHistory(
            @Parameter(description = "ID of the action item", example = "1")
            @PathVariable Long id) {

        List<ActionHistory> history =
                actionItemService.getActionItemHistory(id);

        return history.stream()
                .map(ActionHistoryResponse::from)
                .toList();
    }
}
