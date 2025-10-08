package com.settler.domain.auth.service;

import com.settler.domain.auth.dto.AuthResponse;
import com.settler.domain.auth.dto.LoginRequest;
import com.settler.domain.auth.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
