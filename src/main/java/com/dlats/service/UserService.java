package com.dlats.service;

import com.dlats.dto.AuthDtos.UserProfileDto;
import com.dlats.model.User;
import com.dlats.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public UserProfileDto getProfile() {
        User user = authService.getCurrentApplicantOrDefault();
        return toProfileDto(user);
    }

    @Transactional
    public UserProfileDto updateProfile(UserProfileDto req) {
        User user = authService.getCurrentApplicantOrDefault();
        if (req.fullName() != null && !req.fullName().isBlank()) user.setFullName(req.fullName().trim());
        if (req.phone() != null) user.setPhone(req.phone().trim());
        if (req.dateOfBirth() != null) user.setDateOfBirth(req.dateOfBirth());
        if (req.gender() != null) user.setGender(req.gender().trim());
        if (req.address() != null) user.setAddress(req.address().trim());
        if (req.city() != null) user.setCity(req.city().trim());
        if (req.state() != null) user.setState(req.state().trim());
        if (req.pincode() != null) user.setPincode(req.pincode().trim());

        user = userRepository.save(user);
        return toProfileDto(user);
    }

    public static UserProfileDto toProfileDto(User u) {
        return new UserProfileDto(
                u.getUserId(),
                u.getFullName(),
                u.getEmail(),
                u.getPhone(),
                u.getDateOfBirth(),
                u.getGender(),
                u.getAddress(),
                u.getCity(),
                u.getState(),
                u.getPincode()
        );
    }
}
