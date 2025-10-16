package com.settler.domain.users.dto;

import com.settler.domain.users.enums.UserRole;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String displayName;
    private UserRole role;
    private boolean active;
}
