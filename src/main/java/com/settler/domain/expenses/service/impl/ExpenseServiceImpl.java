package com.settler.domain.expenses.service.impl;

import com.settler.domain.expenses.dto.request.CreateExpenseRequest;
import com.settler.domain.expenses.dto.response.BalanceResponse;
import com.settler.domain.expenses.dto.response.ExpenseResponse;
import com.settler.domain.expenses.dto.response.GroupExpenseSummaryResponse;
import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.entity.ExpenseSplit;
import com.settler.domain.expenses.repo.ExpenseRepository;
import com.settler.domain.expenses.repo.ExpenseSplitRepository;
import com.settler.domain.expenses.service.IExpenseService;
import com.settler.domain.groupbalances.service.IGroupBalanceService;
import com.settler.readmodel.BalanceCalculator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ExpenseServiceImpl implements IExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final IGroupBalanceService groupBalanceService;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              ExpenseSplitRepository expenseSplitRepository,
                              IGroupBalanceService groupBalanceService) {
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.groupBalanceService = groupBalanceService;
    }

    @Override
    public ExpenseResponse createExpense(CreateExpenseRequest request, String correlationId, String sessionId) {
        log.info("[{}] Creating new expense in group: {}, sessionId={}", correlationId, request.getGroupId(), sessionId);

        // Step 1: Build and save expense
        Expense expense = Expense.builder()
                .groupId(request.getGroupId())
                .paidBy(request.getPaidBy())
                .amount(request.getAmount())
                .description(request.getDescription())
                .category(request.getCategory())
                .splitType(request.getSplitType())
                .createdAt(OffsetDateTime.now())
                .build();

        expense = expenseRepository.saveAndFlush(expense); // ensures ID generation

        // Step 2: Create splits (if provided)
        if (request.getSplits() != null && !request.getSplits().isEmpty()) {

            Expense finalExpense = expense;
            List<ExpenseSplit> splits = request.getSplits().stream()
                    .map(s -> ExpenseSplit.builder()
                            .expense(finalExpense)
                            .userId(s.getUserId())
                            .amount(s.getAmount())
                            .percentage(s.getPercentage())
                            .createdAt(OffsetDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            expenseSplitRepository.saveAllAndFlush(splits); // persist splits
            expense.setParticipants(splits);

            // After saving expense and splits
            Map<UUID, BigDecimal> balances = BalanceCalculator.calculateNetBalances(
                    expenseRepository.findByGroupId(expense.getGroupId())
            );

            groupBalanceService.recalculateBalances(expense.getGroupId(), correlationId);



            log.info("[{}] Inserted {} splits for expense {}", correlationId, splits.size(), expense.getId());
        } else {
            log.warn("[{}] No splits provided for expense {}", correlationId, expense.getId());
        }

        log.info("[{}] Expense created successfully with ID {}", correlationId, expense.getId());
        return ExpenseResponse.fromEntity(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(UUID id, String correlationId) {
        log.info("[{}] Fetching expense with ID {}", correlationId, id);

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));

        return ExpenseResponse.fromEntity(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupExpenseSummaryResponse getGroupExpenseSummary(UUID groupId, String correlationId) {
        log.info("[{}] Fetching expenses for group {}", correlationId, groupId);

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        if (expenses.isEmpty()) {
            return new GroupExpenseSummaryResponse(Collections.emptyList(), Collections.emptyList(), 0.0, OffsetDateTime.now().toLocalDateTime());
        }

        Map<UUID, BigDecimal> balances = BalanceCalculator.calculateNetBalances(expenses);

        List<ExpenseResponse> expenseResponses = expenses.stream()
                .map(ExpenseResponse::fromEntity)
                .collect(Collectors.toList());

        List<BalanceResponse> balanceResponses = balances.entrySet().stream()
                .map(e -> new BalanceResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        double totalSpent = expenses.stream()
                .map(e -> e.getAmount().doubleValue())
                .reduce(0.0, Double::sum);

        return new GroupExpenseSummaryResponse(expenseResponses, balanceResponses, totalSpent, OffsetDateTime.now().toLocalDateTime());
    }

    @Override
    public byte[] exportGroupExpensesToCSV(UUID groupId, String correlationId) {
        log.info("[{}] Exporting expenses to CSV for group {}", correlationId, groupId);

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out);
             CSVPrinter csvPrinter = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.builder()
                             .setHeader("Expense ID", "Description", "Category", "Paid By", "Amount", "Created At")
                             .build())) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Expense expense : expenses) {
                csvPrinter.printRecord(
                        expense.getId(),
                        expense.getDescription(),
                        expense.getCategory(),
                        expense.getPaidBy(),
                        expense.getAmount(),
                        expense.getCreatedAt() != null ? expense.getCreatedAt().format(formatter) : ""
                );
            }

            csvPrinter.flush();
            log.info("[{}] CSV export completed for group {}", correlationId, groupId);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("[{}] Error exporting group {} expenses to CSV: {}", correlationId, groupId, e.getMessage(), e);
            throw new RuntimeException("Error generating CSV", e);
        }
    }
}
