package com.sr.smart_civic_platform.complaint.entity;

/*
 * Purpose:
 * Complaint এর সম্ভাব্য সব status - master prompt এ defined workflow অনুযায়ী।
 *
 * Terminal states (no further transition allowed):
 * CLOSED, REJECTED, DUPLICATE
 * (REOPENED থেকে আবার UNDER_REVIEW এ যেতে পারবে - এইটা terminal না)
 */
public enum ComplaintStatus {
    SUBMITTED,
    UNDER_REVIEW,
    VERIFIED,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    REJECTED,
    REOPENED,
    DUPLICATE
}