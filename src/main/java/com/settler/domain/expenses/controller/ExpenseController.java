package com.settler.domain.expenses.controller;

import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.service.IExpenseService;
import com.settler.dto.common.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final IExpenseService expenseService;

    public ExpenseController(IExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> addExpense(@RequestBody Expense expense) {
        Expense saved = expenseService.addExpense(expense);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Expense added successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(saved)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getExpense(@PathVariable UUID id) {
        Expense exp = expenseService.getExpenseById(id);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Success")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Fetched expense successfully")
                        .data(exp)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<Object>> getExpensesByGroup(@PathVariable UUID groupId) {
        List<Expense> list = expenseService.getExpensesByGroup(groupId);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Success")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Fetched all group expenses")
                        .data(list)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
