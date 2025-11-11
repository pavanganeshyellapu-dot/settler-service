package com.settler.domain.settlements.service;

import com.settler.domain.groupbalances.entity.GroupBalance;
import com.settler.domain.groupbalances.repo.GroupBalanceRepository;
import com.settler.domain.settlements.dto.SettlementRequest;
import com.settler.domain.settlements.dto.SettlementResponse;
import com.settler.domain.settlements.entity.Settlement;
import com.settler.domain.settlements.repo.SettlementRepository;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.repo.UserRepository;
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
@Slf4j
@Transactional
public class SettlementServiceImpl implements ISettlementService {

    private final SettlementRepository repo;
    private final GroupBalanceRepository groupBalanceRepository;
    private final UserRepository userRepository;

    public SettlementServiceImpl(SettlementRepository repo,
                                 GroupBalanceRepository groupBalanceRepository,
                                 UserRepository userRepository) {
        this.repo = repo;
        this.groupBalanceRepository = groupBalanceRepository;
        this.userRepository = userRepository;
    }

    /**
     * ✅ Confirm and record a settlement, then update balances
     */
    @Override
    public SettlementResponse confirmSettlement(SettlementRequest request, String correlationId) {
        log.info("[{}] Processing settlement: {} -> {} | ₹{}", correlationId,
                request.getFromUserId(), request.getToUserId(), request.getAmount());

        // 1️⃣ Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Amount must be greater than zero");
        }

        // 2️⃣ Fetch involved balances
        GroupBalance from = groupBalanceRepository.findByGroupIdAndUserId(request.getGroupId(), request.getFromUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Debtor balance not found"));
        GroupBalance to = groupBalanceRepository.findByGroupIdAndUserId(request.getGroupId(), request.getToUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Creditor balance not found"));

        // 3️⃣ Adjust balances
        from.setBalance(from.getBalance().add(request.getAmount())); // debtor pays
        to.setBalance(to.getBalance().subtract(request.getAmount())); // creditor receives

        groupBalanceRepository.save(from);
        groupBalanceRepository.save(to);

        // 4️⃣ Save settlement record
        Settlement settlement = Settlement.builder()
                .groupId(request.getGroupId())
                .fromUserId(request.getFromUserId())
                .toUserId(request.getToUserId())
                .amount(request.getAmount())
                .settledAt(OffsetDateTime.now())
                .confirmed(true)
                .build();

        repo.save(settlement);

        // 5️⃣ Map user names
        Map<UUID, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, User::getDisplayName));

        return SettlementResponse.builder()
                .id(settlement.getId())
                .groupId(request.getGroupId())
                .fromUserId(request.getFromUserId())
                .fromUserName(userNames.getOrDefault(request.getFromUserId(), "Unknown"))
                .toUserId(request.getToUserId())
                .toUserName(userNames.getOrDefault(request.getToUserId(), "Unknown"))
                .amount(request.getAmount())
                .settledAt(settlement.getSettledAt())
                .confirmed(true)
                .build();
    }

    /**
     * ✅ Fetch settlements for a group
     */
    @Override
    public List<SettlementResponse> getSettlementsByGroup(UUID groupId) {
        return repo.findByGroupId(groupId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * ✅ Fetch settlements involving a specific user
     */
    @Override
    public List<SettlementResponse> getUserSettlements(UUID userId) {
        List<Settlement> all = new ArrayList<>();
        all.addAll(repo.findByFromUserId(userId));
        all.addAll(repo.findByToUserId(userId));

        return all.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 🧩 Private mapper
    private SettlementResponse mapToResponse(Settlement s) {
        Map<UUID, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, User::getDisplayName));

        return SettlementResponse.builder()
                .id(s.getId())
                .groupId(s.getGroupId())
                .fromUserId(s.getFromUserId())
                .fromUserName(userNames.getOrDefault(s.getFromUserId(), "Unknown"))
                .toUserId(s.getToUserId())
                .toUserName(userNames.getOrDefault(s.getToUserId(), "Unknown"))
                .amount(s.getAmount())
                .settledAt(s.getSettledAt())
                .confirmed(s.getConfirmed())
                .build();
    }
}
