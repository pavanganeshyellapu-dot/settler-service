package com.settler.domain.auth.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CompleteProfileRequest {

    private String sessionId;

    // mandatory
    private String displayName;

    // optional – can be set or skipped
    private String gender;
    private LocalDate dateOfBirth;

    // optional – can be completed later in profile page
    private String email;
    private String phone;
}
