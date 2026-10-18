# Driving License Application Tracking System (DLATS) — Full-Stack Edition

A complete, working **Full-Stack Driving License Application Tracking System (DLATS)** built with **Spring Boot 3 (Java)** on the backend and **HTML5, CSS3, and Vanilla JavaScript** on the frontend.

---

## 🏗️ Architecture (`/claude` Layered Backend Preset)

```text
Controller Layer (@RestController)   → HTTP request/response handling (/api/v1/*)
        ↓
Service Layer (@Service)             → Business logic, stage transitions, license generation
        ↓
Repository Layer (@Repository)       → Spring Data JPA interfaces
        ↓
Database Layer (H2 / MySQL)          → 8 Relational Entities matching DLATS ER Diagram
```

### Database Entities (from ER Diagram)
1. **`USER` (`users`)** — Applicant personal & address details
2. **`ADMIN` (`admin`)** — RTO Admin and Verification Officer accounts
3. **`VEHICLE_CLASS` (`vehicle_class`)** — `MCWOG`, `MCWG`, `LMV`, `HMV`
4. **`APPLICATION` (`application`)** — Driving license applications (`DL202600101`, etc.)
5. **`APPLICATION_STATUS` (`application_status`)** — Audit trail of all stage updates & remarks
6. **`DOCUMENT` (`document`)** — Uploaded documents (`Aadhaar Card`, `Address Proof`, `Age Proof`) & verification state
7. **`DRIVING_TEST` (`driving_test`)** — Scheduled driving test date, time, RTO location & result
8. **`LICENSE` (`license`)** — Issued driving license number (`MH01DL...`), issue/expiry dates & status

---

## 🚀 Quick Start

```powershell
# Compile and start the server on http://localhost:8080
.\mvnw.cmd spring-boot:run
```

Open **http://localhost:8080** in your browser.

---

## 🔑 Pre-Seeded Demo Credentials

### Applicant Portal (`http://localhost:8080/applicant/login.html`)
| Name | Email | Password | Application No | Status |
|---|---|---|---|---|
| **Rahul Sharma** | `rahul@example.com` | `rahul123` | `DL202600101` | `Document Verification` |
| **Priya Patil** | `priya@example.com` | `priya123` | `DL202600102` | `License Issued` |
| **Arjun Mehta** | `arjun@example.com` | `arjun123` | `DL202600103` | `Rejected` |

### Admin / RTO Portal (`http://localhost:8080/admin/login.html`)
| Name | Email | Password | Role |
|---|---|---|---|
| **RTO Admin** | `admin@dlats.com` | `admin123` | `Admin` |
| **Verification Officer** | `officer@dlats.com` | `officer123` | `Officer` |
