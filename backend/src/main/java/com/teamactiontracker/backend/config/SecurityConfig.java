
package com.teamactiontracker.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource)
            throws Exception {

        http
            .cors(cors ->
                cors.configurationSource(corsConfigurationSource)
            )
            .csrf(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.IF_REQUIRED
                )
            )
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .authorizeHttpRequests(auth -> auth

                // Public endpoints: login and CSRF token
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/csrf"
                ).permitAll()

                // Public health check
                .requestMatchers(
                   HttpMethod.GET, "/api/health",
                   "/actuator/health"
                ).permitAll()
                
                // Swagger UI and OpenAPI documentation
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**"
                ).permitAll()

                // Allow browser preflight requests
                .requestMatchers(
                    HttpMethod.OPTIONS, "/**"
                ).permitAll()

                // Only Managers can view users
                .requestMatchers(
                    HttpMethod.GET, "/api/users"
                ).hasRole("MANAGER")

                // Only Managers can create user accounts
                .requestMatchers(
                    HttpMethod.POST, "/api/users"
                ).hasRole("MANAGER")

                // Managers and Team Members can view action items
                .requestMatchers(
                    HttpMethod.GET, "/api/action-items/**"
                ).hasAnyRole("MANAGER", "TEAM_MEMBER")

                // Only Managers can create action items
                .requestMatchers(
                    HttpMethod.POST, "/api/action-items/**"
                ).hasRole("MANAGER")

                // Managers and Team Members can update status
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/action-items/*/status"
                ).hasAnyRole("MANAGER", "TEAM_MEMBER")

                // Managers and Team Members can view action history
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/action-items/*/history"
                ).hasAnyRole("MANAGER", "TEAM_MEMBER")

                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
