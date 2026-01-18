package com.example.ccms_1.Schedulers;

import com.example.ccms_1.Entities.Admin;
import com.example.ccms_1.Entities.Complaint;
import com.example.ccms_1.Enums.ComplaintStatus;
import com.example.ccms_1.Repositories.AdminRepository;
import com.example.ccms_1.Repositories.ComplaintRepository;
import com.example.ccms_1.Services.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ComplaintEscalationScheduler {

    private final ComplaintRepository complaintRepository;
    private final AdminRepository adminRepository;
    private final EmailService emailService;

    public ComplaintEscalationScheduler(
            ComplaintRepository complaintRepository,
            AdminRepository adminRepository,
            EmailService emailService
    ) {
        this.complaintRepository = complaintRepository;
        this.adminRepository = adminRepository;
        this.emailService = emailService;
    }

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void penalizeAdminsForPendingComplaints() {

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        List<Complaint> complaints =
                complaintRepository.findByStatusAndAssignedAtBefore(
                        ComplaintStatus.ASSIGNED,
                        fiveMinutesAgo
                );

        for (Complaint complaint : complaints) {

            for (Admin admin : complaint.getAdmins()) {
                admin.incrementRanking();
                adminRepository.save(admin);
            }

            complaint.setStatus(ComplaintStatus.IN_PROGRESS);
            complaintRepository.save(complaint);
        }
    }

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void handleUnresolvedComplaintsBeyondTwoHours() {

        LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);

        List<Complaint> complaints =
                complaintRepository
                        .findByStatusAndAssignedAtBeforeAndRegretMailSentFalse(
                                ComplaintStatus.IN_PROGRESS,
                                twoHoursAgo
                        );

        for (Complaint complaint : complaints) {

            complaint.setStatus(ComplaintStatus.ESCALATED);
            complaint.markWaitingForAdmin();

            for (Admin admin : complaint.getAdmins()) {
                admin.markAvailable();
                adminRepository.save(admin);
            }

            emailService.sendSystemHiringAlertMail(
                    "complaintservice184@gmail.com",
                    complaint.getComplaintId(),
                    complaint.getUser().getLocation(),
                    complaint.getServiceType().name()
            );

            emailService.sendUserWaitNotificationMail(
                    complaint.getUser().getEmailId(),
                    complaint.getComplaintId()
            );

            complaint.markRegretMailSent();
            complaintRepository.save(complaint);
        }
    }
}

