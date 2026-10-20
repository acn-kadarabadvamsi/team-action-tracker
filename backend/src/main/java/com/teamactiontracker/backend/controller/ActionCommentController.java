
package com.teamactiontracker.backend.controller;

import com.teamactiontracker.backend.dto.ActionCommentResponse;
import com.teamactiontracker.backend.dto.CreateActionCommentRequest;
import com.teamactiontracker.backend.service.ActionCommentService;

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
@RequestMapping("/api/comments")
@Tag(
    name = "Comments",
    description = "View and add comments to team action items"
)
public class ActionCommentController {

    private final ActionCommentService actionCommentService;

    public ActionCommentController(
            ActionCommentService actionCommentService) {
        this.actionCommentService = actionCommentService;
    }

    @GetMapping
    @Operation(
        summary = "Get comments for an action item",
        description = "Returns comments for the specified action item, subject to the current user's access permissions."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comments retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Missing or invalid action item ID"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "User is not authorized to view these comments"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Action item not found"
        )
    })
    public List<ActionCommentResponse> getComments(
            @Parameter(
                description = "ID of the action item whose comments should be retrieved",
                example = "1",
                required = true
            )
            @RequestParam Long actionItemId,
            Authentication authentication) {

        return actionCommentService.getComments(
                actionItemId, authentication);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Add a comment",
        description = """
            Adds a comment to an action item.
            The authenticated user's identity is obtained from the session.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Comment created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or comment validation failure"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "User is not authorized to comment on this action item"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Action item not found"
        )
    })
    public ActionCommentResponse addComment(
            @Valid @RequestBody CreateActionCommentRequest request,
            Authentication authentication) {

        return actionCommentService.addComment(
                request, authentication);
    }
}
