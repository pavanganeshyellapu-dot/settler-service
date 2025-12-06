package com.settler.domain.groupbalances.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBalanceResponse {
    private UUID userId;
    private String userName;
    private BigDecimal balance;
    private Boolean settled;
}
