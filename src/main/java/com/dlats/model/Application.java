package com.dlats.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "application")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @Column(name = "application_no", nullable = false, unique = true, length = 30)
    private String applicationNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", nullable = false)
    private VehicleClass vehicleClass;

    @Column(name = "application_type", nullable = false, length = 50)
    private String applicationType;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "current_status", nullable = false, length = 50)
    private String currentStatus = "Application Submitted";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Application() {}

    public Application(String applicationNo, User user, VehicleClass vehicleClass,
                       String applicationType, LocalDate applicationDate, String currentStatus) {
        this.applicationNo = applicationNo;
        this.user = user;
        this.vehicleClass = vehicleClass;
        this.applicationType = applicationType;
        this.applicationDate = applicationDate;
        this.currentStatus = currentStatus != null ? currentStatus : "Application Submitted";
        this.createdAt = LocalDateTime.now();
    }

    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }

    public String getApplicationNo() { return applicationNo; }
    public void setApplicationNo(String applicationNo) { this.applicationNo = applicationNo; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public VehicleClass getVehicleClass() { return vehicleClass; }
    public void setVehicleClass(VehicleClass vehicleClass) { this.vehicleClass = vehicleClass; }

    public String getApplicationType() { return applicationType; }
    public void setApplicationType(String applicationType) { this.applicationType = applicationType; }

    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
