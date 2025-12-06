package com.settler.domain.auth.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class VerifyOtpRequest {
    private UUID sessionId;
    private String otp;
}
