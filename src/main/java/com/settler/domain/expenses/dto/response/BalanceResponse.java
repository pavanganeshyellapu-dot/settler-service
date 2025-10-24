package com.settler.domain.expenses.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    private String userName;
    private String email;
    private Double balance;

    // ✅ New backend-friendly constructor for BalanceCalculator
    public BalanceResponse(UUID userId, BigDecimal balance) {
        this.userName = userId != null ? userId.toString() : null;
        this.email = null;
        this.balance = balance != null ? balance.doubleValue() : 0.0;
    }
}
