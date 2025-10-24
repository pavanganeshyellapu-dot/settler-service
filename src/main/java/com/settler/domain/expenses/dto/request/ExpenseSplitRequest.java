package com.settler.domain.expenses.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplitRequest {
    private UUID userId;
    private BigDecimal amount;
    private BigDecimal percentage;
}
