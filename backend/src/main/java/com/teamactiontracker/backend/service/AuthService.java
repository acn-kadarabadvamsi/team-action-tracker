
package com.teamactiontracker.backend.service;

import com.teamactiontracker.backend.dto.LoginRequest;
import com.teamactiontracker.backend.dto.LoginResponse;
import com.teamactiontracker.backend.model.User;
import com.teamactiontracker.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AuthService {

    private static final Set<String> ALLOWED_ROLES =
            Set.of("MANAGER", "TEAM_MEMBER");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSessionSecurityContextRepository contextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(
            LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        String email = request.getEmail().trim();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password"));

        if (!Boolean.TRUE.equals(user.getActiveStatus())
                || user.getPasswordHash() == null
                || !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String role = user.getRole() == null
                ? ""
                : user.getRole().trim().toUpperCase(Locale.ROOT);

        if (!ALLOWED_ROLES.contains(role)) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        user.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Rotate the session ID to reduce session-fixation risk.
        httpRequest.getSession(true);
        httpRequest.changeSessionId();

        // Explicitly save the security context for subsequent requests.
        contextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                role,
                "Login successful"
        );
    }
}
