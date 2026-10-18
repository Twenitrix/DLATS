package com.dlats.service;

import com.dlats.config.JwtAuthFilter.AuthenticatedPrincipal;
import com.dlats.config.JwtService;
import com.dlats.dto.AuthDtos.*;
import com.dlats.exception.CustomException;
import com.dlats.model.Admin;
import com.dlats.model.User;
import com.dlats.repository.AdminRepository;
import com.dlats.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       AdminRepository adminRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthTokenResponse registerApplicant(ApplicantRegisterRequest req) {
        if (req.email() == null || req.email().isBlank() || req.password() == null || req.password().isBlank()) {
            throw CustomException.badRequest("Email and password are required");
        }
        if (userRepository.existsByEmailIgnoreCase(req.email().trim())) {
            throw CustomException.badRequest("An applicant with this email already exists");
        }

        User user = new User(
                req.fullName() != null && !req.fullName().isBlank() ? req.fullName().trim() : "Applicant",
                req.email().trim().toLowerCase(),
                passwordEncoder.encode(req.password()),
                req.phone(),
                req.dateOfBirth(),
                req.gender(),
                req.address(),
                req.city(),
                req.state(),
                req.pincode()
        );
        user = userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), "APPLICANT", user.getUserId(), "Applicant");
        return new AuthTokenResponse(token, "APPLICANT", user.getUserId(), user.getFullName(), user.getEmail(), "Applicant");
    }

    public AuthTokenResponse loginApplicant(LoginRequest req) {
        if (req.email() == null || req.password() == null) {
            throw CustomException.badRequest("Email and password are required");
        }
        User user = userRepository.findByEmailIgnoreCase(req.email().trim())
                .orElseThrow(() -> CustomException.unauthorized("Invalid applicant email or password"));

        if (!matchesPassword(req.password(), user.getPassword())) {
            throw CustomException.unauthorized("Invalid applicant email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), "APPLICANT", user.getUserId(), "Applicant");
        return new AuthTokenResponse(token, "APPLICANT", user.getUserId(), user.getFullName(), user.getEmail(), "Applicant");
    }

    public AuthTokenResponse loginAdmin(LoginRequest req) {
        if (req.email() == null || req.password() == null) {
            throw CustomException.badRequest("Email and password are required");
        }
        Admin admin = adminRepository.findByEmailIgnoreCase(req.email().trim())
                .orElseThrow(() -> CustomException.unauthorized("Invalid admin email or password"));

        if (!matchesPassword(req.password(), admin.getPassword())) {
            throw CustomException.unauthorized("Invalid admin email or password");
        }

        String token = jwtService.generateToken(admin.getEmail(), "ADMIN", admin.getAdminId(), admin.getRole());
        return new AuthTokenResponse(token, "ADMIN", admin.getAdminId(), admin.getAdminName(), admin.getEmail(), admin.getRole());
    }

    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null) return false;
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }

    public AuthenticatedPrincipal getCurrentPrincipalOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedPrincipal principal) {
            return principal;
        }
        return null;
    }

    public User getCurrentApplicantOrDefault() {
        AuthenticatedPrincipal principal = getCurrentPrincipalOrNull();
        if (principal != null && "APPLICANT".equalsIgnoreCase(principal.userType())) {
            return userRepository.findById(principal.id())
                    .orElseThrow(() -> CustomException.notFound("Applicant not found"));
        }
        // Fallback to first seeded applicant (Rahul Sharma) if accessed directly without token
        return userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> CustomException.unauthorized("Please log in as an applicant"));
    }

    public Admin getCurrentAdminOrDefault() {
        AuthenticatedPrincipal principal = getCurrentPrincipalOrNull();
        if (principal != null && "ADMIN".equalsIgnoreCase(principal.userType())) {
            return adminRepository.findById(principal.id())
                    .orElseThrow(() -> CustomException.notFound("Admin not found"));
        }
        // Fallback to first seeded admin (RTO Admin) if accessed directly
        return adminRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> CustomException.unauthorized("Please log in as an admin"));
    }
}
