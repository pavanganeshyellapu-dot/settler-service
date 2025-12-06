package com.settler.domain.auth.service;

public interface IOtpNotificationService {
    void sendOtp(String identifier, String channel, String otp);
}
