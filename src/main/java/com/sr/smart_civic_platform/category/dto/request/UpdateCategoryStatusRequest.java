package com.sr.smart_civic_platform.category.dto.request;

import jakarta.validation.constraints.NotNull;

public class UpdateCategoryStatusRequest {

    @NotNull(message = "isActive is required")
    private Boolean isActive;

    public UpdateCategoryStatusRequest() {
    }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}