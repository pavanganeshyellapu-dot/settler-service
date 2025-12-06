package com.settler.domain.groupbalances.service.impl;

import com.settler.domain.expenses.entity.Expense;
import com.settler.domain.expenses.repo.ExpenseRepository;
import com.settler.domain.groupbalances.dto.response.GroupBalanceResponse;
import com.settler.domain.groupbalances.dto.response.GroupBalanceSummaryResponse;
import com.settler.domain.groupbalances.dto.response.GroupSettlementSuggestionResponse;
import com.settler.domain.groupbalances.entity.GroupBalance;
import com.settler.domain.groupbalances.repo.GroupBalanceRepository;
import com.settler.domain.groupbalances.service.IGroupBalanceService;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.repo.UserRepository;
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
    private final UserRepository userRepository;

    public GroupBalanceServiceImpl(
            ExpenseRepository expenseRepository,
            GroupBalanceRepository groupBalanceRepository,
            UserRepository userRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.groupBalanceRepository = groupBalanceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void recalculateBalances(UUID groupId, String correlationId) {
        log.info("[{}] Recalculating balances for group {}", correlationId, groupId);

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        if (expenses.isEmpty()) {
            log.warn("[{}] No expenses found for group {}", correlationId, groupId);
            return;
        }

        Map<UUID, BigDecimal> netBalances = BalanceCalculator.calculateNetBalances(expenses);

        netBalances.forEach((userId, balance) -> {
            GroupBalance gb = groupBalanceRepository
                    .findByGroupIdAndUserId(groupId, userId)
                    .orElse(GroupBalance.builder()
                            .groupId(groupId)
                            .userId(userId)
                            .build());

            gb.setBalance(balance);
            gb.setUpdatedAt(OffsetDateTime.now());
            groupBalanceRepository.save(gb);
        });

        log.info("[{}] Updated balances for {} users", correlationId, netBalances.size());
    }

    @Override
    public List<GroupBalanceResponse> getGroupBalances(UUID groupId, String correlationId) {
        log.info("[{}] Fetching balances for group {}", correlationId, groupId);

        Map<UUID, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, User::getDisplayName));

        return groupBalanceRepository.findByGroupId(groupId).stream()
                .map(b -> GroupBalanceResponse.builder()
                        .userId(b.getUserId())
                        .userName(userNames.getOrDefault(b.getUserId(), "Unknown"))
                        .balance(b.getBalance())
                        .settled(b.getBalance().compareTo(BigDecimal.ZERO) == 0)
                        .build()
                )
                .toList();
    }

    @Override
    public List<GroupSettlementSuggestionResponse> calculateSettlements(UUID groupId, String correlationId) {
        log.info("[{}] Calculating settlement suggestions for group {}", correlationId, groupId);

        List<GroupBalance> balances = groupBalanceRepository.findByGroupId(groupId);
        if (balances.isEmpty()) {
            log.warn("[{}] No balances found for group {}", correlationId, groupId);
            return Collections.emptyList();
        }

        Map<UUID, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, User::getDisplayName));

        List<GroupBalance> debtors = balances.stream()
                .filter(b -> b.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .sorted(Comparator.comparing(GroupBalance::getBalance))
                .toList();

        List<GroupBalance> creditors = balances.stream()
                .filter(b -> b.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.getBalance().compareTo(a.getBalance()))
                .toList();

        List<GroupSettlementSuggestionResponse> settlements = new ArrayList<>();
        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            GroupBalance debtor = debtors.get(i);
            GroupBalance creditor = creditors.get(j);

            BigDecimal amount = debtor.getBalance().abs().min(creditor.getBalance());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) break;

            settlements.add(
                    GroupSettlementSuggestionResponse.builder()
                            .fromUserId(debtor.getUserId())
                            .fromUserName(userNames.getOrDefault(debtor.getUserId(), "Unknown"))
                            .toUserId(creditor.getUserId())
                            .toUserName(userNames.getOrDefault(creditor.getUserId(), "Unknown"))
                            .amount(amount)
                            .build()
            );

            debtor.setBalance(debtor.getBalance().add(amount));
            creditor.setBalance(creditor.getBalance().subtract(amount));

            if (debtor.getBalance().compareTo(BigDecimal.ZERO) == 0) i++;
            if (creditor.getBalance().compareTo(BigDecimal.ZERO) == 0) j++;
        }

        log.info("[{}] Created {} settlement suggestions", correlationId, settlements.size());
        return settlements;
    }

    @Override
    public BigDecimal getRemainingAmountBetweenUsers(UUID groupId, UUID fromUserId, UUID toUserId) {

        Optional<GroupBalance> fromBalanceOpt =
                groupBalanceRepository.findByGroupIdAndUserId(groupId, fromUserId);

        if (fromBalanceOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal balance = fromBalanceOpt.get().getBalance();

        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            return balance.abs();
        }

        return BigDecimal.ZERO;
    }

    @Override
    public GroupBalanceSummaryResponse getGroupSummary(UUID groupId, String correlationId) {

        log.info("[{}] Generating group summary for {}", correlationId, groupId);

        List<GroupBalance> balances = groupBalanceRepository.findByGroupId(groupId);

        if (balances.isEmpty()) {
            return GroupBalanceSummaryResponse.builder()
                    .groupId(groupId)
                    .totalUsers(0)
                    .totalDebtors(0)
                    .totalCreditors(0)
                    .totalUnsettledAmount(BigDecimal.ZERO)
                    .isFullySettled(true)
                    .build();
        }

        Map<UUID, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, User::getDisplayName));

        int totalUsers = balances.size();

        List<GroupBalance> debtors = balances.stream()
                .filter(b -> b.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .toList();

        List<GroupBalance> creditors = balances.stream()
                .filter(b -> b.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        int totalDebtors = debtors.size();
        int totalCreditors = creditors.size();

        BigDecimal totalUnsettled = balances.stream()
                .map(b -> b.getBalance().abs())
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        boolean fullySettled = balances.stream()
                .allMatch(b -> b.getBalance().compareTo(BigDecimal.ZERO) == 0);

        GroupBalance largestDebtor = debtors.stream()
                .min(Comparator.comparing(GroupBalance::getBalance)) // most negative
                .orElse(null);

        GroupBalance largestCreditor = creditors.stream()
                .max(Comparator.comparing(GroupBalance::getBalance))
                .orElse(null);

        return GroupBalanceSummaryResponse.builder()
                .groupId(groupId)
                .totalUsers(totalUsers)
                .totalDebtors(totalDebtors)
                .totalCreditors(totalCreditors)
                .totalUnsettledAmount(totalUnsettled)
                .isFullySettled(fullySettled)
                .largestDebtorUserId(largestDebtor != null ? largestDebtor.getUserId() : null)
                .largestDebtorName(largestDebtor != null ? userNames.get(largestDebtor.getUserId()) : null)
                .largestDebtorAmount(largestDebtor != null ? largestDebtor.getBalance().abs() : null)
                .largestCreditorUserId(largestCreditor != null ? largestCreditor.getUserId() : null)
                .largestCreditorName(largestCreditor != null ? userNames.get(largestCreditor.getUserId()) : null)
                .largestCreditorAmount(largestCreditor != null ? largestCreditor.getBalance() : null)
                .build();
    }

}




