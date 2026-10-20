
package com.teamactiontracker.backend.controller;

import com.teamactiontracker.backend.dto.CreateUserRequest;
import com.teamactiontracker.backend.dto.LoginResponse;
import com.teamactiontracker.backend.dto.UserSummaryResponse;
import com.teamactiontracker.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(
    name = "Users",
    description = "Manage user accounts and retrieve active users"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a user account",
        description = """
            Creates a new user account using the supplied user details.
            The endpoint is restricted by the application's security rules.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User account created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or validation failure"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions"
        )
    })
    public LoginResponse createUser(
            @Parameter(description = "Details for the new user account")
            @Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    @Operation(
        summary = "Get active users",
        description = "Returns a list of active users available through this endpoint."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Active users retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions"
        )
    })
    public List<UserSummaryResponse> getUsers() {
        return userService.getActiveUsers();
    }
}
