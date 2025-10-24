package com.settler.domain.expenses.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBalanceResponse {
    private Map<UUID, BigDecimal> userBalances;
    private List<String> simplifiedSummary; // "User A owes User B ₹100"

    public static GroupBalanceResponse fromBalances(Map<UUID, BigDecimal> balances) {
        List<String> summary = new ArrayList<>();

        // Simplify into readable text (optional logic)
        for (Map.Entry<UUID, BigDecimal> entry : balances.entrySet()) {
            BigDecimal amount = entry.getValue();
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                summary.add("User " + entry.getKey() + " owes ₹" + amount.abs());
            } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
                summary.add("User " + entry.getKey() + " should receive ₹" + amount);
            }
        }

        return GroupBalanceResponse.builder()
                .userBalances(balances)
                .simplifiedSummary(summary)
                .build();
    }
}
