package com.example.ccms_1.Services;


import com.example.ccms_1.Entities.Admin;
import com.example.ccms_1.Entities.Complaint;
import com.example.ccms_1.Enums.ComplaintStatus;
import com.example.ccms_1.Repositories.AdminRepository;
import com.example.ccms_1.Repositories.ComplaintRepository;
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
public class AdminService {

    private final AdminRepository adminRepository;
    private final ComplaintRepository complaintRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final EmailService emailService;

    public AdminService(
            AdminRepository adminRepository,
            ComplaintRepository complaintRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JWTService jwtService,
            EmailService emailService
    ) {
        this.adminRepository = adminRepository;
        this.complaintRepository = complaintRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public void register(Admin admin) {
        if (adminRepository.findByEmailId(admin.getEmailId()).isPresent()) {
            throw new RuntimeException("Admin already registered");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
    }

    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return jwtService.generateToken(
                (UserDetails) authentication.getPrincipal()
        );
    }

    public List<Complaint> getAssignedComplaints(String email) {
        Admin admin = adminRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return complaintRepository.findByAdmins_AdminId(admin.getAdminId());
    }

    @Transactional
    public String resolveComplaint(Long complaintId, String email) {

        Admin admin = adminRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (!complaint.getAdmins().contains(admin)) {
            throw new RuntimeException("Complaint not assigned to this admin");
        }

        complaint.setStatus(ComplaintStatus.RESOLVED);
        admin.markAvailable();
        admin.incrementHandledComplaintCount();

        emailService.sendComplaintResolvedMailToUser(
                complaint.getUser().getEmailId(),
                complaint.getComplaintId()
        );

        return "Complaint resolved successfully.";
    }

    @Transactional
    public void updateLocationOnDuty(String email, boolean onDuty) {
        Admin admin = adminRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setLocationOnDuty(onDuty);
    }

    @Transactional
    public void respondToWillingness(Long complaintId, String email, boolean willing) {

        Admin admin = adminRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (!complaint.getAdmins().contains(admin)) {
            throw new RuntimeException("Complaint not assigned to this admin");
        }

        admin.setWillingToHandle(willing);

        if (!willing) {
            admin.markAvailable();
        }
    }

}

