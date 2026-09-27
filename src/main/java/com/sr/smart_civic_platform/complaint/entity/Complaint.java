package com.sr.smart_civic_platform.complaint.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/*
 * Purpose:
 * Core entity - একটা citizen-reported civic issue এর পুরো lifecycle।
 *
 * Ownership fields (citizenId, assignedStaffId):
 * কখনো client request থেকে সরাসরি set হবে না - citizenId আসবে
 * JWT token (SecurityContext) থেকে create করার সময়, assignedStaffId
 * set হবে শুধু admin-triggered assign action দিয়ে (PHASE-6)।
 *
 * imageUrls:
 * এখন empty list দিয়ে শুরু হবে (কোনো actual file upload নেই এখনো) -
 * MinIO integration (future) এ এইখানে actual URL বসবে, entity
 * structure পাল্টাতে হবে না।
 */
@Document(collection = "complaints")
public class Complaint {

    @Id
    private String id;

    private String title;

    private String description;

    @Indexed
    private String categoryId;

    @Indexed
    private String citizenId;

    @Indexed
    private String assignedStaffId; // nullable until assigned

    @Indexed
    private ComplaintStatus status;

    private String address;

    private Double latitude;  // nullable
    private Double longitude; // nullable

    private List<String> imageUrls = new ArrayList<>();

    private List<StatusHistoryEntry> statusHistory = new ArrayList<>();

    @CreatedDate
    @Indexed
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Complaint() {
    }

    public Complaint(String title, String description, String categoryId, String citizenId,
                      String address, Double latitude, Double longitude) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.citizenId = citizenId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = ComplaintStatus.SUBMITTED;
        this.imageUrls = new ArrayList<>();
        this.statusHistory = new ArrayList<>();
    }

    // ---- Getters & Setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }

    public String getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(String assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public List<StatusHistoryEntry> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<StatusHistoryEntry> statusHistory) { this.statusHistory = statusHistory; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}