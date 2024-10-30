package com.backend.dto.request.team;

import jakarta.validation.constraints.NotBlank;

public class TeamCreationRequest {
    
    @NotBlank(message = "Team name is required")
    private String name;

    // Bạn có thể thêm các thuộc tính khác nếu cần, ví dụ:
    private String description;

    // Getter và Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}