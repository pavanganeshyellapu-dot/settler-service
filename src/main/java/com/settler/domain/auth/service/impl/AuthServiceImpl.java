package com.settler.domain.auth.service.impl;

import com.settler.config.security.JwtTokenProvider;
import com.settler.domain.auth.dto.AuthRequest;
import com.settler.domain.auth.dto.AuthResponse;
import com.settler.domain.auth.dto.RegisterRequest;
import com.settler.domain.auth.service.IAuthService;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.repo.UserRepository;
import com.settler.domain.users.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 🧾 Register a new user with email + password.
     * If user already exists, throw exception.
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }

        // Build user entity
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName() != null
                        ? request.getDisplayName()
                        : request.getEmail().split("@")[0]) // default name
                .role(UserRole.USER)
                .createdAt(OffsetDateTime.now())
                .build();

        userRepository.save(user);

        // Generate JWT for immediate login
        String token = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole().name())
                .build();
    }

    /**
     * 🔐 Login existing user and generate JWT.
     */
    @Override
    public AuthResponse login(AuthRequest request) {
        // Authenticate using AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Fetch user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT
        String token = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole().name())
                .build();
    }
}
