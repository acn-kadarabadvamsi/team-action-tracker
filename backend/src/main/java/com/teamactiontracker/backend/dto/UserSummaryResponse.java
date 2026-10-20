
package com.teamactiontracker.backend.dto;

public class UserSummaryResponse {

    private final Long userId;
    private final String name;
    private final String email;
    private final String role;

    public UserSummaryResponse(
            Long userId,
            String name,
            String email,
            String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
