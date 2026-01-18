package com.example.ccms_1.Entities;


import com.example.ccms_1.Enums.AdminStatus;
import com.example.ccms_1.Enums.ServiceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @JsonProperty("name")
    @Column(name = "admin_name", nullable = false, unique = true, length = 50)
    private String adminName;

    @JsonProperty("email")
    @Column(nullable = false, unique = true, length = 25)
    private String emailId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminStatus status = AdminStatus.AVAILABLE;

    @Column(nullable = false)
    private int ranking = 1;

    @Column(nullable = false)
    private int handledComplaintCount = 0;

    @Column(nullable = false)
    private boolean locationOnDuty = false;

    @Column(nullable = false)
    private boolean willingToHandle = false;

    @Column(length = 50)
    private String location;

    @ManyToMany(mappedBy = "admins")
    @JsonIgnore
    private Set<Complaint> complaints = new HashSet<>();

    public Admin() {
    }

    public Admin(ServiceType serviceType,
                 String adminName,
                 String emailId,
                 String password,
                 boolean locationOnDuty,
                 boolean willingToHandle,
                 String location) {

        this.serviceType = serviceType;
        this.adminName = adminName;
        this.emailId = emailId;
        this.password = password;
        this.locationOnDuty = locationOnDuty;
        this.willingToHandle = willingToHandle;
        this.location = location;
    }

    public Long getAdminId() {
        return adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPassword() {
        return password;
    }

    public AdminStatus getStatus() {
        return status;
    }

    public int getRanking() {
        return ranking;
    }

    public int getHandledComplaintCount() {
        return handledComplaintCount;
    }

    public boolean isLocationOnDuty() {
        return locationOnDuty;
    }

    public boolean isWillingToHandle() {
        return willingToHandle;
    }

    public String getLocation() {
        return location;
    }

    public Set<Complaint> getComplaints() {
        return complaints;
    }

    public void incrementRanking() {
        this.ranking++;
    }

    public void incrementHandledComplaintCount() {
        this.handledComplaintCount++;
    }

    public void markAvailable() {
        this.status = AdminStatus.AVAILABLE;
    }

    public void markBusy() {
        this.status = AdminStatus.BUSY;
    }

    public void setLocationOnDuty(boolean onDuty) {
        this.locationOnDuty = onDuty;
    }

    public void setWillingToHandle(boolean willing) {
        this.willingToHandle = willing;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
