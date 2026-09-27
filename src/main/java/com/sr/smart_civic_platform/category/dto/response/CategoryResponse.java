package com.sr.smart_civic_platform.category.dto.response;

import com.sr.smart_civic_platform.category.entity.Category;

import java.time.Instant;

public class CategoryResponse {

    private String id;
    private String name;
    private String description;
    private boolean isActive;
    private Instant createdAt;

    public CategoryResponse(String id, String name, String description, boolean isActive, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt()
        );
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }
    public Instant getCreatedAt() { return createdAt; }
}