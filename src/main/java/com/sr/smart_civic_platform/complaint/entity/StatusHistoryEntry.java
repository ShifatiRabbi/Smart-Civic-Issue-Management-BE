package com.sr.smart_civic_platform.complaint.entity;

import java.time.Instant;

/*
 * Purpose:
 * প্রতিটা status change এর audit record - embedded sub-document,
 * আলাদা collection না (সবসময় parent complaint এর সাথেই accessed হয়)।
 *
 * Why no @Id here:
 * এইটা standalone entity না, শুধু Complaint এর অংশ হিসেবে বাঁচে -
 * নিজস্ব identity/lifecycle নেই।
 */
public class StatusHistoryEntry {

    private ComplaintStatus status;
    private String changedBy; // userId
    private Instant changedAt;
    private String note; // optional, e.g. rejection reason

    public StatusHistoryEntry() {
    }

    public StatusHistoryEntry(ComplaintStatus status, String changedBy, String note) {
        this.status = status;
        this.changedBy = changedBy;
        this.changedAt = Instant.now();
        this.note = note;
    }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public Instant getChangedAt() { return changedAt; }
    public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}