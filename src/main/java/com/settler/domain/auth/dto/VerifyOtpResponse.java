package com.settler.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VerifyOtpResponse {
    private UUID sessionId;
    private boolean verified;
    private String message;
}
