package com.settler.domain.auth.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String displayName;
    private String role;
    private String user;
    private String id;


}
