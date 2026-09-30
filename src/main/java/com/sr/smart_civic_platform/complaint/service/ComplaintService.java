package com.sr.smart_civic_platform.complaint.service;

import com.sr.smart_civic_platform.complaint.dto.request.CreateComplaintRequest;
import com.sr.smart_civic_platform.complaint.dto.request.UpdateComplaintStatusRequest;
import com.sr.smart_civic_platform.complaint.dto.response.ComplaintResponse;
import com.sr.smart_civic_platform.complaint.entity.ComplaintStatus;
import com.sr.smart_civic_platform.user.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

/*
 * Purpose:
 * Complaint সম্পর্কিত সব business logic এর contract।
 *
 * Note on parameters:
 * Every method takes "requesterId" and/or "requesterRole" explicitly
 * (extracted from SecurityContext in the controller) rather than
 * reading SecurityContextHolder inside the service - this keeps the
 * service layer testable without needing to mock a security context.
 */
public interface ComplaintService {

    ComplaintResponse createComplaint(String citizenId, CreateComplaintRequest request);

    PagedResponse<ComplaintResponse> getMyComplaints(String citizenId, Pageable pageable);

    PagedResponse<ComplaintResponse> getAssignedComplaints(String staffId, Pageable pageable);

    PagedResponse<ComplaintResponse> getAllComplaints(ComplaintStatus statusFilter, String categoryIdFilter, Pageable pageable);

    ComplaintResponse getComplaintById(String id, String requesterId, String requesterRole);

    ComplaintResponse updateStatus(String id, String requesterId, String requesterRole, UpdateComplaintStatusRequest request);
}