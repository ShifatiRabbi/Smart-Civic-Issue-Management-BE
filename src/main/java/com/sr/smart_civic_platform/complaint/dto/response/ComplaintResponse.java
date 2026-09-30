package com.sr.smart_civic_platform.complaint.dto.response;

import com.sr.smart_civic_platform.complaint.entity.Complaint;
import com.sr.smart_civic_platform.complaint.entity.ComplaintStatus;

import java.time.Instant;
import java.util.List;

public class ComplaintResponse {

    private String id;
    private String title;
    private String description;
    private String categoryId;
    private String citizenId;
    private String assignedStaffId;
    private ComplaintStatus status;
    private String address;
    private Double latitude;
    private Double longitude;
    private List<String> imageUrls;
    private List<StatusHistoryResponse> statusHistory;
    private Instant createdAt;
    private Instant updatedAt;

    public ComplaintResponse(String id, String title, String description, String categoryId,
                              String citizenId, String assignedStaffId, ComplaintStatus status,
                              String address, Double latitude, Double longitude,
                              List<String> imageUrls, List<StatusHistoryResponse> statusHistory,
                              Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.citizenId = citizenId;
        this.assignedStaffId = assignedStaffId;
        this.status = status;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrls = imageUrls;
        this.statusHistory = statusHistory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ComplaintResponse fromEntity(Complaint c) {
        List<StatusHistoryResponse> history = c.getStatusHistory().stream()
                .map(StatusHistoryResponse::fromEntity)
                .toList();

        return new ComplaintResponse(
                c.getId(), c.getTitle(), c.getDescription(), c.getCategoryId(),
                c.getCitizenId(), c.getAssignedStaffId(), c.getStatus(),
                c.getAddress(), c.getLatitude(), c.getLongitude(),
                c.getImageUrls(), history, c.getCreatedAt(), c.getUpdatedAt()
        );
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategoryId() { return categoryId; }
    public String getCitizenId() { return citizenId; }
    public String getAssignedStaffId() { return assignedStaffId; }
    public ComplaintStatus getStatus() { return status; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public List<String> getImageUrls() { return imageUrls; }
    public List<StatusHistoryResponse> getStatusHistory() { return statusHistory; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}