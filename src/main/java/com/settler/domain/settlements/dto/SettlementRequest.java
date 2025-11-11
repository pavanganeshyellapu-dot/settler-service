package com.settler.domain.settlements.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementRequest {
    private UUID groupId;
    private UUID fromUserId;
    private UUID toUserId;
    private BigDecimal amount;
}
