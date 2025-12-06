package com.settler.infra.auth;

import com.settler.domain.auth.entity.RefreshToken;
import com.settler.domain.auth.repo.RefreshTokenRepository;
import com.settler.domain.auth.service.RefreshTokenService;
import com.settler.domain.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiry-days:30}")
    private long refreshExpiryDays;

    @Override
    public String generateRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString() + UUID.randomUUID();
        String hashed = passwordEncoder.encode(rawToken);

        RefreshToken entity = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashed)
                .createdAt(OffsetDateTime.now())
                .expiresAt(OffsetDateTime.now().plusDays(refreshExpiryDays))
                .revoked(false)
                .build();

        repo.save(entity);
        return rawToken;
    }

    @Override
    public boolean validateRefreshToken(String rawToken) {
        OffsetDateTime now = OffsetDateTime.now();

        return repo.findAll().stream()
                .filter(rt -> !rt.isRevoked() && rt.getExpiresAt().isAfter(now))
                .anyMatch(rt -> passwordEncoder.matches(rawToken, rt.getTokenHash()));
    }

    @Override
    public void revoke(String rawToken) {
        repo.findAll().forEach(rt -> {
            if (passwordEncoder.matches(rawToken, rt.getTokenHash())) {
                rt.setRevoked(true);
                repo.save(rt);
            }
        });
    }

    @Override
    public UUID findUserIdByRefreshToken(String rawToken) {
        OffsetDateTime now = OffsetDateTime.now();

        return repo.findAll().stream()
                .filter(rt -> !rt.isRevoked() && rt.getExpiresAt().isAfter(now))
                .filter(rt -> passwordEncoder.matches(rawToken, rt.getTokenHash()))
                .map(RefreshToken::getUserId)
                .findFirst()
                .orElse(null);
    }
}
