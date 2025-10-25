package com.settler.domain.groups.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {
    @NotBlank(message = "name is required")
    private String name;


    @NotBlank(message = "currencyCode is required")
    // optional: validate ISO currency with regex or enum
    private String currencyCode;
}
