package com.settler.domain.expenses.controller;

import com.settler.domain.expenses.dto.request.CreateExpenseRequest;
import com.settler.domain.expenses.dto.response.ExpenseResponse;
import com.settler.domain.expenses.service.IExpenseService;
import com.settler.dto.common.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@Slf4j
public class ExpenseController {

    private final IExpenseService expenseService;

    public ExpenseController(IExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * ✅ Create new expense
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createExpense(@RequestBody CreateExpenseRequest request) {
        log.info("Received request to create expense for group {}", request.getGroupId());

        ExpenseResponse created = expenseService.createExpense(request);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Expense created successfully")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(created)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Get expense by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getExpense(@PathVariable UUID id) {
        log.info("Fetching expense with ID {}", id);
        ExpenseResponse expense = expenseService.getExpenseById(id);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Success")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("Expense details fetched")
                        .data(expense)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Get all expenses for a group
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<Object>> getExpensesByGroup(@PathVariable UUID groupId) {
        log.info("Fetching all expenses for group {}", groupId);
        List<ExpenseResponse> expenses = expenseService.getExpensesByGroup(groupId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched all group expenses")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(expenses)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Get all expenses paid by a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getExpensesByUser(@PathVariable UUID userId) {
        log.info("Fetching expenses paid by user {}", userId);
        List<ExpenseResponse> expenses = expenseService.getExpensesByUser(userId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched user expenses")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(expenses)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
