package com.settler.domain.groupbalances.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementResponse {
    private UUID fromUserId;
    private String fromUserName;  // optional for UI
    private UUID toUserId;
    private String toUserName;    // optional for UI
    private BigDecimal amount;
}
