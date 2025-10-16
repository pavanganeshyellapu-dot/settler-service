package com.settler.domain.expenses.dto.request;

import lombok.*;
import java.util.UUID;

/**
 * Participant-level data in a split.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitDetailRequest {

    private UUID userId;      // User’s ID
    private Double amount;    // How much they owe
    private Double percentage; // Used when splitType = PERCENTAGE
}
