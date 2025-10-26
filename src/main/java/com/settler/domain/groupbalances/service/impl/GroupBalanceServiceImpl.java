package com.settler.domain.groupbalances.service.impl;

import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.repo.ExpenseRepository;
import com.settler.domain.groupbalances.entity.GroupBalance;
import com.settler.domain.groupbalances.repo.GroupBalanceRepository;
import com.settler.domain.groupbalances.service.IGroupBalanceService;
import com.settler.readmodel.BalanceCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
}
