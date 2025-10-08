package com.settler.readmodel;

import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.entity.ExpenseSplit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Aggregates all expenses in a group to calculate per-user net balances.
 * Positive balance → user is owed money.
 * Negative balance → user owes money.
 */
public class BalanceCalculator {

    /**
     * @param expenses List of all expenses in a group
     * @return Map<UUID, BigDecimal>  // userId → net balance
     */
    public static Map<UUID, BigDecimal> calculateNetBalances(List<Expense> expenses) {
        Map<UUID, BigDecimal> balances = new HashMap<>();

        for (Expense expense : expenses) {
            UUID payerId = expense.getPaidBy();
            BigDecimal total = expense.getAmount();
            List<ExpenseSplit> participants = expense.getParticipants();

            if (participants == null || participants.isEmpty()) continue;

            // Distribute equally if not specified
            BigDecimal share = total
                    .divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.HALF_UP);

            // ✅ 1. Add credit to payer (payer paid total, but owes only their share)
            balances.merge(payerId, total.subtract(share), BigDecimal::add);

            // ✅ 2. Deduct share for each participant (including payer)
            for (ExpenseSplit split : participants) {
                UUID userId = split.getUserId();
                BigDecimal individualShare = split.getAmount() != null
                        ? split.getAmount()
                        : share; // fallback to equal share if not specified

                balances.merge(userId, individualShare.negate(), BigDecimal::add);
            }
        }

        return balances;
    }
}
