package com.settler.domain.expenses.service.impl;

import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.entity.ExpenseSplit;
import com.settler.domain.expenses.repo.ExpenseRepository;
import com.settler.domain.expenses.repo.ExpenseSplitRepository;
import com.settler.domain.expenses.service.IExpenseService;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@Transactional
public class ExpenseServiceImpl implements IExpenseService {

    private final ExpenseRepository expenseRepo;
    private final ExpenseSplitRepository splitRepo;

    public ExpenseServiceImpl(ExpenseRepository expenseRepo, ExpenseSplitRepository splitRepo) {
        this.expenseRepo = expenseRepo;
        this.splitRepo = splitRepo;
    }

    @Override
    public Expense addExpense(Expense expense, List<UUID> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "At least one participant required");
        }

        expense.setCreatedAt(OffsetDateTime.now());
        expense.setUpdatedAt(OffsetDateTime.now());
        Expense savedExpense = expenseRepo.save(expense);

        String splitType = expense.getSplitType().toUpperCase(Locale.ROOT);
        List<ExpenseSplit> splits;

        switch (splitType) {
            case "EQUAL" -> splits = calculateEqualSplit(savedExpense, participantIds);
            case "PERCENTAGE" -> splits = calculatePercentageSplit(savedExpense, participantIds, expense.getPercentageMap());
            case "SHARE" -> splits = calculateShareSplit(savedExpense, participantIds, expense.getShareMap());
            default -> throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Invalid split type: " + splitType);
        }

        splitRepo.saveAll(splits);
        return savedExpense;
    }


    @Override
    public List<Expense> getExpensesByGroup(UUID groupId) {
        return expenseRepo.findByGroupId(groupId);
    }

    @Override
    public void deleteExpense(UUID expenseId) {
        if (!expenseRepo.existsById(expenseId))
            throw new BusinessException(ErrorCode.NOT_FOUND, "Expense not found");

        splitRepo.deleteByExpenseId(expenseId);
        expenseRepo.deleteById(expenseId);
    }
    // --- SPLIT CALCULATIONS ---

    private List<ExpenseSplit> calculateEqualSplit(Expense expense, List<UUID> participants) {
        BigDecimal total = BigDecimal.valueOf(expense.getAmount());
        BigDecimal share = total.divide(BigDecimal.valueOf(participants.size()), 2, BigDecimal.ROUND_HALF_UP);

        List<ExpenseSplit> splits = new ArrayList<>();
        for (UUID userId : participants) {
            splits.add(ExpenseSplit.builder()
                    .expenseId(expense.getId())
                    .userId(userId)
                    .amount(share)
                    .build());
        }
        return splits;
    }

    private List<ExpenseSplit> calculatePercentageSplit(Expense expense, List<UUID> participants,
                                                        Map<UUID, BigDecimal> percentageMap) {
        BigDecimal total = BigDecimal.valueOf(expense.getAmount());
        List<ExpenseSplit> splits = new ArrayList<>();

        // if no custom percentages given → fallback to equal
        if (percentageMap == null || percentageMap.isEmpty()) {
            BigDecimal equalPercent = BigDecimal.valueOf(100.0 / participants.size());
            for (UUID userId : participants) {
                BigDecimal share = total.multiply(equalPercent).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                splits.add(ExpenseSplit.builder()
                        .expenseId(expense.getId())
                        .userId(userId)
                        .amount(share)
                        .build());
            }
            return splits;
        }

        // validate total = 100
        BigDecimal sum = percentageMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sum.compareTo(BigDecimal.valueOf(100)) != 0)
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Percentages must total 100%");

        // calculate per-user share
        for (UUID userId : participants) {
            BigDecimal percent = percentageMap.getOrDefault(userId, BigDecimal.ZERO);
            BigDecimal share = total.multiply(percent).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            splits.add(ExpenseSplit.builder()
                    .expenseId(expense.getId())
                    .userId(userId)
                    .amount(share)
                    .build());
        }
        return splits;
    }

    private List<ExpenseSplit> calculateShareSplit(Expense expense, List<UUID> participants,
                                                   Map<UUID, Integer> shareMap) {
        List<ExpenseSplit> splits = new ArrayList<>();
        BigDecimal total = BigDecimal.valueOf(expense.getAmount());

        // if no shareMap provided → default 1 share per participant
        if (shareMap == null || shareMap.isEmpty()) {
            int totalShares = participants.size();
            BigDecimal shareAmount = total.divide(BigDecimal.valueOf(totalShares), 2, BigDecimal.ROUND_HALF_UP);
            for (UUID userId : participants) {
                splits.add(ExpenseSplit.builder()
                        .expenseId(expense.getId())
                        .userId(userId)
                        .amount(shareAmount)
                        .build());
            }
            return splits;
        }

        // calculate total shares
        int totalShares = shareMap.values().stream().mapToInt(Integer::intValue).sum();
        if (totalShares <= 0)
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Invalid share distribution");

        // calculate share amount
        BigDecimal perShare = total.divide(BigDecimal.valueOf(totalShares), 2, BigDecimal.ROUND_HALF_UP);
        for (UUID userId : participants) {
            int shares = shareMap.getOrDefault(userId, 0);
            BigDecimal amount = perShare.multiply(BigDecimal.valueOf(shares));
            splits.add(ExpenseSplit.builder()
                    .expenseId(expense.getId())
                    .userId(userId)
                    .amount(amount)
                    .build());
        }
        return splits;
    }

}
