package com.example.ccms_1.Repositories;

import com.example.ccms_1.Entities.Admin;
import com.example.ccms_1.Enums.AdminStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import com.example.ccms_1.Enums.ServiceType;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmailId(String emailId);

    List<Admin> findByStatusAndServiceTypeAndLocationAndWillingToHandleTrueOrderByRankingAscHandledComplaintCountDesc(
            AdminStatus status,
            ServiceType serviceType,
            String location
    );

    List<Admin> findByStatusAndServiceTypeAndWillingToHandleTrueOrderByRankingAscHandledComplaintCountDesc(
            AdminStatus status,
            ServiceType serviceType
    );
}






