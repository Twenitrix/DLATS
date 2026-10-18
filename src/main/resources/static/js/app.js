/**
 * DLATS (DriveTrack) — Frontend Application Controller & REST API Client
 * Connects all Applicant and Admin HTML pages to the Spring Boot 3 /api/v1 backend.
 */

const API_BASE = '/api/v1';

const STAGES = [
    {
        name: 'Application Submitted',
        desc: 'Application has been successfully submitted.'
    },
    {
        name: 'Document Verification',
        desc: 'Your application and documents are being reviewed.'
    },
    {
        name: 'Driving Test',
        desc: 'Driving test scheduled and conducted at RTO.'
    },
    {
        name: 'License Issued',
        desc: 'Driving license issued after successful completion.'
    }
];

function getAuthToken() {
    return localStorage.getItem('dlats_token');
}

function setAuthSession(data) {
    if (data && data.token) {
        localStorage.setItem('dlats_token', data.token);
        localStorage.setItem('dlats_user', JSON.stringify(data));
    }
}

function getAuthUser() {
    try {
        const raw = localStorage.getItem('dlats_user');
        return raw ? JSON.parse(raw) : null;
    } catch (e) {
        return null;
    }
}

function clearAuthSession() {
    localStorage.removeItem('dlats_token');
    localStorage.removeItem('dlats_user');
}

async function apiRequest(path, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };
    const token = getAuthToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers
    });

    const payload = await response.json().catch(() => ({
        success: false,
        message: 'Unexpected server response'
    }));

    if (!response.ok || payload.success === false) {
        throw new Error(payload.message || 'Request failed');
    }
    return payload.data;
}

function showAlert(message, type = 'error') {
    const box = document.getElementById('alert-box');
    if (!box) {
        alert(message);
        return;
    }
    box.textContent = message;
    box.className = `alert-box ${type}`;
    box.style.display = 'block';
}

function hideAlert() {
    const box = document.getElementById('alert-box');
    if (box) box.style.display = 'none';
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    try {
        const d = new Date(dateStr);
        if (isNaN(d.getTime())) return dateStr;
        return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' });
    } catch (e) {
        return dateStr;
    }
}

function formatDateTime(dtStr) {
    if (!dtStr) return '';
    try {
        const d = new Date(dtStr);
        if (isNaN(d.getTime())) return dtStr;
        return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    } catch (e) {
        return dtStr;
    }
}

function stageIndex(status) {
    if (!status) return 0;
    const s = status.toLowerCase();
    if (s.includes('submitted')) return 0;
    if (s.includes('verification')) return 1;
    if (s.includes('test')) return 2;
    if (s.includes('issued') || s.includes('approved')) return 3;
    if (s.includes('rejected')) return -1;
    return 0;
}

function getQueryParam(key) {
    const params = new URLSearchParams(window.location.search);
    return params.get(key);
}

// ======================================================
// 1. APPLICANT LOGIN & REGISTER
// ======================================================
function initApplicantLogin() {
    const form = document.getElementById('applicant-login-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        hideAlert();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        try {
            const data = await apiRequest('/auth/applicant/login', {
                method: 'POST',
                body: JSON.stringify({ email, password })
            });
            setAuthSession(data);
            window.location.href = 'dashboard.html';
        } catch (err) {
            showAlert(err.message, 'error');
        }
    });
}

function initApplicantRegister() {
    const form = document.getElementById('applicant-register-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        hideAlert();

        const fullName = document.getElementById('fullName').value.trim();
        const email = document.getElementById('email').value.trim();
        const phone = document.getElementById('phone').value.trim();
        const dateOfBirth = document.getElementById('dateOfBirth').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (password !== confirmPassword) {
            showAlert('Passwords do not match', 'error');
            return;
        }

        try {
            const data = await apiRequest('/auth/applicant/register', {
                method: 'POST',
                body: JSON.stringify({ fullName, email, phone, dateOfBirth, password })
            });
            setAuthSession(data);
            window.location.href = 'dashboard.html';
        } catch (err) {
            showAlert(err.message, 'error');
        }
    });
}

// ======================================================
// 2. APPLICANT DASHBOARD
// ======================================================
async function initApplicantDashboard() {
    const user = getAuthUser();
    const welcomeEl = document.getElementById('welcome-user-text');
    if (welcomeEl && user && user.name) {
        welcomeEl.textContent = `Welcome back, ${user.name}`;
    }

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => clearAuthSession());
    }

    const container = document.getElementById('my-applications-list');
    if (!container) return;

    try {
        const apps = await apiRequest('/applications/my');
        if (!apps || apps.length === 0) {
            container.innerHTML = `
                <div class="application-box">
                    <div>
                        <span>No Applications Yet</span>
                        <strong>You haven't submitted a driving license application yet.</strong>
                    </div>
                </div>
                <a class="details-link" href="application-form.html">Start Your First Application</a>
            `;
            return;
        }

        const trackLink = document.getElementById('track-app-link');
        if (trackLink) {
            trackLink.href = `track-application.html?appNo=${encodeURIComponent(apps[0].applicationNo)}`;
        }

        container.innerHTML = apps.map(app => `
            <div style="margin-bottom: 22px;">
                <div class="application-box">
                    <div>
                        <span>Application Number</span>
                        <strong>${app.applicationNo}</strong>
                    </div>
                    <div>
                        <span>Application Type</span>
                        <strong>${app.applicationType}</strong>
                    </div>
                    <div>
                        <span>Vehicle Class</span>
                        <strong>${app.classCode} (${app.className})</strong>
                    </div>
                    <div>
                        <span>Current Status</span>
                        <strong class="status" style="color:${app.currentStatus === 'Rejected' ? '#d93025' : app.currentStatus === 'License Issued' ? '#218838' : '#1e5eff'}">
                            ${app.currentStatus}
                        </strong>
                    </div>
                </div>
                <div style="display:flex; gap:10px;">
                    <a class="details-link" href="application-details.html?appNo=${encodeURIComponent(app.applicationNo)}">
                        View Application Details
                    </a>
                    <a class="details-link" style="background:#1e5eff;" href="track-application.html?appNo=${encodeURIComponent(app.applicationNo)}">
                        Track Timeline
                    </a>
                </div>
            </div>
        `).join('');
    } catch (err) {
        console.error(err);
    }
}

// ======================================================
// 3. APPLICANT PROFILE
// ======================================================
async function initApplicantProfile() {
    const viewContainer = document.getElementById('profile-view-container');
    const editForm = document.getElementById('profile-edit-form');
    const toggleBtn = document.getElementById('toggle-edit-profile-btn');
    const cancelBtn = document.getElementById('cancel-edit-profile-btn');

    let currentProfile = null;

    function renderProfile(p) {
        currentProfile = p;
        document.getElementById('prof-fullName').textContent = p.fullName || '-';
        document.getElementById('prof-email').textContent = p.email || '-';
        document.getElementById('prof-phone').textContent = p.phone || '-';
        document.getElementById('prof-dob').textContent = formatDate(p.dateOfBirth);
        document.getElementById('prof-gender').textContent = p.gender || 'Not specified';
        document.getElementById('prof-address').textContent = p.address || 'Not specified';
        document.getElementById('prof-city').textContent = p.city || 'Not specified';
        document.getElementById('prof-state').textContent = p.state || 'Not specified';
        document.getElementById('prof-pincode').textContent = p.pincode || 'Not specified';

        document.getElementById('edit-fullName').value = p.fullName || '';
        document.getElementById('edit-phone').value = p.phone || '';
        document.getElementById('edit-dob').value = p.dateOfBirth || '';
        document.getElementById('edit-gender').value = p.gender || 'Male';
        document.getElementById('edit-address').value = p.address || '';
        document.getElementById('edit-city').value = p.city || '';
        document.getElementById('edit-state').value = p.state || '';
        document.getElementById('edit-pincode').value = p.pincode || '';
    }

    try {
        const profile = await apiRequest('/users/profile');
        renderProfile(profile);
    } catch (err) {
        showAlert(err.message, 'error');
    }

    if (toggleBtn) {
        toggleBtn.addEventListener('click', () => {
            hideAlert();
            viewContainer.style.display = 'none';
            editForm.style.display = 'block';
            toggleBtn.style.display = 'none';
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            editForm.style.display = 'none';
            viewContainer.style.display = 'block';
            toggleBtn.style.display = 'inline-block';
        });
    }

    if (editForm) {
        editForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            hideAlert();
            try {
                const updated = await apiRequest('/users/profile', {
                    method: 'PUT',
                    body: JSON.stringify({
                        fullName: document.getElementById('edit-fullName').value.trim(),
                        phone: document.getElementById('edit-phone').value.trim(),
                        dateOfBirth: document.getElementById('edit-dob').value || null,
                        gender: document.getElementById('edit-gender').value,
                        address: document.getElementById('edit-address').value.trim(),
                        city: document.getElementById('edit-city').value.trim(),
                        state: document.getElementById('edit-state').value.trim(),
                        pincode: document.getElementById('edit-pincode').value.trim()
                    })
                });
                renderProfile(updated);
                editForm.style.display = 'none';
                viewContainer.style.display = 'block';
                toggleBtn.style.display = 'inline-block';
                showAlert('Profile updated successfully!', 'success');
            } catch (err) {
                showAlert(err.message, 'error');
            }
        });
    }
}

// ======================================================
// 4. LICENSE APPLICATION FORM
// ======================================================
async function initApplicationForm() {
    const form = document.getElementById('license-application-form');
    if (!form) return;

    // Pre-fill from logged in applicant profile
    try {
        const profile = await apiRequest('/users/profile');
        if (profile) {
            if (profile.fullName) document.getElementById('fullName').value = profile.fullName;
            if (profile.dateOfBirth) document.getElementById('dateOfBirth').value = profile.dateOfBirth;
            if (profile.gender) document.getElementById('gender').value = profile.gender;
            if (profile.phone) document.getElementById('phone').value = profile.phone;
            if (profile.email) document.getElementById('email').value = profile.email;
            if (profile.address) document.getElementById('address').value = profile.address;
            if (profile.city) document.getElementById('city').value = profile.city;
            if (profile.state) document.getElementById('state').value = profile.state;
            if (profile.pincode) document.getElementById('pincode').value = profile.pincode;
        }
    } catch (e) {
        // Ignore if not logged in
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        hideAlert();

        const aadhaarInput = document.getElementById('aadhaarFile');
        const addressInput = document.getElementById('addressFile');
        const ageInput = document.getElementById('ageFile');

        const payload = {
            fullName: document.getElementById('fullName').value.trim(),
            dateOfBirth: document.getElementById('dateOfBirth').value || null,
            gender: document.getElementById('gender').value,
            phone: document.getElementById('phone').value.trim(),
            email: document.getElementById('email').value.trim(),
            address: document.getElementById('address').value.trim(),
            city: document.getElementById('city').value.trim(),
            state: document.getElementById('state').value.trim(),
            pincode: document.getElementById('pincode').value.trim(),
            applicationType: document.getElementById('applicationType').value,
            classCode: document.getElementById('classCode').value,
            aadhaarDocName: aadhaarInput && aadhaarInput.files[0] ? aadhaarInput.files[0].name : null,
            addressDocName: addressInput && addressInput.files[0] ? addressInput.files[0].name : null,
            ageDocName: ageInput && ageInput.files[0] ? ageInput.files[0].name : null
        };

        try {
            const created = await apiRequest('/applications', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            window.location.href = `application-details.html?appNo=${encodeURIComponent(created.applicationNo)}`;
        } catch (err) {
            showAlert(err.message, 'error');
        }
    });
}

// ======================================================
// 5. APPLICANT APPLICATION DETAILS
// ======================================================
async function initApplicantApplicationDetails() {
    let appNo = getQueryParam('appNo');
    try {
        if (!appNo) {
            const myApps = await apiRequest('/applications/my');
            if (myApps && myApps.length > 0) {
                appNo = myApps[0].applicationNo;
            } else {
                appNo = 'DL202600101';
            }
        }

        const data = await apiRequest(`/applications/${encodeURIComponent(appNo)}`);
        document.getElementById('detail-app-no').textContent = data.applicationNo;
        const statusBadge = document.getElementById('detail-status-badge');
        statusBadge.textContent = data.currentStatus;

        const u = data.applicant || {};
        document.getElementById('detail-fullName').textContent = u.fullName || '-';
        document.getElementById('detail-dob').textContent = formatDate(u.dateOfBirth);
        document.getElementById('detail-gender').textContent = u.gender || '-';
        document.getElementById('detail-phone').textContent = u.phone || '-';
        document.getElementById('detail-email').textContent = u.email || '-';
        document.getElementById('detail-address').textContent = u.address || '-';
        document.getElementById('detail-city').textContent = u.city || '-';
        document.getElementById('detail-state').textContent = u.state || '-';
        document.getElementById('detail-pincode').textContent = u.pincode || '-';

        document.getElementById('detail-appType').textContent = data.applicationType;
        document.getElementById('detail-vehicleClass').textContent = data.vehicleClass
            ? `${data.vehicleClass.classCode} (${data.vehicleClass.className})`
            : '-';
        document.getElementById('detail-appDate').textContent = formatDate(data.applicationDate);

        // Render Documents
        const docList = document.getElementById('detail-documents-list');
        if (docList && data.documents) {
            docList.innerHTML = data.documents.map((d, i) => {
                const badgeClass = d.verificationStatus === 'Verified'
                    ? 'badge-verified'
                    : d.verificationStatus === 'Rejected'
                    ? 'badge-rejected'
                    : 'badge-pending';
                return `
                    <div class="progress-item ${d.verificationStatus === 'Verified' ? 'completed' : ''}" style="justify-content:space-between; align-items:center;">
                        <div style="display:flex; gap:15px; align-items:center;">
                            <span>${i + 1}</span>
                            <div>
                                <strong>${d.documentType}</strong>
                                <p>${d.documentPath || ''} ${d.remarks ? '— ' + d.remarks : ''}</p>
                            </div>
                        </div>
                        <span class="${badgeClass}">${d.verificationStatus}</span>
                    </div>
                `;
            }).join('');
        }

        // Render Driving Test
        const testBox = document.getElementById('detail-driving-test-box');
        if (testBox) {
            if (data.drivingTests && data.drivingTests.length > 0) {
                const t = data.drivingTests[0];
                testBox.innerHTML = `
                    <div><span>Test Date</span><strong>${formatDate(t.testDate)}</strong></div>
                    <div><span>Test Time</span><strong>${t.testTime || '-'}</strong></div>
                    <div><span>Test Location</span><strong>${t.testLocation || '-'}</strong></div>
                    <div><span>Result</span><strong style="color:#1e5eff;">${t.result || 'Scheduled'}</strong></div>
                `;
            } else {
                testBox.innerHTML = `<div><span>Status</span><strong>Driving test will be scheduled after document verification.</strong></div>`;
            }
        }

        // Render Issued License if available
        const licSection = document.getElementById('detail-license-section');
        const licBox = document.getElementById('detail-license-box');
        if (licSection && licBox && data.license) {
            licSection.style.display = 'block';
            licBox.innerHTML = `
                <div><span>License Number</span><strong style="color:#218838; font-size:15px;">${data.license.licenseNumber}</strong></div>
                <div><span>Issue Date</span><strong>${formatDate(data.license.issueDate)}</strong></div>
                <div><span>Expiry Date</span><strong>${formatDate(data.license.expiryDate)}</strong></div>
                <div><span>License Status</span><strong class="badge-verified">${data.license.licenseStatus}</strong></div>
            `;
        }

        // Render 4-Stage Progress List
        renderProgressTimeline('detail-progress-list', data, 'progress-item');
    } catch (err) {
        console.error(err);
    }
}

function renderProgressTimeline(containerId, data, itemClass) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const currentIdx = stageIndex(data.currentStatus);
    const historyMap = {};
    if (data.statusHistory) {
        data.statusHistory.forEach(h => {
            historyMap[h.status.toLowerCase()] = h;
        });
    }

    if (currentIdx === -1) {
        // Rejected state
        const rejectedEntry = historyMap['rejected'];
        container.innerHTML = `
            <div class="${itemClass} completed">
                <span class="${itemClass === 'timeline-item' ? 'timeline-circle' : ''}">1</span>
                <div>
                    <strong>Application Submitted</strong>
                    <p>Application was submitted by the applicant.</p>
                </div>
            </div>
            <div class="${itemClass} current">
                <span class="${itemClass === 'timeline-item' ? 'timeline-circle' : ''}" style="background:#d93025; color:white;">!</span>
                <div>
                    <strong style="color:#d93025;">Application Rejected</strong>
                    <p>${rejectedEntry && rejectedEntry.remarks ? rejectedEntry.remarks : 'Application was rejected by RTO Admin.'}</p>
                    ${rejectedEntry ? `<span>${formatDateTime(rejectedEntry.updatedAt)}</span>` : ''}
                </div>
            </div>
        `;
        return;
    }

    container.innerHTML = STAGES.map((stage, idx) => {
        let stateClass = '';
        if (idx < currentIdx || data.currentStatus === 'License Issued') {
            stateClass = 'completed';
        } else if (idx === currentIdx) {
            stateClass = 'current';
        }
        const h = historyMap[stage.name.toLowerCase()];
        const desc = h && h.remarks ? h.remarks : stage.desc;
        const dateSpan = h && h.updatedAt ? `<span>${formatDateTime(h.updatedAt)}</span>` : '';

        if (itemClass === 'timeline-item') {
            return `
                <div class="timeline-item ${stateClass}">
                    <div class="timeline-circle">${idx + 1}</div>
                    <div>
                        <strong>${stage.name}</strong>
                        <p>${desc}</p>
                        ${dateSpan}
                    </div>
                </div>
            `;
        }

        return `
            <div class="${itemClass} ${stateClass}">
                <span>${idx + 1}</span>
                <div>
                    <strong>${stage.name}</strong>
                    <p>${desc}</p>
                </div>
            </div>
        `;
    }).join('');
}

// ======================================================
// 6. TRACK APPLICATION PAGE
// ======================================================
async function initTrackApplication() {
    const input = document.getElementById('track-input-no');
    const btn = document.getElementById('track-btn');
    const qAppNo = getQueryParam('appNo');
    if (qAppNo && input) {
        input.value = qAppNo;
    }

    async function loadTracking(appNo) {
        hideAlert();
        try {
            const data = await apiRequest(`/applications/track/${encodeURIComponent(appNo)}`);
            document.getElementById('track-app-no').textContent = data.applicationNo;
            document.getElementById('track-current-status').textContent = data.currentStatus;
            renderProgressTimeline('track-timeline', data, 'timeline-item');
        } catch (err) {
            showAlert(err.message, 'error');
        }
    }

    if (btn && input) {
        btn.addEventListener('click', () => {
            if (input.value.trim()) loadTracking(input.value.trim());
        });
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && input.value.trim()) {
                e.preventDefault();
                loadTracking(input.value.trim());
            }
        });
    }

    if (input && input.value.trim()) {
        loadTracking(input.value.trim());
    }
}

// ======================================================
// 7. ADMIN LOGIN & DASHBOARD
// ======================================================
function initAdminLogin() {
    const form = document.getElementById('admin-login-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        hideAlert();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        try {
            const data = await apiRequest('/auth/admin/login', {
                method: 'POST',
                body: JSON.stringify({ email, password })
            });
            setAuthSession(data);
            window.location.href = 'dashboard.html';
        } catch (err) {
            showAlert(err.message, 'error');
        }
    });
}

async function initAdminDashboard() {
    const user = getAuthUser();
    const roleEl = document.getElementById('admin-role-text');
    if (roleEl && user && user.name) {
        roleEl.textContent = `Admin Portal — ${user.name} (${user.role || 'Admin'})`;
    }

    const logoutBtn = document.getElementById('admin-logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => clearAuthSession());
    }

    const searchInput = document.getElementById('admin-search-input');
    const searchBtn = document.getElementById('admin-search-btn');

    async function loadDashboard(query = '') {
        try {
            const qParam = query ? `?search=${encodeURIComponent(query)}` : '';
            const data = await apiRequest(`/admin/dashboard${qParam}`);

            document.getElementById('kpi-total').textContent = data.totalApplications;
            document.getElementById('kpi-pending').textContent = data.pendingApplications;
            document.getElementById('kpi-approved').textContent = data.approvedApplications;
            document.getElementById('kpi-rejected').textContent = data.rejectedApplications;

            const table = document.getElementById('admin-applications-table');
            const headingHtml = `
                <div class="table-row table-heading">
                    <span>Application No.</span>
                    <span>Applicant Name</span>
                    <span>Vehicle Class</span>
                    <span>Status</span>
                    <span>Action</span>
                </div>
            `;

            if (!data.applications || data.applications.length === 0) {
                table.innerHTML = headingHtml + `
                    <div class="table-row">
                        <span>No matching applications</span>
                        <span>-</span>
                        <span>-</span>
                        <span>-</span>
                        <span>-</span>
                    </div>
                `;
                return;
            }

            const rowsHtml = data.applications.map(app => {
                let statusClass = 'pending';
                if (app.currentStatus === 'License Issued' || app.currentStatus === 'Approved') {
                    statusClass = 'approved';
                } else if (app.currentStatus === 'Rejected') {
                    statusClass = 'rejected';
                }
                return `
                    <div class="table-row">
                        <span><strong>${app.applicationNo}</strong></span>
                        <span>${app.applicantName}</span>
                        <span>${app.classCode}</span>
                        <span class="${statusClass}">${app.currentStatus}</span>
                        <a href="application-details.html?appNo=${encodeURIComponent(app.applicationNo)}">View</a>
                    </div>
                `;
            }).join('');

            table.innerHTML = headingHtml + rowsHtml;
        } catch (err) {
            console.error(err);
        }
    }

    if (searchBtn && searchInput) {
        searchBtn.addEventListener('click', () => loadDashboard(searchInput.value.trim()));
        searchInput.addEventListener('keyup', (e) => {
            if (e.key === 'Enter') loadDashboard(searchInput.value.trim());
            if (searchInput.value === '') loadDashboard('');
        });
    }

    loadDashboard('');
}

// ======================================================
// 8. ADMIN APPLICATION MANAGEMENT DETAILS
// ======================================================
async function initAdminApplicationDetails() {
    const appNo = getQueryParam('appNo') || 'DL202600101';
    const nextStageBtn = document.getElementById('move-next-stage-btn');
    const openRejectBtn = document.getElementById('open-reject-modal-btn');
    const rejectModal = document.getElementById('reject-modal');
    const cancelRejectBtn = document.getElementById('cancel-reject-btn');
    const confirmRejectBtn = document.getElementById('confirm-reject-btn');
    const reasonInput = document.getElementById('rejection-reason-input');
    const testForm = document.getElementById('admin-test-form');

    function renderAdminDetails(data) {
        document.getElementById('admin-app-no').textContent = data.applicationNo;
        document.getElementById('admin-app-status').textContent = data.currentStatus;

        const u = data.applicant || {};
        document.getElementById('admin-fullName').textContent = u.fullName || '-';
        document.getElementById('admin-dob').textContent = formatDate(u.dateOfBirth);
        document.getElementById('admin-gender').textContent = u.gender || '-';
        document.getElementById('admin-phone').textContent = u.phone || '-';
        document.getElementById('admin-email').textContent = u.email || '-';
        document.getElementById('admin-address').textContent = u.address || '-';
        document.getElementById('admin-city').textContent = u.city || '-';
        document.getElementById('admin-state').textContent = u.state || '-';
        document.getElementById('admin-pincode').textContent = u.pincode || '-';

        document.getElementById('admin-appType').textContent = data.applicationType;
        document.getElementById('admin-vehicleClass').textContent = data.vehicleClass
            ? `${data.vehicleClass.classCode} (${data.vehicleClass.className})`
            : '-';
        document.getElementById('admin-appDate').textContent = formatDate(data.applicationDate);

        // Render Documents with Verify / Reject buttons
        const docsContainer = document.getElementById('admin-documents-list');
        if (docsContainer && data.documents) {
            docsContainer.innerHTML = data.documents.map((d, idx) => {
                const badgeClass = d.verificationStatus === 'Verified'
                    ? 'badge-verified'
                    : d.verificationStatus === 'Rejected'
                    ? 'badge-rejected'
                    : 'badge-pending';
                return `
                    <div class="status-item ${d.verificationStatus === 'Verified' ? 'completed' : ''}" style="justify-content:space-between; align-items:center;">
                        <div style="display:flex; gap:15px; align-items:center;">
                            <span>${idx + 1}</span>
                            <div>
                                <strong>${d.documentType}</strong>
                                <p>${d.documentPath || ''} ${d.remarks ? '— ' + d.remarks : ''} ${d.verifiedByAdmin ? '(by ' + d.verifiedByAdmin + ')' : ''}</p>
                            </div>
                        </div>
                        <div style="display:flex; gap:8px; align-items:center;">
                            <span class="${badgeClass}">${d.verificationStatus}</span>
                            <button type="button" class="btn-small btn-verify" onclick="window.verifyDoc(${d.documentId}, 'Verified')">Verify</button>
                            <button type="button" class="btn-small btn-reject-doc" onclick="window.verifyDoc(${d.documentId}, 'Rejected')">Reject</button>
                        </div>
                    </div>
                `;
            }).join('');
        }

        // Driving Test section
        const testInfo = document.getElementById('admin-driving-test-info');
        if (data.drivingTests && data.drivingTests.length > 0) {
            const t = data.drivingTests[0];
            if (testInfo) {
                testInfo.innerHTML = `<p style="font-size:13px; color:#1e5eff;"><strong>Current Scheduled Test:</strong> ${formatDate(t.testDate)} at ${t.testTime || '10:30'} — ${t.testLocation} (Result: <strong>${t.result}</strong>)</p>`;
            }
            if (t.testDate) document.getElementById('test-date-input').value = t.testDate;
            if (t.testTime) document.getElementById('test-time-input').value = t.testTime.substring(0, 5);
            if (t.testLocation) document.getElementById('test-location-input').value = t.testLocation;
            if (t.result) document.getElementById('test-result-input').value = t.result;
        } else {
            if (testInfo) {
                testInfo.innerHTML = `<p style="font-size:12px; color:#777;">No driving test scheduled yet. Schedule below or click "Move to Next Stage".</p>`;
            }
            document.getElementById('test-location-input').value = `RTO ${u.city || 'Mumbai'}`;
        }

        // License section
        const licSec = document.getElementById('admin-license-section');
        const licBox = document.getElementById('admin-license-box');
        if (licSec && licBox) {
            if (data.license) {
                licSec.style.display = 'block';
                licBox.innerHTML = `
                    <div><span>License Number</span><strong style="color:#218838;">${data.license.licenseNumber}</strong></div>
                    <div><span>Issue Date</span><strong>${formatDate(data.license.issueDate)}</strong></div>
                    <div><span>Expiry Date</span><strong>${formatDate(data.license.expiryDate)}</strong></div>
                    <div><span>Status</span><strong class="badge-verified">${data.license.licenseStatus}</strong></div>
                `;
            } else {
                licSec.style.display = 'none';
            }
        }

        // Status Timeline
        renderProgressTimeline('admin-status-timeline', data, 'status-item');

        // Button state
        if (nextStageBtn) {
            if (data.currentStatus === 'License Issued' || data.currentStatus === 'Rejected') {
                nextStageBtn.disabled = true;
                nextStageBtn.style.opacity = '0.5';
                nextStageBtn.textContent = data.currentStatus === 'License Issued' ? 'Completed (License Issued)' : 'Application Rejected';
            } else {
                nextStageBtn.disabled = false;
                nextStageBtn.style.opacity = '1';
                nextStageBtn.textContent = 'Move to Next Stage';
            }
        }
    }

    async function fetchDetails() {
        try {
            const data = await apiRequest(`/applications/${encodeURIComponent(appNo)}`);
            renderAdminDetails(data);
        } catch (err) {
            showAlert(err.message, 'error');
        }
    }

    window.verifyDoc = async (docId, status) => {
        hideAlert();
        try {
            await apiRequest(`/admin/documents/${docId}/verify`, {
                method: 'PATCH',
                body: JSON.stringify({
                    verificationStatus: status,
                    remarks: status === 'Verified' ? 'Verified by RTO Officer' : 'Document rejected by RTO Officer'
                })
            });
            await fetchDetails();
            showAlert(`Document marked as ${status}`, 'success');
        } catch (err) {
            showAlert(err.message, 'error');
        }
    };

    if (nextStageBtn) {
        nextStageBtn.addEventListener('click', async () => {
            hideAlert();
            try {
                const updated = await apiRequest(`/admin/applications/${encodeURIComponent(appNo)}/next-stage`, {
                    method: 'POST',
                    body: JSON.stringify({})
                });
                renderAdminDetails(updated);
                showAlert(`Application moved to stage: ${updated.currentStatus}`, 'success');
            } catch (err) {
                showAlert(err.message, 'error');
            }
        });
    }

    if (openRejectBtn && rejectModal) {
        openRejectBtn.addEventListener('click', () => {
            rejectModal.style.display = 'flex';
        });
    }

    if (cancelRejectBtn && rejectModal) {
        cancelRejectBtn.addEventListener('click', () => {
            rejectModal.style.display = 'none';
        });
    }

    if (confirmRejectBtn) {
        confirmRejectBtn.addEventListener('click', async () => {
            const reason = reasonInput ? reasonInput.value.trim() : '';
            if (!reason) {
                alert('Please enter a rejection reason');
                return;
            }
            try {
                const updated = await apiRequest(`/admin/applications/${encodeURIComponent(appNo)}/reject`, {
                    method: 'POST',
                    body: JSON.stringify({ reason })
                });
                rejectModal.style.display = 'none';
                renderAdminDetails(updated);
                showAlert('Application has been rejected.', 'error');
            } catch (err) {
                showAlert(err.message, 'error');
            }
        });
    }

    if (testForm) {
        testForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            hideAlert();
            try {
                const updated = await apiRequest(`/admin/applications/${encodeURIComponent(appNo)}/driving-test`, {
                    method: 'POST',
                    body: JSON.stringify({
                        testDate: document.getElementById('test-date-input').value,
                        testTime: document.getElementById('test-time-input').value + ':00',
                        testLocation: document.getElementById('test-location-input').value.trim(),
                        result: document.getElementById('test-result-input').value,
                        remarks: 'Updated by Admin'
                    })
                });
                renderAdminDetails(updated);
                showAlert('Driving test schedule & result saved!', 'success');
            } catch (err) {
                showAlert(err.message, 'error');
            }
        });
    }

    fetchDetails();
}

// ======================================================
// BOOTSTRAP ON DOM READY
// ======================================================
document.addEventListener('DOMContentLoaded', () => {
    initApplicantLogin();
    initApplicantRegister();
    initAdminLogin();

    const page = document.body.getAttribute('data-page');
    if (page === 'applicant-dashboard') initApplicantDashboard();
    if (page === 'applicant-profile') initApplicantProfile();
    if (page === 'application-form') initApplicationForm();
    if (page === 'applicant-application-details') initApplicantApplicationDetails();
    if (page === 'track-application') initTrackApplication();
    if (page === 'admin-dashboard') initAdminDashboard();
    if (page === 'admin-application-details') initAdminApplicationDetails();
});
