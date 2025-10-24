package com.settler.domain.expenses.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExpenseRequest {
    private UUID groupId;
    private UUID paidBy;
    private BigDecimal amount;
    private String description;
    private String category; // optional
    private String splitType; // equal, percentage, custom
    private List<ExpenseSplitRequest> splits;
}
