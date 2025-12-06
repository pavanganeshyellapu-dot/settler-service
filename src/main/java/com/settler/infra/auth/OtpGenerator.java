package com.settler.infra.auth;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private final SecureRandom random = new SecureRandom();

    /**
     * 6-digit numeric OTP as string (100000 - 999999)
     */
    public String generateOtp() {
        int v = 100000 + random.nextInt(900000);
        return Integer.toString(v);
    }
}
