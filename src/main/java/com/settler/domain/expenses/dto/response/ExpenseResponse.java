package com.settler.domain.expenses.dto.response;

import com.settler.domain.expenses.entity.Expense;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {
    private UUID id;
    private UUID groupId;
    private UUID paidBy;
    private BigDecimal amount;
    private String description;
    private String category;
    private String splitType;
    private OffsetDateTime createdAt;

    public static ExpenseResponse fromEntity(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .groupId(expense.getGroupId())
                .paidBy(expense.getPaidBy())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .category(expense.getCategory())
                .splitType(expense.getSplitType())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
