package com.settler.readmodel;

import com.settler.domain.expenses.entity.Expense;
import java.math.BigDecimal;
import java.util.*;

/**
 * Aggregates all expenses in a group to calculate per-user net balances.
 * Positive balance → user is owed money.
 * Negative balance → user owes money.
 */
public class BalanceCalculator {

    /**
     * @param expenses List of all expenses in a group
     * @return Map<userId, netBalance>
     */
    public static Map<String, BigDecimal> calculateNetBalances(List<Expense> expenses) {
        Map<String, BigDecimal> balances = new HashMap<>();

        for (Expense expense : expenses) {
            String payerId = expense.getPaidBy();
            BigDecimal total = expense.getAmount();
            List<String> participants = expense.getParticipants();

            if (participants == null || participants.isEmpty()) continue;

            BigDecimal share = total.divide(BigDecimal.valueOf(participants.size()), 2, BigDecimal.ROUND_HALF_UP);

            // Add credit to payer
            balances.merge(payerId, total.subtract(share), BigDecimal::add);

            // Deduct share for each participant (including payer)
            for (String userId : participants) {
                balances.merge(userId, share.negate(), BigDecimal::add);
            }
        }

        return balances;
    }
}
