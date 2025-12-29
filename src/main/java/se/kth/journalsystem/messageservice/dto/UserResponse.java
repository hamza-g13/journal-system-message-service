package se.kth.journalsystem.messageservice.dto;

import se.kth.journalsystem.messageservice.model.UserRole;

public record UserResponse(
        Long id,
        String username,
        UserRole role) {

    // Compatibility methods
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }
}
