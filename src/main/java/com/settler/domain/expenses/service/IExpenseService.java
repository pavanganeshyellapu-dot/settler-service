package com.settler.domain.expenses.service;

import com.settler.domain.expenses.entity.Expense;
import java.util.List;
import java.util.UUID;

public interface IExpenseService {
    Expense addExpense(Expense expense);
    Expense getExpenseById(UUID id);
    List<Expense> getExpensesByGroup(UUID groupId);
    List<Expense> getExpensesByPayer(UUID payerId);
    List<Expense> getAllExpenses();
}
