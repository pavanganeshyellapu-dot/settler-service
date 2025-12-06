package com.settler.domain.settlements.mapper;

import com.settler.domain.settlements.dto.SettlementResponse;
import com.settler.domain.settlements.entity.Settlement;

import java.math.BigDecimal;

public class SettlementMapper {

    public static SettlementResponse toResponse(Settlement s, String fromUserName, String toUserName) {
        return toResponse(s, fromUserName, toUserName, null);
    }

    public static SettlementResponse toResponse(
            Settlement s,
            String fromUserName,
            String toUserName,
            BigDecimal remaining
    ) {

        Boolean fullySettled = null;
        if (remaining != null) {
            fullySettled = remaining.compareTo(BigDecimal.ZERO) == 0;
        }

        return SettlementResponse.builder()
                .id(s.getId())
                .groupId(s.getGroupId())
                .fromUserId(s.getFromUserId())
                .fromUserName(fromUserName)
                .toUserId(s.getToUserId())
                .toUserName(toUserName)
                .amount(s.getAmount())
                .notes(s.getNotes())
                .confirmed(s.getConfirmed())
                .createdAt(s.getCreatedAt())
                .settledAt(s.getSettledAt())
                .remainingBetweenUsers(remaining)
                .fullySettled(fullySettled)
                .build();
    }
}
