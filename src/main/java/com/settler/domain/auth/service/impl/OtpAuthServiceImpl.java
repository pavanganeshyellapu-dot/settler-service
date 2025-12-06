package com.settler.domain.auth.service.impl;

import com.settler.config.security.JwtTokenProvider;
import com.settler.domain.auth.dto.*;
import com.settler.domain.auth.entity.OtpSession;
import com.settler.domain.auth.repo.OtpSessionRepository;
import com.settler.domain.auth.service.IOtpAuthService;
import com.settler.domain.auth.service.IOtpNotificationService;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.enums.UserRole;
import com.settler.domain.users.repo.UserRepository;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import com.settler.domain.auth.service.RefreshTokenService;
import com.settler.infra.auth.OtpGenerator;
import com.settler.infra.ratelimit.RedisRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpAuthServiceImpl implements IOtpAuthService {

    private final OtpSessionRepository otpSessionRepository;
    private final UserRepository userRepository;
    private final IOtpNotificationService otpNotificationService;
    private final PasswordEncoder passwordEncoder;
    private final OtpGenerator otpGenerator;
    private final RedisRateLimiter rateLimiter;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${otp.expiry-minutes:5}")
    private long otpExpiryMinutes;

    private static final long OTP_REQUEST_LIMIT = 3;
    private static final long OTP_REQUEST_WINDOW_SECONDS = 300;
    private static final long OTP_VERIFY_LIMIT = 5;

    @Override
    public RequestOtpResponse requestOtp(RequestOtpRequest request) {
        String identifier = request.getIdentifier();

        String rateKey = "otp:requests:" + identifier;
        long count = rateLimiter.incrementAndGet(rateKey, OTP_REQUEST_WINDOW_SECONDS);
        if (count > OTP_REQUEST_LIMIT) {
            throw new BusinessException(ErrorCode.CONFLICT);
        }

        String otp = otpGenerator.generateOtp();
        String hashedOtp = passwordEncoder.encode(otp);
        log.info("DEBUG OTP for {} is: {}", identifier, otp);

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiry = now.plusMinutes(otpExpiryMinutes);

        OtpSession session = OtpSession.builder()
                .identifier(identifier)
                .channel(request.getChannel())
                .otpHash(hashedOtp)
                .expiresAt(expiry)
                .verified(false)
                .attempts(0)
                .purpose("LOGIN")
                .createdAt(now)
                .build();

        otpSessionRepository.save(session);

        long expiresInSeconds = Duration.between(OffsetDateTime.now(), session.getExpiresAt()).getSeconds();
        if (expiresInSeconds < 0) expiresInSeconds = 0;

        otpNotificationService.sendOtp(identifier, request.getChannel(), otp);

        return RequestOtpResponse.builder()
                .sessionId(session.getId())
                .expiresInSeconds(expiresInSeconds)
                .message("OTP sent successfully")
                .build();
    }

    @Override
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        OtpSession session = otpSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (session.getVerified()) {
            throw new BusinessException(ErrorCode.CONFLICT);
        }

        if (session.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String attemptKey = "otp:attempts:" + session.getId();
        long attempts = rateLimiter.incrementAndGet(attemptKey, otpExpiryMinutes * 60);
        if (attempts > OTP_VERIFY_LIMIT) {
            throw new BusinessException(ErrorCode.CONFLICT);
        }

        if (!passwordEncoder.matches(request.getOtp(), session.getOtpHash())) {
            session.setAttempts(session.getAttempts() + 1);
            otpSessionRepository.save(session);
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        session.setVerified(true);
        otpSessionRepository.save(session);
        rateLimiter.reset(attemptKey);

        return VerifyOtpResponse.builder()
                .sessionId(session.getId())
                .verified(true)
                .message("OTP verified successfully")
                .build();
    }

    @Override
    public AuthResponse completeProfile(CompleteProfileRequest request) {

        OtpSession session = otpSessionRepository.findById(UUID.fromString(request.getSessionId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!session.getVerified()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        if (request.getDisplayName() == null || request.getDisplayName().isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED);
        }

        String identifier = session.getIdentifier(); // phone or email used to login

        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElse(null);

        boolean isNew = false;

        if (user == null) {
            isNew = true;
            user = User.builder()
                    .displayName(request.getDisplayName())
                    .gender(request.getGender())
                    .dateOfBirth(request.getDateOfBirth())
                    .role(UserRole.USER)
                    .status("ACTIVE")
                    .createdAt(OffsetDateTime.now())
                    .build();

            if (identifier.contains("@")) {
                user.setEmail(identifier);
            } else {
                user.setPhone(identifier);
            }

            userRepository.save(user);
        } else {
            user.setDisplayName(request.getDisplayName());

            if (request.getGender() != null) {
                user.setGender(request.getGender());
            }
            if (request.getDateOfBirth() != null) {
                user.setDateOfBirth(request.getDateOfBirth());
            }

            userRepository.save(user);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .displayName(user.getDisplayName())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNew)
                .build();
    }

    @Override
    public AuthResponse refresh(String rawRefreshToken) {
        if (!refreshTokenService.validateRefreshToken(rawRefreshToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UUID userId = refreshTokenService.findUserIdByRefreshToken(rawRefreshToken);
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .displayName(user.getDisplayName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .accessToken(newAccessToken)
                .refreshToken(rawRefreshToken)
                .isNewUser(false)
                .build();
    }

    @Override
    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }
}
