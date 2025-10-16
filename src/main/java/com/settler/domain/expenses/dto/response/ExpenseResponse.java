package com.settler.domain.expenses.dto.response;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Unified response object for expenses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

    private UUID id;
    private UUID groupId;
    private String description;
    private Double amount;
    private UUID paidBy;
    private String splitType;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private List<SplitDetailResponse> participants; // Nested splits
}
