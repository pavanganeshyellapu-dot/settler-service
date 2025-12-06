package com.settler.domain.auth.service.impl;

import com.settler.domain.auth.service.IOtpNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"dev", "local"})
public class DevOtpNotificationService implements IOtpNotificationService {

    @Override
    public void sendOtp(String identifier, String channel, String otp) {
        log.info("📨 DEV OTP: [{}] via {} = {}", identifier, channel, otp);
    }
}
