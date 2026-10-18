package com.dlats.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "driving_test")
public class DrivingTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Integer testId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "test_date")
    private LocalDate testDate;

    @Column(name = "test_time")
    private LocalTime testTime;

    @Column(name = "test_location", length = 150)
    private String testLocation;

    @Column(name = "result", length = 20)
    private String result = "Scheduled";

    @Column(name = "remarks", length = 255)
    private String remarks;

    public DrivingTest() {}

    public DrivingTest(Application application, LocalDate testDate, LocalTime testTime,
                       String testLocation, String result, String remarks) {
        this.application = application;
        this.testDate = testDate;
        this.testTime = testTime;
        this.testLocation = testLocation;
        this.result = result != null ? result : "Scheduled";
        this.remarks = remarks;
    }

    public Integer getTestId() { return testId; }
    public void setTestId(Integer testId) { this.testId = testId; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public LocalDate getTestDate() { return testDate; }
    public void setTestDate(LocalDate testDate) { this.testDate = testDate; }

    public LocalTime getTestTime() { return testTime; }
    public void setTestTime(LocalTime testTime) { this.testTime = testTime; }

    public String getTestLocation() { return testLocation; }
    public void setTestLocation(String testLocation) { this.testLocation = testLocation; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
