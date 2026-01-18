package com.example.ccms_1.Entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.List;
import com.example.ccms_1.Enums.ServiceType;

@Entity
@Table(name = "user")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @JsonProperty("name")
    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    private String userName;

    @JsonProperty("email")
    @Column(nullable = false, unique = true, length = 25)
    private String emailId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private boolean locationOnDuty = false;

    @Column(length = 50)
    @JsonProperty("location")
    private String location;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Complaint> complaints;

    protected User() {
    }

    public User(String userName, String emailId, String password, ServiceType serviceType, boolean locationOnDuty, String location) {
        this.userName = userName;
        this.emailId = emailId;
        this.password = password;
        this.serviceType = serviceType;
        this.locationOnDuty = locationOnDuty;
        this.location = location;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPassword() {
        return password;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public boolean isLocationOnDuty() {
        return locationOnDuty;
    }

    public String getLocation() {
        return location;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLocationOnDuty(boolean locationOnDuty) {
        this.locationOnDuty = locationOnDuty;
    }
}
