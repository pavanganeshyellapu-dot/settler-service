package com.settler.domain.expenses.service;

import com.settler.domain.expenses.entity.Expense;

import java.util.List;
import java.util.UUID;

public interface IExpenseService {

    Expense addExpense(Expense expense, List<UUID> participantIds);

    List<Expense> getExpensesByGroup(UUID groupId);

    void deleteExpense(UUID expenseId);
}
