package com.settler.readmodel;

import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.entity.ExpenseSplit;

import java.math.BigDecimal;
import java.util.*;

public class BalanceCalculator {

    /**
     * Calculates how much each user owes or is owed in a group.
     * Positive = user is owed money
     * Negative = user owes money
     */
    public static Map<UUID, BigDecimal> calculateNetBalances(List<Expense> expenses) {
        Map<UUID, BigDecimal> balances = new HashMap<>();

        for (Expense expense : expenses) {
            UUID payer = expense.getPaidBy();
            BigDecimal totalAmount = expense.getAmount();

            // Credit the payer
            balances.put(payer, balances.getOrDefault(payer, BigDecimal.ZERO).add(totalAmount));

            // Debit each participant
            if (expense.getParticipants() != null) {
                for (ExpenseSplit split : expense.getParticipants()) {
                    balances.put(split.getUserId(),
                            balances.getOrDefault(split.getUserId(), BigDecimal.ZERO)
                                    .subtract(split.getAmount()));
                }
            }
        }

        return balances;
    }
}
