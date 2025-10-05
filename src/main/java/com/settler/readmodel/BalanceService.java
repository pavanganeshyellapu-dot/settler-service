package com.settler.readmodel;

import com.settler.domain.expenses.entity.Expense;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculates and simplifies balances between users inside a group.
 */
@Service
public class BalanceService {

    /**
     * Simplifies who owes whom based on all expenses in a group.
     * @param expenses List of expenses in the group.
     * @return A simplified list of transactions: fromUser → toUser → amount.
     */
    public List<Map<String, Object>> simplifyBalances(List<Expense> expenses) {
        Map<UUID, Double> balanceMap = new HashMap<>();

        // Aggregate balances
        for (Expense e : expenses) {
            double share = e.getAmount() / 2; // simplified logic for now
            balanceMap.put(e.getPayerId(), balanceMap.getOrDefault(e.getPayerId(), 0.0) + share);
            balanceMap.put(e.getGroupId(), balanceMap.getOrDefault(e.getGroupId(), 0.0) - share);
        }

        // Convert to list of simplified transactions
        return balanceMap.entrySet().stream()
                .map(entry -> Map.of("userId", entry.getKey(), "balance", entry.getValue()))
                .collect(Collectors.toList());
    }
}
