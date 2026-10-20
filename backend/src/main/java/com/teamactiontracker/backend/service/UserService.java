
package com.teamactiontracker.backend.service;

import com.teamactiontracker.backend.dto.CreateUserRequest;
import com.teamactiontracker.backend.dto.LoginResponse;
import com.teamactiontracker.backend.dto.UserSummaryResponse;
import com.teamactiontracker.backend.model.User;
import com.teamactiontracker.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class UserService {

    private static final Set<String> ALLOWED_ROLES =
            Set.of("MANAGER", "TEAM_MEMBER");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse createUser(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String name = request.getName().trim();
        String role = request.getRole().trim().toUpperCase(Locale.ROOT);

        if (name.isEmpty() || email.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Name and email are required");
        }

        if (!ALLOWED_ROLES.contains(role)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role must be MANAGER or TEAM_MEMBER");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));
        user.setActiveStatus(true);

        User savedUser = userRepository.save(user);

        return new LoginResponse(
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "User created successfully");
    }

    public List<UserSummaryResponse> getActiveUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> Boolean.TRUE.equals(user.getActiveStatus()))
                .map(user -> new UserSummaryResponse(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()))
                .toList();
    }
}
