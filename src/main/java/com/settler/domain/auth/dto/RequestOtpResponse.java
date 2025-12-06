package com.settler.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RequestOtpResponse {
    private UUID sessionId;
    private long expiresInSeconds;
    private String message;
}
