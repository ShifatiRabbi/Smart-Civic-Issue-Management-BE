package com.sr.smart_civic_platform.complaint.validator;

import com.sr.smart_civic_platform.common.exception.BusinessException;
import com.sr.smart_civic_platform.complaint.entity.ComplaintStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/*
 * Purpose:
 * Complaint status workflow এর "কোন status থেকে কোন status এ যাওয়া
 * বৈধ" - এই rule centralized জায়গা থেকে enforce করা।
 *
 * Why centralized (not scattered if-else in service methods):
 * Workflow rule ভবিষ্যতে পরিবর্তন হতে পারে (নতুন status যোগ, নতুন
 * transition allow/disallow) - একটা জায়গায় থাকলে সেইটা এক জায়গাতেই
 * update করলেই চলবে, পুরো codebase খুঁজে বেড়াতে হবে না।
 *
 * Note:
 * "কে" (role) এই transition করতে পারবে - সেইটা এখানে না, service
 * layer এ (পরের sub-step এ) role check হবে, কারণ role check করতে
 * assignedStaffId এর মতো complaint-instance-specific data লাগে যেইটা
 * এই stateless validator এ available না।
 */
@Component
public class ComplaintStatusValidator {

    private static final Map<ComplaintStatus, Set<ComplaintStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(ComplaintStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(ComplaintStatus.SUBMITTED,
                EnumSet.of(ComplaintStatus.UNDER_REVIEW, ComplaintStatus.DUPLICATE));

        ALLOWED_TRANSITIONS.put(ComplaintStatus.UNDER_REVIEW,
                EnumSet.of(ComplaintStatus.VERIFIED, ComplaintStatus.REJECTED, ComplaintStatus.DUPLICATE));

        ALLOWED_TRANSITIONS.put(ComplaintStatus.VERIFIED,
                EnumSet.of(ComplaintStatus.ASSIGNED, ComplaintStatus.DUPLICATE));

        ALLOWED_TRANSITIONS.put(ComplaintStatus.ASSIGNED,
                EnumSet.of(ComplaintStatus.IN_PROGRESS, ComplaintStatus.DUPLICATE));

        ALLOWED_TRANSITIONS.put(ComplaintStatus.IN_PROGRESS,
                EnumSet.of(ComplaintStatus.RESOLVED, ComplaintStatus.DUPLICATE));

        ALLOWED_TRANSITIONS.put(ComplaintStatus.RESOLVED,
                EnumSet.of(ComplaintStatus.CLOSED, ComplaintStatus.REOPENED));

        ALLOWED_TRANSITIONS.put(ComplaintStatus.REOPENED,
                EnumSet.of(ComplaintStatus.UNDER_REVIEW));

        // Terminal states: no outgoing transitions.
        ALLOWED_TRANSITIONS.put(ComplaintStatus.CLOSED, EnumSet.noneOf(ComplaintStatus.class));
        ALLOWED_TRANSITIONS.put(ComplaintStatus.REJECTED, EnumSet.noneOf(ComplaintStatus.class));
        ALLOWED_TRANSITIONS.put(ComplaintStatus.DUPLICATE, EnumSet.noneOf(ComplaintStatus.class));
    }

    /*
     * Throws BusinessException if the transition is not allowed;
     * returns silently (no-op) if it is.
     */
    public void validateTransition(ComplaintStatus from, ComplaintStatus to) {
        Set<ComplaintStatus> allowedNext = ALLOWED_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(ComplaintStatus.class));

        if (!allowedNext.contains(to)) {
            throw new BusinessException(
                    "Cannot change status from " + from + " to " + to,
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}