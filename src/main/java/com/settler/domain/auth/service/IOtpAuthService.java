package com.settler.domain.auth.service;

import com.settler.domain.auth.dto.*;

public interface IOtpAuthService {

    RequestOtpResponse requestOtp(RequestOtpRequest request);

    VerifyOtpResponse verifyOtp(VerifyOtpRequest request);

    AuthResponse completeProfile(CompleteProfileRequest request);

    AuthResponse refresh(String refreshToken);

    void logout(String refreshToken);
}
