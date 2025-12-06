package com.settler.domain.settlements.service.impl;

import com.settler.domain.groupbalances.service.IGroupBalanceService;
import com.settler.domain.settlements.dto.SettlementRequest;
import com.settler.domain.settlements.dto.SettlementResponse;
import com.settler.domain.settlements.entity.Settlement;
import com.settler.domain.settlements.mapper.SettlementMapper;
import com.settler.domain.settlements.repository.SettlementRepository;
import com.settler.domain.settlements.service.ISettlementService;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.repo.UserRepository;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SettlementServiceImpl implements ISettlementService {

    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;
    private final IGroupBalanceService groupBalanceService;

    @Override
    public SettlementResponse createSettlement(SettlementRequest request, String correlationId) {

        // Validations
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Amount must be greater than zero");
        }
        if (request.getGroupId() == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "GroupId is required");
        }
        if (request.getFromUserId() == null || request.getToUserId() == null) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "From and To user ids are required");
        }

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        log.info("[{}] Creating settlement. groupId={}, fromUser={}, toUser={}, amount={}",
                correlationId, request.getGroupId(), request.getFromUserId(), request.getToUserId(), request.getAmount());

        OffsetDateTime now = OffsetDateTime.now();

        // Record settlement as completed payment
        Settlement settlement = Settlement.builder()
                .groupId(request.getGroupId())
                .fromUserId(request.getFromUserId())
                .toUserId(request.getToUserId())
                .amount(request.getAmount())
                .notes(request.getNotes())
                .createdAt(now)
                .updatedAt(now)
                .confirmed(true)
                .settledAt(now)
                .build();

        Settlement saved = settlementRepository.save(settlement);

        // Recalculate group balances after this payment
        groupBalanceService.recalculateBalances(request.getGroupId(), correlationId);

        // Compute remaining amount between the two users
        BigDecimal remaining = groupBalanceService.getRemainingAmountBetweenUsers(
                request.getGroupId(),
                request.getFromUserId(),
                request.getToUserId()
        );

        // Load user names
        User fromUser = userRepository.findById(saved.getFromUserId()).orElse(null);
        User toUser = userRepository.findById(saved.getToUserId()).orElse(null);

        String fromUserName = fromUser != null ? fromUser.getDisplayName() : "Unknown";
        String toUserName = toUser != null ? toUser.getDisplayName() : "Unknown";

        // Map with remaining
        return SettlementMapper.toResponse(saved, fromUserName, toUserName, remaining);
    }

    @Override
    public List<SettlementResponse> getSettlementsByGroup(UUID groupId) {
        return settlementRepository.findByGroupId(groupId).stream()
                .map(this::mapToResponseWithoutRemaining)
                .collect(Collectors.toList());
    }

    @Override
    public List<SettlementResponse> getUserSettlements(UUID userId) {
        List<Settlement> list = settlementRepository.findByFromUserIdOrToUserId(userId, userId);
        return list.stream()
                .map(this::mapToResponseWithoutRemaining)
                .collect(Collectors.toList());
    }

    private SettlementResponse mapToResponseWithoutRemaining(Settlement s) {
        User fromUser = userRepository.findById(s.getFromUserId()).orElse(null);
        User toUser = userRepository.findById(s.getToUserId()).orElse(null);

        String fromUserName = fromUser != null ? fromUser.getDisplayName() : "Unknown";
        String toUserName = toUser != null ? toUser.getDisplayName() : "Unknown";

        // For list endpoints we skip remainingBetweenUsers
        return SettlementMapper.toResponse(s, fromUserName, toUserName);
    }
}


