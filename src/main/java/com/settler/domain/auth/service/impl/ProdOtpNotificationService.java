package com.settler.domain.auth.service.impl;

import com.settler.domain.auth.service.IOtpNotificationService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class ProdOtpNotificationService implements IOtpNotificationService {

    @Override
    public void sendOtp(String identifier, String channel, String otp) {
        // TODO integrate SMS/Email provider like Twilio, Amazon SES, SendGrid etc
        System.out.println("PROD OTP SENT");
    }
}

