package com.settler.domain.expenses.controller;

import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.service.IExpenseService;
import com.settler.dto.common.ApiResponse;
import com.settler.dto.common.ResponseBodyWrapper;
import com.settler.dto.common.ResponseInfo;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expense APIs", description = "Handles expense creation, retrieval and deletion")
public class ExpenseController {

    private final IExpenseService expenseService;

    public ExpenseController(IExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // ✅ Create new expense
    @PostMapping
    @Operation(summary = "Create a new expense", description = "Adds an expense with given participants and split type")
    public ResponseEntity<ApiResponse<?>> createExpense(
            @RequestBody ExpenseCreateRequest request) {

        try {
            Expense expense = Expense.builder()
                    .groupId(request.getGroupId())
                    .payerId(request.getPayerId())
                    .description(request.getDescription())
                    .amount(request.getAmount())
                    .splitType(request.getSplitType() == null ? "EQUAL" : request.getSplitType())
                    .build();

            Expense saved = expenseService.addExpense(expense, request.getParticipantIds());

            ResponseInfo info = ResponseInfo.builder()
                    .timestamp(OffsetDateTime.now())
                    .code(ErrorCode.SUCCESS.getCode())
                    .message("Expense created successfully")
                    .build();

            ResponseBodyWrapper<Expense> body = ResponseBodyWrapper.<Expense>builder()
                    .statusCode(String.valueOf(HttpStatus.CREATED.value()))
                    .statusMessage(HttpStatus.CREATED.getReasonPhrase())
                    .data(saved)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<Expense>builder().responseInfo(info).body(body).build());

        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Failed to create expense: " + e.getMessage());
        }
    }

    // ✅ Get all expenses for a group
    @GetMapping("/group/{groupId}")
    @Operation(summary = "Get all expenses for a group")
    public ResponseEntity<ApiResponse<?>> getExpensesByGroup(@PathVariable UUID groupId) {
        List<Expense> expenses = expenseService.getExpensesByGroup(groupId);

        ResponseInfo info = ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .code(ErrorCode.SUCCESS.getCode())
                .message("Fetched successfully")
                .build();

        ResponseBodyWrapper<List<Expense>> body = ResponseBodyWrapper.<List<Expense>>builder()
                .statusCode(String.valueOf(HttpStatus.OK.value()))
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .data(expenses)
                .build();

        return ResponseEntity.ok(ApiResponse.<List<Expense>>builder().responseInfo(info).body(body).build());
    }

    // ✅ Delete an expense by ID
    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Delete an expense", description = "Removes the expense and its associated splits")
    public ResponseEntity<ApiResponse<?>> deleteExpense(@PathVariable UUID expenseId) {
        expenseService.deleteExpense(expenseId);

        ResponseInfo info = ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .code(ErrorCode.SUCCESS.getCode())
                .message("Deleted successfully")
                .build();

        ResponseBodyWrapper<String> body = ResponseBodyWrapper.<String>builder()
                .statusCode(String.valueOf(HttpStatus.OK.value()))
                .statusMessage("Expense deleted successfully")
                .data("Deleted")
                .build();

        return ResponseEntity.ok(ApiResponse.<String>builder().responseInfo(info).body(body).build());
    }
}
