package com.settler.domain.expenses.service;

import com.settler.domain.expenses.dto.request.CreateExpenseRequest;
import com.settler.domain.expenses.dto.response.ExpenseResponse;
import java.util.List;
import java.util.UUID;

public interface IExpenseService {

    ExpenseResponse createExpense(CreateExpenseRequest request);

    ExpenseResponse getExpenseById(UUID id);

    List<ExpenseResponse> getExpensesByGroup(UUID groupId);

    List<ExpenseResponse> getExpensesByUser(UUID userId);
}
