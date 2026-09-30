package com.sr.smart_civic_platform.complaint.dto.response;

import com.sr.smart_civic_platform.complaint.entity.ComplaintStatus;
import com.sr.smart_civic_platform.complaint.entity.StatusHistoryEntry;

import java.time.Instant;

public class StatusHistoryResponse {

    private ComplaintStatus status;
    private String changedBy;
    private Instant changedAt;
    private String note;

    public StatusHistoryResponse(ComplaintStatus status, String changedBy, Instant changedAt, String note) {
        this.status = status;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
        this.note = note;
    }

    public static StatusHistoryResponse fromEntity(StatusHistoryEntry entry) {
        return new StatusHistoryResponse(entry.getStatus(), entry.getChangedBy(), entry.getChangedAt(), entry.getNote());
    }

    public ComplaintStatus getStatus() { return status; }
    public String getChangedBy() { return changedBy; }
    public Instant getChangedAt() { return changedAt; }
    public String getNote() { return note; }
}