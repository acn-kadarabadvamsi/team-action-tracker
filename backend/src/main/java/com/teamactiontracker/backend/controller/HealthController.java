
package com.teamactiontracker.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(
    name = "Health",
    description = "Check the availability of the Team Action Tracker backend"
)
public class HealthController {

    @GetMapping("/health")
    @Operation(
        summary = "Check backend health",
        description = "Checks whether the Team Action Tracker backend is responding."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Backend is responding successfully"
        )
    })
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "message", "Team Action Tracker backend is running"
        );
    }
}
