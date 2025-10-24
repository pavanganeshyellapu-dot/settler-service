package com.settler.domain.expenses.controller;

import com.settler.domain.expenses.dto.request.CreateExpenseRequest;
import com.settler.domain.expenses.dto.response.ExpenseResponse;
import com.settler.domain.expenses.dto.response.GroupExpenseSummaryResponse;
import com.settler.domain.expenses.service.IExpenseService;
import com.settler.dto.common.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
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
    public ResponseEntity<ApiResponse<Object>> createExpense(
            @RequestBody CreateExpenseRequest request,
            @RequestHeader(value = "Correlation-Id", required = false) String correlationId,
            @RequestHeader(value = "Session-Id", required = false) String sessionId) {

        if (correlationId == null) correlationId = UUID.randomUUID().toString();
        log.info("[{}] Received request to create expense for group {}", correlationId, request.getGroupId());

        ExpenseResponse created = expenseService.createExpense(request, correlationId, sessionId);

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
    public ResponseEntity<ApiResponse<Object>> getExpense(@PathVariable UUID id,
                                                          @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {

        log.info("[{}] Fetching expense with ID {}", correlationId, id);
        ExpenseResponse expense = expenseService.getExpenseById(id, correlationId);

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
     * ✅ Get all expenses + balances for a group
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<Object>> getExpensesByGroup(
            @PathVariable UUID groupId,
            @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {

        if (correlationId == null) correlationId = UUID.randomUUID().toString();
        log.info("[{}] Fetching all expenses and balances for group {}", correlationId, groupId);

        GroupExpenseSummaryResponse summary = expenseService.getGroupExpenseSummary(groupId, correlationId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched group expenses and balances")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(summary)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Export group transactions to CSV
     */
    @GetMapping("/group/{groupId}/export")
    public ResponseEntity<byte[]> exportGroupExpensesToCSV(@PathVariable UUID groupId,
                                                           @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {
        if (correlationId == null) correlationId = UUID.randomUUID().toString();
        log.info("[{}] Exporting group {} expenses to CSV", correlationId, groupId);

        byte[] csvData = expenseService.exportGroupExpensesToCSV(groupId, correlationId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=group-" + groupId + "-expenses.csv")
                .header("Content-Type", "text/csv")
                .body(csvData);
    }
}
