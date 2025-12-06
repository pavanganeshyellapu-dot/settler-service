package com.settler.domain.auth.controller;

import com.settler.domain.auth.dto.*;
import com.settler.domain.auth.service.IOtpAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IOtpAuthService otpAuthService;

    @PostMapping("/request-otp")
    public ResponseEntity<RequestOtpResponse> requestOtp(@RequestBody RequestOtpRequest request) {
        return ResponseEntity.ok(otpAuthService.requestOtp(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(otpAuthService.verifyOtp(request));
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<AuthResponse> completeProfile(@RequestBody CompleteProfileRequest request) {
        return ResponseEntity.ok(otpAuthService.completeProfile(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest req) {
        return ResponseEntity.ok(otpAuthService.refresh(req.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest req) {
        otpAuthService.logout(req.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth stack is up");
    }
}
