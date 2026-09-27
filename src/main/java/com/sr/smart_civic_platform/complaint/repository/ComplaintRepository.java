package com.sr.smart_civic_platform.complaint.repository;

import com.sr.smart_civic_platform.complaint.entity.Complaint;
import com.sr.smart_civic_platform.complaint.entity.ComplaintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/*
 * Purpose:
 * Complaint collection এর CRUD + common filtered queries।
 *
 * Method names kept simple now; more complex multi-field filtering
 * (status + category + date range together) will likely need a
 * Criteria/Query-based custom method in the next sub-step once the
 * actual list API's filter requirements are finalized.
 */
public interface ComplaintRepository extends MongoRepository<Complaint, String> {

    Page<Complaint> findByCitizenId(String citizenId, Pageable pageable);

    Page<Complaint> findByAssignedStaffId(String staffId, Pageable pageable);

    Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable);
}