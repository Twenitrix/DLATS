USE dlats;


-- =========================================
-- 1. VIEW ALL USERS
-- =========================================

SELECT * FROM USER;


-- =========================================
-- 2. VIEW ALL ADMINS
-- =========================================

SELECT * FROM ADMIN;


-- =========================================
-- 3. VIEW ALL VEHICLE CLASSES
-- =========================================

SELECT * FROM VEHICLE_CLASS;


-- =========================================
-- 4. VIEW ALL APPLICATIONS
-- =========================================

SELECT * FROM APPLICATION;


-- =========================================
-- 5. APPLICATION DETAILS
-- User + Application + Vehicle Class
-- =========================================

SELECT
    A.application_no,
    U.full_name,
    U.email,
    U.phone,
    V.class_name,
    A.application_type,
    A.application_date,
    A.current_status
FROM APPLICATION A
JOIN USER U
    ON A.user_id = U.user_id
JOIN VEHICLE_CLASS V
    ON A.class_id = V.class_id;


-- =========================================
-- 6. TRACK APPLICATION STATUS
-- =========================================

SELECT
    A.application_no,
    S.status,
    S.remarks,
    S.updated_at
FROM APPLICATION A
JOIN APPLICATION_STATUS S
    ON A.application_id = S.application_id
WHERE A.application_no = 'DL202600101'
ORDER BY S.updated_at;


-- =========================================
-- 7. VIEW DOCUMENTS OF AN APPLICATION
-- =========================================

SELECT
    A.application_no,
    U.full_name,
    D.document_type,
    D.document_path,
    D.verification_status,
    D.remarks,
    D.uploaded_at,
    D.verified_at
FROM DOCUMENT D
JOIN APPLICATION A
    ON D.application_id = A.application_id
JOIN USER U
    ON A.user_id = U.user_id
WHERE A.application_no = 'DL202600101';


-- =========================================
-- 8. VIEW DOCUMENTS WITH VERIFIER
-- =========================================

SELECT
    A.application_no,
    D.document_type,
    D.verification_status,
    AD.admin_name AS verified_by
FROM DOCUMENT D
JOIN APPLICATION A
    ON D.application_id = A.application_id
LEFT JOIN ADMIN AD
    ON D.verified_by = AD.admin_id;


-- =========================================
-- 9. VIEW DRIVING TEST DETAILS
-- =========================================

SELECT
    A.application_no,
    U.full_name,
    D.test_date,
    D.test_time,
    D.test_location,
    D.result,
    D.remarks
FROM DRIVING_TEST D
JOIN APPLICATION A
    ON D.application_id = A.application_id
JOIN USER U
    ON A.user_id = U.user_id;


-- =========================================
-- 10. VIEW LICENSE DETAILS
-- =========================================

SELECT
    U.full_name,
    A.application_no,
    L.license_number,
    L.issue_date,
    L.expiry_date,
    L.license_status
FROM LICENSE L
JOIN APPLICATION A
    ON L.application_id = A.application_id
JOIN USER U
    ON A.user_id = U.user_id;


-- =========================================
-- 11. SEARCH APPLICATION BY APPLICATION NUMBER
-- =========================================

SELECT
    A.application_no,
    U.full_name,
    A.current_status
FROM APPLICATION A
JOIN USER U
    ON A.user_id = U.user_id
WHERE A.application_no = 'DL202600101';


-- =========================================
-- 12. VIEW PENDING DOCUMENTS
-- =========================================

SELECT
    A.application_no,
    U.full_name,
    D.document_type,
    D.verification_status
FROM DOCUMENT D
JOIN APPLICATION A
    ON D.application_id = A.application_id
JOIN USER U
    ON A.user_id = U.user_id
WHERE D.verification_status = 'Pending';


-- =========================================
-- 13. VIEW APPLICATIONS BY STATUS
-- =========================================

SELECT
    A.application_no,
    U.full_name,
    A.current_status
FROM APPLICATION A
JOIN USER U
    ON A.user_id = U.user_id
WHERE A.current_status = 'Document Verification';


-- =========================================
-- 14. COUNT TOTAL APPLICATIONS
-- =========================================

SELECT COUNT(*) AS total_applications
FROM APPLICATION;


-- =========================================
-- 15. COUNT APPLICATIONS BY STATUS
-- =========================================

SELECT
    current_status,
    COUNT(*) AS total
FROM APPLICATION
GROUP BY current_status;


-- =========================================
-- 16. VIEW APPLICATIONS FOR A PARTICULAR USER
-- =========================================

SELECT
    A.application_no,
    V.class_name,
    A.application_type,
    A.application_date,
    A.current_status
FROM APPLICATION A
JOIN VEHICLE_CLASS V
    ON A.class_id = V.class_id
WHERE A.user_id = 1;


-- =========================================
-- 17. UPDATE APPLICATION STATUS
-- =========================================

UPDATE APPLICATION
SET current_status = 'Driving Test'
WHERE application_no = 'DL202600101';


-- =========================================
-- 18. ADD STATUS HISTORY
-- =========================================

INSERT INTO APPLICATION_STATUS
(application_id, admin_id, status, remarks)
VALUES
(
    1,
    1,
    'Driving Test',
    'Application moved to driving test stage'
);


-- =========================================
-- 19. VERIFY A DOCUMENT
-- =========================================

UPDATE DOCUMENT
SET
    verification_status = 'Verified',
    remarks = 'Document verified successfully',
    verified_by = 2,
    verified_at = CURRENT_TIMESTAMP
WHERE document_id = 3;


-- =========================================
-- 20. REJECT AN APPLICATION
-- =========================================

UPDATE APPLICATION
SET current_status = 'Rejected'
WHERE application_no = 'DL202600102';


-- =========================================
-- 21. RECORD REJECTION REASON
-- =========================================

INSERT INTO APPLICATION_STATUS
(application_id, admin_id, status, remarks)
VALUES
(
    2,
    1,
    'Rejected',
    'Required document could not be verified'
);