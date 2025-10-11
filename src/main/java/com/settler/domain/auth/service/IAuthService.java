package com.settler.domain.auth.service;

import com.settler.domain.auth.dto.AuthRequest;
import com.settler.domain.auth.dto.RegisterRequest;
import com.settler.domain.auth.dto.AuthResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
}
