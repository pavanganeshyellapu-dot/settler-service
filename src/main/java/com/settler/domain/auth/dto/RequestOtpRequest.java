package com.settler.domain.auth.dto;

import lombok.Data;

@Data
public class RequestOtpRequest {
    // phone or email
    private String identifier;

    // "PHONE" or "EMAIL"
    private String channel;
}
