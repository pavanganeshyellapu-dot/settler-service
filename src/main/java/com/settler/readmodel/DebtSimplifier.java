package com.settler.readmodel;

import java.math.BigDecimal;
import java.util.*;

/**
 * Simplifies debts so fewer transactions are needed.
 * Example:
 *   A owes B ₹100, B owes C ₹100 → becomes A owes C ₹100.
 */
public class DebtSimplifier {

    public static List<Debt> simplify(Map<String, BigDecimal> balances) {
        List<Debt> simplified = new ArrayList<>();

        // Separate positive (creditors) and negative (debtors)
        List<UserBalance> creditors = new ArrayList<>();
        List<UserBalance> debtors = new ArrayList<>();

        balances.forEach((userId, amount) -> {
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new UserBalance(userId, amount));
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new UserBalance(userId, amount.abs())); // store as positive
            }
        });

        // Greedy simplification
        int i = 0, j = 0;
        while (i < debtors.size() && j < creditors.size()) {
            UserBalance debtor = debtors.get(i);
            UserBalance creditor = creditors.get(j);

            BigDecimal min = debtor.amount.min(creditor.amount);
            simplified.add(new Debt(debtor.userId, creditor.userId, min));

            debtor.amount = debtor.amount.subtract(min);
            creditor.amount = creditor.amount.subtract(min);

            if (debtor.amount.compareTo(BigDecimal.ZERO) == 0) i++;
            if (creditor.amount.compareTo(BigDecimal.ZERO) == 0) j++;
        }

        return simplified;
    }

    // Inner helper classes
    public static class UserBalance {
        public String userId;
        public BigDecimal amount;

        public UserBalance(String userId, BigDecimal amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }

    public static class Debt {
        public String from;
        public String to;
        public BigDecimal amount;

        public Debt(String from, String to, BigDecimal amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
    }
}
