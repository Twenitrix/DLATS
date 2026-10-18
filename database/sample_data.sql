USE dlats;


-- =========================================
-- USER SAMPLE DATA
-- =========================================

INSERT INTO USER
(full_name, email, password, phone, date_of_birth,
 gender, address, city, state, pincode)
VALUES
(
    'Rahul Sharma',
    'rahul@example.com',
    'rahul123',
    '9876543210',
    '2000-05-15',
    'Male',
    'Andheri West',
    'Mumbai',
    'Maharashtra',
    '400058'
),
(
    'Priya Patil',
    'priya@example.com',
    'priya123',
    '9876501234',
    '1999-08-20',
    'Female',
    'Kothrud',
    'Pune',
    'Maharashtra',
    '411038'
);


-- =========================================
-- ADMIN SAMPLE DATA
-- =========================================

INSERT INTO ADMIN
(admin_name, email, password, role)
VALUES
(
    'RTO Admin',
    'admin@dlats.com',
    'admin123',
    'Admin'
),
(
    'Verification Officer',
    'officer@dlats.com',
    'officer123',
    'Officer'
);


-- =========================================
-- VEHICLE CLASS SAMPLE DATA
-- =========================================

INSERT INTO VEHICLE_CLASS
(class_code, class_name, description)
VALUES
(
    'MCWOG',
    'Motorcycle Without Gear',
    'Two wheeler without gear'
),
(
    'MCWG',
    'Motorcycle With Gear',
    'Two wheeler with gear'
),
(
    'LMV',
    'Light Motor Vehicle',
    'Cars and other light motor vehicles'
),
(
    'HMV',
    'Heavy Motor Vehicle',
    'Heavy motor vehicles'
);


-- =========================================
-- APPLICATION SAMPLE DATA
-- =========================================

INSERT INTO APPLICATION
(application_no, user_id, class_id, application_type,
 application_date, current_status)
VALUES
(
    'DL202600101',
    1,
    3,
    'New Driving License',
    '2026-09-10',
    'Document Verification'
),
(
    'DL202600102',
    2,
    2,
    'New Driving License',
    '2026-09-11',
    'Application Submitted'
);


-- =========================================
-- APPLICATION STATUS SAMPLE DATA
-- =========================================

INSERT INTO APPLICATION_STATUS
(application_id, admin_id, status, remarks)
VALUES
(
    1,
    NULL,
    'Application Submitted',
    'Application submitted successfully'
),
(
    1,
    1,
    'Document Verification',
    'Application is under document verification'
),
(
    2,
    NULL,
    'Application Submitted',
    'Application submitted successfully'
);


-- =========================================
-- DOCUMENT SAMPLE DATA
-- =========================================

INSERT INTO DOCUMENT
(application_id, document_type, document_path,
 verification_status, remarks, verified_by, verified_at)
VALUES
(
    1,
    'Aadhaar Card',
    'uploads/aadhaar_1.pdf',
    'Verified',
    'Identity verified successfully',
    2,
    '2026-09-12 11:30:00'
),
(
    1,
    'Address Proof',
    'uploads/address_1.pdf',
    'Verified',
    'Address proof verified',
    2,
    '2026-09-12 11:35:00'
),
(
    1,
    'Age Proof',
    'uploads/age_1.pdf',
    'Pending',
    'Awaiting verification',
    NULL,
    NULL
),
(
    2,
    'Aadhaar Card',
    'uploads/aadhaar_2.pdf',
    'Pending',
    'Document submitted for verification',
    NULL,
    NULL
);


-- =========================================
-- DRIVING TEST SAMPLE DATA
-- =========================================

INSERT INTO DRIVING_TEST
(application_id, test_date, test_time,
 test_location, result, remarks)
VALUES
(
    1,
    '2026-10-15',
    '10:30:00',
    'RTO Mumbai',
    'Scheduled',
    'Driving test scheduled'
);


-- =========================================
-- LICENSE SAMPLE DATA
-- =========================================

INSERT INTO LICENSE
(application_id, license_number,
 issue_date, expiry_date, license_status)
VALUES
(
    1,
    'MH01DL20261001',
    '2026-11-01',
    '2046-11-01',
    'Active'
);