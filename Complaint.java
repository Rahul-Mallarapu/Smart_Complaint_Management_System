package com.example.ccms_1.Entities;


import com.example.ccms_1.Enums.ComplaintStatus;
import com.example.ccms_1.Enums.ServiceType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "complaint")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @Column(nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"complaints"})
    private User user;

    @ManyToMany
    @JoinTable(
            name = "complaint_admin",
            joinColumns = @JoinColumn(name = "complaint_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_id")
    )
    private Set<Admin> admins = new HashSet<>();

    @Column
    private LocalDateTime assignedAt;

    @Column(nullable = false)
    private boolean regretMailSent = false;

    @Column(nullable = false)
    private boolean waitingForAdmin = false;

    public Long getComplaintId() {
        return complaintId;
    }

    public String getDescription() {
        return description;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public User getUser() {
        return user;
    }

    public Set<Admin> getAdmins() {
        return admins;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public boolean isRegretMailSent() {
        return regretMailSent;
    }

    public boolean isWaitingForAdmin() {
        return waitingForAdmin;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public void markWaiting() {
        this.waitingForAdmin = true;
    }

    public void markWaitingForAdmin() {
        this.waitingForAdmin = true;
    }

    public void markAssigned() {
        this.status = ComplaintStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
        this.waitingForAdmin = false;
    }

    public void markEscalated() {
        this.status = ComplaintStatus.ESCALATED;
    }

    public void markResolved() {
        this.status = ComplaintStatus.RESOLVED;
    }

    public void markRegretMailSent() {
        this.regretMailSent = true;
    }

    public void assignAdmin(Admin admin) {
        this.admins.add(admin);
        admin.getComplaints().add(this);
        markAssigned();
    }
}

