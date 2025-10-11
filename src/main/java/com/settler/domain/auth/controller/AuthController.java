package com.settler.domain.auth.controller;

import com.settler.domain.auth.dto.*;
import com.settler.domain.auth.service.IAuthService;
import com.settler.domain.auth.service.IGoogleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;
    private final IGoogleAuthService googleAuthService;

    public AuthController(IAuthService authService, IGoogleAuthService googleAuthService) {
        this.authService = authService;
        this.googleAuthService = googleAuthService;
    }

    /** 🧾 User Registration (email + password) **/
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /** 🔐 User Login (email + password) **/
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /** 🌐 Google Sign-In **/
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok(googleAuthService.loginWithGoogle(request));
    }

    /** 🧠 Optional health/test endpoint **/
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("✅ AuthController is up and running");
    }
}
