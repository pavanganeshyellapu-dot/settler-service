package com.settler.domain.expenses.service.impl;

import com.settler.domain.expenses.dto.request.CreateExpenseRequest;
import com.settler.domain.expenses.dto.request.SplitDetailRequest;
import com.settler.domain.expenses.dto.response.ExpenseResponse;
import com.settler.domain.expenses.dto.response.SplitDetailResponse;
import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.entity.ExpenseSplit;
import com.settler.domain.expenses.repo.ExpenseRepository;
import com.settler.domain.expenses.repo.ExpenseSplitRepository;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ExpenseServiceImpl implements com.settler.domain.expenses.service.IExpenseService {

    private final ExpenseRepository expenseRepo;
    private final ExpenseSplitRepository splitRepo;

    public ExpenseServiceImpl(ExpenseRepository expenseRepo, ExpenseSplitRepository splitRepo) {
        this.expenseRepo = expenseRepo;
        this.splitRepo = splitRepo;
    }

    @Override
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        log.info("Creating expense for group {}", request.getGroupId());

        if (request.getAmount() == null || request.getAmount() <= 0)
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Invalid expense amount");

        // Step 1: Create Expense entity
        Expense expense = Expense.builder()
                .groupId(request.getGroupId())
                .description(request.getDescription())
                .amount(BigDecimal.valueOf(request.getAmount()))
                .paidBy(request.getPaidBy())
                .splitType(request.getSplitType())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        expense = expenseRepo.save(expense);
        log.debug("Expense created with ID {}", expense.getId());

        // Step 2: Create splits
        List<ExpenseSplit> splits = buildSplits(expense, request);
        splitRepo.saveAll(splits);

        // Step 3: Return response
        return mapToResponse(expense, splits);
    }

    @Override
    public ExpenseResponse getExpenseById(UUID id) {
        Expense expense = expenseRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Expense not found"));

        List<ExpenseSplit> splits = splitRepo.findByExpenseId(id);
        return mapToResponse(expense, splits);
    }

    @Override
    public List<ExpenseResponse> getExpensesByGroup(UUID groupId) {
        return expenseRepo.findByGroupId(groupId).stream()
                .map(exp -> mapToResponse(exp, splitRepo.findByExpenseId(exp.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseResponse> getExpensesByUser(UUID userId) {
        return expenseRepo.findByPaidBy(userId).stream()
                .map(exp -> mapToResponse(exp, splitRepo.findByExpenseId(exp.getId())))
                .collect(Collectors.toList());
    }

    // --- Helper methods ---

    private List<ExpenseSplit> buildSplits(Expense expense, CreateExpenseRequest request) {
        List<SplitDetailRequest> participants = request.getParticipants();

        if (participants == null || participants.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Expense must have at least one participant");
        }

        double totalSplit = 0;
        List<ExpenseSplit> splits = new ArrayList<>();

        switch (request.getSplitType().toUpperCase()) {
            case "EQUAL" -> {
                double equalShare = request.getAmount() / participants.size();
                for (SplitDetailRequest p : participants) {
                    splits.add(ExpenseSplit.builder()
                            .expense(expense)
                            .userId(p.getUserId())
                            .amount(BigDecimal.valueOf(equalShare)) // optional precision fix
                            .createdAt(OffsetDateTime.now())
                            .build());
                    ;
                }
            }
            case "PERCENTAGE" -> {
                for (SplitDetailRequest p : participants) {
                    if (p.getPercentage() == null)
                        throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Percentage missing for user");
                    double amount = request.getAmount() * (p.getPercentage() / 100);
                    totalSplit += amount;
                    splits.add(ExpenseSplit.builder()
                            .expense(expense)
                            .userId(p.getUserId())
                            .amount(BigDecimal.valueOf(amount))
                            .percentage(p.getPercentage())
                            .createdAt(OffsetDateTime.now())
                            .build());
                }
                if (Math.abs(totalSplit - request.getAmount()) > 0.1)
                    throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Percentages must sum to 100%");
            }
            case "EXACT" -> {
                for (SplitDetailRequest p : participants) {
                    if (p.getAmount() == null)
                        throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Amount missing for user");
                    totalSplit += p.getAmount();
                    splits.add(ExpenseSplit.builder()
                            .expense(expense)
                            .userId(p.getUserId())
                            .amount(BigDecimal.valueOf(p.getAmount()))
                            .createdAt(OffsetDateTime.now())
                            .build());
                }
                if (Math.abs(totalSplit - request.getAmount()) > 0.1)
                    throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Split amounts must sum to total");
            }
            default -> throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Invalid split type");
        }

        log.info("Created {} splits for expense {}", splits.size(), expense.getId());
        return splits;
    }

    private ExpenseResponse mapToResponse(Expense expense, List<ExpenseSplit> splits) {
        List<SplitDetailResponse> splitResponses = splits.stream()
                .map(s -> SplitDetailResponse.builder()
                        .userId(s.getUserId())
                        .amount(s.getAmount())
                        .percentage(s.getPercentage())
                        .build())
                .toList();

        return ExpenseResponse.builder()
                .id(expense.getId())
                .groupId(expense.getGroupId())
                .description(expense.getDescription())
                .amount(expense.getAmount().doubleValue())
                .paidBy(expense.getPaidBy())
                .splitType(expense.getSplitType())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .participants(splitResponses)
                .build();
    }
}
