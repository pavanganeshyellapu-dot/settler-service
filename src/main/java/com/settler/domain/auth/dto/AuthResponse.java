package com.settler.domain.auth.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private UUID userId;
    private String email;
    private String phone;
    private String displayName;
    private String role;

    private String accessToken;
    private String refreshToken;

    private boolean isNewUser; // true = profile just created
}
