package com.settler.domain.expenses.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Each participant’s contribution in the expense.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitDetailResponse {

    private UUID userId;
    private BigDecimal amount;
    private Double percentage;
}
