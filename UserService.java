package com.example.ccms_1.Services;

import com.example.ccms_1.Entities.Admin;
import com.example.ccms_1.Entities.Complaint;
import com.example.ccms_1.Entities.User;
import com.example.ccms_1.Enums.AdminStatus;
import com.example.ccms_1.Enums.ComplaintStatus;
import com.example.ccms_1.Enums.ServiceType;
import com.example.ccms_1.Repositories.AdminRepository;
import com.example.ccms_1.Repositories.ComplaintRepository;
import com.example.ccms_1.Repositories.UserRepository;
import com.example.ccms_1.Security.JWTService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ComplaintRepository complaintRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final EmailService emailService;

    public UserService(
            UserRepository userRepository,
            AdminRepository adminRepository,
            ComplaintRepository complaintRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JWTService jwtService,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.complaintRepository = complaintRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    /* ================= USER AUTH ================= */

    public void registerUser(User user) {
        if (userRepository.findByEmailId(user.getEmailId()).isPresent()) {
            throw new RuntimeException("User already registered");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return jwtService.generateToken((UserDetails) authentication.getPrincipal());
    }

    public List<Complaint> getUserComplaints(String email) {
        User user = userRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return complaintRepository.findByUser_UserId(user.getUserId());
    }

    /* ================= COMPLAINT ================= */

    @Transactional
    public String registerComplaint(String email, String description) {

        User user = userRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Complaint complaint = new Complaint();
        complaint.setDescription(description);
        complaint.setUser(user);
        complaint.setServiceType(user.getServiceType());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.markWaiting();

        List<Admin> admins;

        if (user.getServiceType() == ServiceType.PHYSICAL) {
            admins = adminRepository
                    .findByStatusAndServiceTypeAndLocationAndWillingToHandleTrueOrderByRankingAscHandledComplaintCountDesc(
                            AdminStatus.AVAILABLE,
                            ServiceType.PHYSICAL,
                            user.getLocation()
                    );
        } else {
            admins = adminRepository
                    .findByStatusAndServiceTypeAndWillingToHandleTrueOrderByRankingAscHandledComplaintCountDesc(
                            AdminStatus.AVAILABLE,
                            ServiceType.ONLINE
                    );
        }

        if (admins.isEmpty()) {

            Complaint savedComplaint = complaintRepository.save(complaint);

            emailService.sendSystemHiringAlertMailWithDelay(
                    "complaintservice184@gmail.com",
                    savedComplaint.getComplaintId(),
                    savedComplaint.getDescription(),
                    user.getUserName(),
                    user.getEmailId(),
                    user.getLocation(),
                    user.getServiceType().name(),
                    5
            );

            emailService.sendUserWaitNotificationMail(
                    user.getEmailId(),
                    savedComplaint.getComplaintId()
            );

            return "No admin available. Please retry after some time.";
        }

        Admin selectedAdmin = admins.getFirst();
        selectedAdmin.markBusy();
        complaint.assignAdmin(selectedAdmin);

        complaintRepository.save(complaint);
        adminRepository.save(selectedAdmin);

        emailService.sendComplaintRaisedMailToAdmin(
                selectedAdmin.getEmailId(),
                user.getEmailId(),
                complaint.getComplaintId()
        );

        return "Complaint registered and assigned successfully.";
    }

}



