package com.settler.domain.expenses.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupExpenseSummaryResponse {
    private List<ExpenseResponse> expenses;
    private List<BalanceResponse> balances;
    private Double totalSpent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ New 4-arg constructor used by ExpenseServiceImpl
    public GroupExpenseSummaryResponse(
            List<ExpenseResponse> expenses,
            List<BalanceResponse> balances,
            Double totalSpent,
            LocalDateTime createdAt
    ) {
        this.expenses = expenses;
        this.balances = balances;
        this.totalSpent = totalSpent;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
}
