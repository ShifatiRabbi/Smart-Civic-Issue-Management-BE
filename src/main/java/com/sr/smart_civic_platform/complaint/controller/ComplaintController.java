package com.sr.smart_civic_platform.complaint.controller;

import com.sr.smart_civic_platform.common.response.ApiResponse;
import com.sr.smart_civic_platform.complaint.dto.request.CreateComplaintRequest;
import com.sr.smart_civic_platform.complaint.dto.request.UpdateComplaintStatusRequest;
import com.sr.smart_civic_platform.complaint.dto.response.ComplaintResponse;
import com.sr.smart_civic_platform.complaint.entity.ComplaintStatus;
import com.sr.smart_civic_platform.complaint.service.ComplaintService;
import com.sr.smart_civic_platform.user.dto.response.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PreAuthorize("hasRole('CITIZEN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            Authentication authentication,
            @Valid @RequestBody CreateComplaintRequest request) {

        String citizenId = (String) authentication.getPrincipal();
        ComplaintResponse response = complaintService.createComplaint(citizenId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complaint submitted successfully", response));
    }

    @PreAuthorize("hasRole('CITIZEN')")
    @GetMapping("/my")
    public ApiResponse<PagedResponse<ComplaintResponse>> getMyComplaints(
            Authentication authentication, Pageable pageable) {

        String citizenId = (String) authentication.getPrincipal();
        return ApiResponse.success("Complaints fetched successfully",
                complaintService.getMyComplaints(citizenId, pageable));
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/assigned")
    public ApiResponse<PagedResponse<ComplaintResponse>> getAssignedComplaints(
            Authentication authentication, Pageable pageable) {

        String staffId = (String) authentication.getPrincipal();
        return ApiResponse.success("Assigned complaints fetched successfully",
                complaintService.getAssignedComplaints(staffId, pageable));
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @GetMapping
    public ApiResponse<PagedResponse<ComplaintResponse>> getAllComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) String categoryId,
            Pageable pageable) {

        return ApiResponse.success("Complaints fetched successfully",
                complaintService.getAllComplaints(status, categoryId, pageable));
    }

    /*
     * Purpose:
     * Any authenticated role can call this - ownership/role-based
     * visibility is enforced inside the service (a citizen only sees
     * their own; staff/admin see any).
     */
    @GetMapping("/{id}")
    public ApiResponse<ComplaintResponse> getComplaintById(
            Authentication authentication, @PathVariable String id) {

        String requesterId = (String) authentication.getPrincipal();
        String requesterRole = extractRole(authentication);

        return ApiResponse.success("Complaint fetched successfully",
                complaintService.getComplaintById(id, requesterId, requesterRole));
    }

    /*
     * Purpose:
     * Single endpoint for every status transition - the service layer's
     * ComplaintStatusValidator + authorizeTransition() decide what is
     * actually allowed for this requester and this complaint's state.
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<ComplaintResponse> updateStatus(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody UpdateComplaintStatusRequest request) {

        String requesterId = (String) authentication.getPrincipal();
        String requesterRole = extractRole(authentication);

        return ApiResponse.success("Complaint status updated successfully",
                complaintService.updateStatus(id, requesterId, requesterRole, request));
    }

    /*
     * Purpose:
     * Extract the plain role name ("CITIZEN"/"STAFF"/"ADMIN") from the
     * "ROLE_X" authority set by JwtAuthenticationFilter, since the
     * service layer works with plain role strings, not Spring's
     * "ROLE_"-prefixed authority format.
     */
    private String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No role found on authenticated principal"));
    }
}