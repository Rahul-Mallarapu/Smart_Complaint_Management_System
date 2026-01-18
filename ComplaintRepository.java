package com.example.ccms_1.Repositories;

import com.example.ccms_1.Entities.Complaint;
import com.example.ccms_1.Enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUser_UserId(Long userId);

    List<Complaint> findByAdmins_AdminId(Long adminId);

    List<Complaint> findByStatusAndAssignedAtBefore(
            ComplaintStatus status,
            LocalDateTime time
    );

    List<Complaint> findByStatusAndAssignedAtBeforeAndRegretMailSentFalse(
            ComplaintStatus status,
            LocalDateTime time
    );
}

