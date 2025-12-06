package com.settler.domain.settlements.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementResponse {

    private UUID id;

    private UUID groupId;

    private UUID fromUserId;
    private String fromUserName;

    private UUID toUserId;
    private String toUserName;

    private BigDecimal amount;
    private String notes;

    private Boolean confirmed;

    private OffsetDateTime createdAt;
    private OffsetDateTime settledAt;

    private BigDecimal remainingBetweenUsers;
    private Boolean fullySettled;
}
