package com.settler.domain.expenses.service;

import com.settler.domain.expenses.dto.request.CreateExpenseRequest;
import com.settler.domain.expenses.dto.response.ExpenseResponse;
import com.settler.domain.expenses.dto.response.GroupExpenseSummaryResponse;

import java.util.UUID;

public interface IExpenseService {

    /**
     * Create a new expense
     */
    ExpenseResponse createExpense(CreateExpenseRequest request, String correlationId, String sessionId);

    /**
     * Get expense by ID
     */
    ExpenseResponse getExpenseById(UUID id, String correlationId);

    /**
     * Get all expenses and balances for a group
     */
    GroupExpenseSummaryResponse getGroupExpenseSummary(UUID groupId, String correlationId);

    /**
     * Export group expenses to CSV
     */
    byte[] exportGroupExpensesToCSV(UUID groupId, String correlationId);
}
