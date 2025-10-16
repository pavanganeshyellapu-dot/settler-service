package com.settler.domain.auth.service;

import com.settler.domain.auth.dto.AuthResponse;
import com.settler.domain.auth.dto.GoogleAuthRequest;

public interface IGoogleAuthService {
    AuthResponse loginWithGoogle(GoogleAuthRequest request);
}
