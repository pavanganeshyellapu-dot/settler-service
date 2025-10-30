package com.settler.domain.groupbalances.service.impl;

import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.repo.ExpenseRepository;
import com.settler.domain.groupbalances.dto.response.SettlementResponse;
import com.settler.domain.groupbalances.entity.GroupBalance;
import com.settler.domain.groupbalances.repo.GroupBalanceRepository;
import com.settler.domain.groupbalances.service.IGroupBalanceService;
import com.settler.readmodel.BalanceCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class GroupBalanceServiceImpl implements IGroupBalanceService {

    private final ExpenseRepository expenseRepository;
    private final GroupBalanceRepository groupBalanceRepository;

    public GroupBalanceServiceImpl(ExpenseRepository expenseRepository,
                                   GroupBalanceRepository groupBalanceRepository) {
        this.expenseRepository = expenseRepository;
        this.groupBalanceRepository = groupBalanceRepository;
    }

    @Override
    public void recalculateBalances(UUID groupId, String correlationId) {
        log.info("[{}] Recalculating balances for group {}", correlationId, groupId);

        // 1️⃣ Fetch all expenses for the group
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        if (expenses.isEmpty()) {
            log.warn("[{}] No expenses found for group {}", correlationId, groupId);
            return;
        }

        // 2️⃣ Compute net balances for each user
        Map<UUID, BigDecimal> netBalances = BalanceCalculator.calculateNetBalances(expenses);

        // 3️⃣ Update or insert group balances
        netBalances.forEach((userId, balance) -> {
            GroupBalance gb = groupBalanceRepository.findByGroupIdAndUserId(groupId, userId)
                    .orElse(GroupBalance.builder()
                            .groupId(groupId)
                            .userId(userId)
                            .build());
            gb.setBalance(balance);
            gb.setUpdatedAt(OffsetDateTime.now());
            groupBalanceRepository.save(gb);
        });

        log.info("[{}] ✅ Group balances recalculated for {} users in group {}", correlationId, netBalances.size(), groupId);
    }

    @Override
    public List<GroupBalance> getGroupBalances(UUID groupId, String correlationId) {
        log.info("[{}] Retrieving balances for group {}", correlationId, groupId);
        return groupBalanceRepository.findByGroupId(groupId);
    }

    @Override
    public List<SettlementResponse> calculateSettlements(UUID groupId, String correlationId) {
        log.info("[{}] Calculating settlements for group {}", correlationId, groupId);

        List<GroupBalance> balances = groupBalanceRepository.findByGroupId(groupId);
        if (balances.isEmpty()) {
            log.warn("[{}] No balances found for group {}", correlationId, groupId);
            return Collections.emptyList();
        }

        List<GroupBalance> debtors = balances.stream()
                .filter(b -> b.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .sorted(Comparator.comparing(GroupBalance::getBalance))
                .collect(Collectors.toList());

        List<GroupBalance> creditors = balances.stream()
                .filter(b -> b.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.getBalance().compareTo(a.getBalance()))
                .collect(Collectors.toList());

        List<SettlementResponse> settlements = new ArrayList<>();

        int i = 0, j = 0;
        while (i < debtors.size() && j < creditors.size()) {
            GroupBalance debtor = debtors.get(i);
            GroupBalance creditor = creditors.get(j);

            BigDecimal amount = debtor.getBalance().abs().min(creditor.getBalance());
            settlements.add(SettlementResponse.builder()
                    .fromUserId(debtor.getUserId())
                    .toUserId(creditor.getUserId())
                    .amount(amount)
                    .build());

            // Adjust balances
            debtor.setBalance(debtor.getBalance().add(amount));
            creditor.setBalance(creditor.getBalance().subtract(amount));

            if (debtor.getBalance().compareTo(BigDecimal.ZERO) == 0) i++;
            if (creditor.getBalance().compareTo(BigDecimal.ZERO) == 0) j++;
        }

        log.info("[{}] Generated {} settlement suggestions", correlationId, settlements.size());
        return settlements;
    }


}
