package com.dlats.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_class")
public class VehicleClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Integer classId;

    @Column(name = "class_code", nullable = false, unique = true, length = 20)
    private String classCode;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @Column(name = "description", length = 255)
    private String description;

    public VehicleClass() {}

    public VehicleClass(String classCode, String className, String description) {
        this.classCode = classCode;
        this.className = className;
        this.description = description;
    }

    public Integer getClassId() { return classId; }
    public void setClassId(Integer classId) { this.classId = classId; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
