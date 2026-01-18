package com.example.ccms_1.Controllers;

import com.example.ccms_1.Entities.Admin;
import com.example.ccms_1.Entities.Complaint;
import com.example.ccms_1.Services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody Admin admin) {
        adminService.register(admin);
        return ResponseEntity.ok("Admin registered successfully");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody Admin admin) {
        return ResponseEntity.ok(
                adminService.login(admin.getEmailId(), admin.getPassword())
        );
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> viewAssignedComplaints(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                adminService.getAssignedComplaints(authentication.getName())
        );
    }

    @PutMapping("/complaint/resolve/{complaintId}")
    public ResponseEntity<String> resolveComplaint(
            @PathVariable Long complaintId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                adminService.resolveComplaint(
                        complaintId,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/location/on-duty")
    public ResponseEntity<String> updateLocationOnDuty(
            @RequestParam boolean onDuty,
            Authentication authentication
    ) {
        adminService.updateLocationOnDuty(authentication.getName(), onDuty);
        return ResponseEntity.ok("Location ON DUTY updated");
    }

    @PutMapping("/complaint/willingness/{complaintId}")
    public ResponseEntity<String> respondToComplaintWillingness(
            @PathVariable Long complaintId,
            @RequestParam boolean willing,
            Authentication authentication
    ) {
        adminService.respondToWillingness(
                complaintId,
                authentication.getName(),
                willing
        );
        return ResponseEntity.ok("Response recorded");
    }
}

