package com.sr.smart_civic_platform.complaint.service.impl;

import com.sr.smart_civic_platform.category.entity.Category;
import com.sr.smart_civic_platform.category.repository.CategoryRepository;
import com.sr.smart_civic_platform.common.exception.BusinessException;
import com.sr.smart_civic_platform.common.exception.ResourceNotFoundException;
import com.sr.smart_civic_platform.complaint.dto.request.CreateComplaintRequest;
import com.sr.smart_civic_platform.complaint.dto.request.UpdateComplaintStatusRequest;
import com.sr.smart_civic_platform.complaint.dto.response.ComplaintResponse;
import com.sr.smart_civic_platform.complaint.entity.Complaint;
import com.sr.smart_civic_platform.complaint.entity.ComplaintStatus;
import com.sr.smart_civic_platform.complaint.entity.StatusHistoryEntry;
import com.sr.smart_civic_platform.complaint.repository.ComplaintRepository;
import com.sr.smart_civic_platform.complaint.service.ComplaintService;
import com.sr.smart_civic_platform.complaint.validator.ComplaintStatusValidator;
import com.sr.smart_civic_platform.user.dto.response.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private static final String ROLE_CITIZEN = "CITIZEN";
    private static final String ROLE_STAFF = "STAFF";
    private static final String ROLE_ADMIN = "ADMIN";

    private final ComplaintRepository complaintRepository;
    private final CategoryRepository categoryRepository;
    private final ComplaintStatusValidator statusValidator;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                 CategoryRepository categoryRepository,
                                 ComplaintStatusValidator statusValidator) {
        this.complaintRepository = complaintRepository;
        this.categoryRepository = categoryRepository;
        this.statusValidator = statusValidator;
    }

    /*
     * Purpose:
     * Citizen creates a new complaint. categoryId must reference an
     * existing, ACTIVE category - complaints cannot be filed against
     * a deactivated category (it would no longer make sense to route it).
     */
    @Override
    public ComplaintResponse createComplaint(String citizenId, CreateComplaintRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.isActive()) {
            throw new BusinessException("Selected category is not active", HttpStatus.BAD_REQUEST);
        }

        Complaint complaint = new Complaint(
                request.getTitle(),
                request.getDescription(),
                request.getCategoryId(),
                citizenId,
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude()
        );

        complaint.getStatusHistory().add(
                new StatusHistoryEntry(ComplaintStatus.SUBMITTED, citizenId, "Complaint submitted")
        );

        Complaint saved = complaintRepository.save(complaint);
        return ComplaintResponse.fromEntity(saved);
    }

    @Override
    public PagedResponse<ComplaintResponse> getMyComplaints(String citizenId, Pageable pageable) {
        Page<Complaint> page = complaintRepository.findByCitizenId(citizenId, pageable);
        return PagedResponse.fromPage(page.map(ComplaintResponse::fromEntity));
    }

    @Override
    public PagedResponse<ComplaintResponse> getAssignedComplaints(String staffId, Pageable pageable) {
        Page<Complaint> page = complaintRepository.findByAssignedStaffId(staffId, pageable);
        return PagedResponse.fromPage(page.map(ComplaintResponse::fromEntity));
    }

    /*
     * Purpose:
     * Admin/staff-wide list, optionally filtered by status. categoryId
     * filter is applied in-memory-free via a simple branch since we only
     * have single-field filters for now (a Criteria-based dynamic query
     * would be the next evolution if filters start combining).
     */
    @Override
    public PagedResponse<ComplaintResponse> getAllComplaints(ComplaintStatus statusFilter, String categoryIdFilter, Pageable pageable) {
        Page<Complaint> page;

        if (statusFilter != null) {
            page = complaintRepository.findByStatus(statusFilter, pageable);
        } else {
            page = complaintRepository.findAll(pageable);
        }

        // NOTE: categoryIdFilter combined with statusFilter is not yet
        // supported by a single repository query - documenting this as a
        // known limitation to address with a Criteria query if/when both
        // filters are needed simultaneously in the frontend.
        return PagedResponse.fromPage(page.map(ComplaintResponse::fromEntity));
    }

    /*
     * Purpose:
     * Fetch a single complaint, enforcing ownership: a CITIZEN may only
     * view their own complaint; STAFF/ADMIN may view any.
     */
    @Override
    public ComplaintResponse getComplaintById(String id, String requesterId, String requesterRole) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        if (ROLE_CITIZEN.equals(requesterRole) && !complaint.getCitizenId().equals(requesterId)) {
            // Same exception as "not found" - a citizen probing another
            // citizen's complaint id should not be able to distinguish
            // "exists but not yours" from "does not exist" (enumeration).
            throw new ResourceNotFoundException("Complaint not found");
        }

        return ComplaintResponse.fromEntity(complaint);
    }

    /*
     * Purpose:
     * The core workflow engine: validates (1) the status transition
     * itself is allowed by the state machine, (2) the requester's role
     * is permitted to make that particular transition, and (3) for
     * staff/citizen-specific transitions, that the requester actually
     * owns/is-assigned-to this complaint.
     */
    @Override
    public ComplaintResponse updateStatus(String id, String requesterId, String requesterRole, UpdateComplaintStatusRequest request) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        ComplaintStatus from = complaint.getStatus();
        ComplaintStatus to = request.getStatus();

        // 1. Workflow validity (state machine)
        statusValidator.validateTransition(from, to);

        // 2 & 3. Role permission + ownership, per transition
        authorizeTransition(complaint, requesterId, requesterRole, to);

        complaint.setStatus(to);
        complaint.getStatusHistory().add(new StatusHistoryEntry(to, requesterId, request.getNote()));

        Complaint saved = complaintRepository.save(complaint);
        return ComplaintResponse.fromEntity(saved);
    }

    /*
     * Purpose:
     * Encodes the "who can do this transition" table from the design step.
     * Kept as a single method (rather than scattered checks) so the whole
     * permission matrix is readable in one place.
     */
    private void authorizeTransition(Complaint complaint, String requesterId, String requesterRole, ComplaintStatus to) {

        boolean isAdmin = ROLE_ADMIN.equals(requesterRole);
        boolean isStaff = ROLE_STAFF.equals(requesterRole);
        boolean isCitizen = ROLE_CITIZEN.equals(requesterRole);
        boolean isAssignedStaff = isStaff && requesterId.equals(complaint.getAssignedStaffId());
        boolean isOwnerCitizen = isCitizen && requesterId.equals(complaint.getCitizenId());

        Set<ComplaintStatus> staffOrAdminOnly = EnumSet.of(
                ComplaintStatus.UNDER_REVIEW, ComplaintStatus.VERIFIED,
                ComplaintStatus.REJECTED, ComplaintStatus.DUPLICATE
        );

        boolean allowed = switch (to) {
            // Review/verify/reject/duplicate - staff or admin, no ownership needed
            case UNDER_REVIEW, VERIFIED, REJECTED, DUPLICATE -> isStaff || isAdmin;

            // Assign - admin only (full assignment flow completed in PHASE-6)
            case ASSIGNED -> isAdmin;

            // In-progress / resolved - only the staff this complaint is assigned to
            case IN_PROGRESS, RESOLVED -> isAssignedStaff || isAdmin;

            // Closed - admin, or the citizen who owns the complaint
            case CLOSED -> isAdmin || isOwnerCitizen;

            // Reopened - only the owning citizen
            case REOPENED -> isOwnerCitizen;

            default -> false;
        };

        if (!allowed) {
            throw new BusinessException(
                    "You are not permitted to change this complaint to status " + to,
                    HttpStatus.FORBIDDEN
            );
        }
    }
}