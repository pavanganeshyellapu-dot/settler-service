package com.settler.domain.auth.dto;

import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String token; // Google ID token from frontend
}
