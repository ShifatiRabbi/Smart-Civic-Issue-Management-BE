package com.sr.smart_civic_platform.complaint.dto.request;

import com.sr.smart_civic_platform.complaint.entity.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateComplaintStatusRequest {

    @NotNull(message = "Status is required")
    private ComplaintStatus status;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note; // optional - e.g. rejection reason, resolution summary

    public UpdateComplaintStatusRequest() {
    }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}