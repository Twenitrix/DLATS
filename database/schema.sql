CREATE DATABASE IF NOT EXISTS dlats;

USE dlats;

-- =========================================
-- DROP TABLES
-- =========================================

DROP TABLE IF EXISTS DOCUMENT;
DROP TABLE IF EXISTS LICENSE;
DROP TABLE IF EXISTS DRIVING_TEST;
DROP TABLE IF EXISTS APPLICATION_STATUS;
DROP TABLE IF EXISTS APPLICATION;
DROP TABLE IF EXISTS VEHICLE_CLASS;
DROP TABLE IF EXISTS ADMIN;
DROP TABLE IF EXISTS USER;


-- =========================================
-- USER TABLE
-- =========================================

CREATE TABLE USER (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    date_of_birth DATE,
    gender VARCHAR(10),
    address VARCHAR(255),
    city VARCHAR(50),
    state VARCHAR(50),
    pincode VARCHAR(10),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- =========================================
-- ADMIN TABLE
-- =========================================

CREATE TABLE ADMIN (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) DEFAULT 'Admin',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- =========================================
-- VEHICLE CLASS TABLE
-- =========================================

CREATE TABLE VEHICLE_CLASS (
    class_id INT PRIMARY KEY AUTO_INCREMENT,
    class_code VARCHAR(20) NOT NULL UNIQUE,
    class_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);


-- =========================================
-- APPLICATION TABLE
-- =========================================

CREATE TABLE APPLICATION (
    application_id INT PRIMARY KEY AUTO_INCREMENT,
    application_no VARCHAR(30) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    class_id INT NOT NULL,
    application_type VARCHAR(50) NOT NULL,
    application_date DATE NOT NULL,
    current_status VARCHAR(50) NOT NULL
        DEFAULT 'Application Submitted',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES USER(user_id),

    FOREIGN KEY (class_id)
        REFERENCES VEHICLE_CLASS(class_id)
);


-- =========================================
-- APPLICATION STATUS TABLE
-- =========================================

CREATE TABLE APPLICATION_STATUS (
    status_id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT NOT NULL,
    admin_id INT,
    status VARCHAR(50) NOT NULL,
    remarks VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (application_id)
        REFERENCES APPLICATION(application_id),

    FOREIGN KEY (admin_id)
        REFERENCES ADMIN(admin_id)
);


-- =========================================
-- DOCUMENT TABLE
-- =========================================

CREATE TABLE DOCUMENT (
    document_id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_path VARCHAR(255),
    verification_status VARCHAR(30) DEFAULT 'Pending',
    remarks VARCHAR(255),
    verified_by INT,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    verified_at DATETIME,

    FOREIGN KEY (application_id)
        REFERENCES APPLICATION(application_id),

    FOREIGN KEY (verified_by)
        REFERENCES ADMIN(admin_id)
);


-- =========================================
-- DRIVING TEST TABLE
-- =========================================

CREATE TABLE DRIVING_TEST (
    test_id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT NOT NULL,
    test_date DATE,
    test_time TIME,
    test_location VARCHAR(150),
    result VARCHAR(20) DEFAULT 'Scheduled',
    remarks VARCHAR(255),

    FOREIGN KEY (application_id)
        REFERENCES APPLICATION(application_id)
);


-- =========================================
-- LICENSE TABLE
-- =========================================

CREATE TABLE LICENSE (
    license_id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT NOT NULL UNIQUE,
    license_number VARCHAR(30) NOT NULL UNIQUE,
    issue_date DATE,
    expiry_date DATE,
    license_status VARCHAR(20) DEFAULT 'Active',

    FOREIGN KEY (application_id)
        REFERENCES APPLICATION(application_id)
);