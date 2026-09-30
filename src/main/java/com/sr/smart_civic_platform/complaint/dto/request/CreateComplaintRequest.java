package com.sr.smart_civic_platform.complaint.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * Purpose:
 * Complaint create করার request body।
 *
 * Why no "citizenId" field:
 * Server SecurityContext (token) থেকে নির্ধারণ করবে কে submit করছে -
 * client কে অন্য কারো নামে complaint submit করার সুযোগ দেওয়া যাবে না।
 *
 * Why no "status"/"imageUrls" field:
 * status সবসময় SUBMITTED দিয়ে শুরু হয় (server hardcoded)।
 * imageUrls এখনো actual upload flow নেই (MinIO future), তাই এখন
 * request এ রাখা হচ্ছে না - future এ আলাদা upload-then-attach flow হবে।
 */
public class CreateComplaintRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 150, message = "Title must be between 5 and 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;

    @NotBlank(message = "Category is required")
    private String categoryId;

    @NotBlank(message = "Address is required")
    @Size(max = 300, message = "Address must not exceed 300 characters")
    private String address;

    private Double latitude;  // optional
    private Double longitude; // optional

    public CreateComplaintRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}