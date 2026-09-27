package com.sr.smart_civic_platform.category.dto.request;

import jakarta.validation.constraints.Size;

/*
 * Purpose:
 * name/description আপডেট করার request - PATCH semantics
 * (null field মানে "এইটা change করছি না")।
 *
 * Why isActive is NOT here:
 * Status change এর জন্য আলাদা endpoint/DTO (UpdateCategoryStatusRequest)
 * রাখা হয়েছে - এইটা intentional (Single Responsibility): "নাম বদলানো"
 * আর "activate/deactivate করা" দুইটা আলাদা business action,
 * আলাদা audit/log প্রয়োজন হতে পারে future এ।
 */
public class UpdateCategoryRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    public UpdateCategoryRequest() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}