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
            double share = e.getAmount().doubleValue() / 2; // simplified logic for now
            balanceMap.put(e.getPaidBy(), balanceMap.getOrDefault(e.getPaidBy(), 0.0) + share);
            balanceMap.put(e.getGroupId(), balanceMap.getOrDefault(e.getGroupId(), 0.0) - share);
        }

        // Convert to list of simplified transactions
        return balanceMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("userId", entry.getKey());
                    m.put("balance", entry.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }
}
