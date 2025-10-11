package com.settler.domain.auth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.settler.config.security.JwtTokenProvider;
import com.settler.domain.auth.dto.AuthResponse;
import com.settler.domain.auth.dto.GoogleAuthRequest;
import com.settler.domain.auth.service.IGoogleAuthService;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.enums.UserRole;
import com.settler.domain.users.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements IGoogleAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 👇 Replace this with your Google Client ID (from Google Cloud Console)
    private static final String GOOGLE_CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com";

    @Override
    public AuthResponse loginWithGoogle(GoogleAuthRequest request) {
        try {
            log.info("🔐 Verifying Google ID token...");

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());
            if (idToken == null) {
                log.error("❌ Invalid Google token");
                throw new RuntimeException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
           // String picture = (String) payload.get("picture");

            log.info("✅ Google token verified for {}", email);

            // 🔍 Check if user already exists
            Optional<User> existingUserOpt = userRepository.findByEmail(email);
            User user;

            if (existingUserOpt.isPresent()) {
                user = existingUserOpt.get();
                log.info("🧩 Existing Google user found: {}", user.getEmail());
            } else {
                // 🆕 Register new Google user
                user = User.builder()
                        .email(email)
                        .displayName(name != null ? name : email.split("@")[0])
                        .password(passwordEncoder.encode(UUID.randomUUID().toString())) // random dummy password
                        .role(UserRole.valueOf("USER"))
                        .build();
                userRepository.save(user);
                log.info("🎉 New Google user created: {}", user.getEmail());
            }

            // 🎟️ Generate Settler JWT
            String jwt = jwtTokenProvider.generateToken(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            return AuthResponse.builder()
                    .token(jwt)
                    .email(user.getEmail())
                    .displayName(user.getDisplayName())
                    .role(user.getRole().name())
                    .build();


        } catch (Exception ex) {
            log.error("⚠️ Google login failed: {}", ex.getMessage(), ex);
            throw new RuntimeException("Google login failed: " + ex.getMessage());
        }
    }
}
