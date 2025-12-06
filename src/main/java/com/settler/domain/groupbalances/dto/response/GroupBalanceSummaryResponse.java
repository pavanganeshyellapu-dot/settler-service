package com.settler.domain.groupbalances.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBalanceSummaryResponse {

    private UUID groupId;

    private Integer totalUsers;
    private Integer totalDebtors;
    private Integer totalCreditors;

    private BigDecimal totalUnsettledAmount;

    private Boolean isFullySettled;

    private UUID largestDebtorUserId;
    private String largestDebtorName;
    private BigDecimal largestDebtorAmount;

    private UUID largestCreditorUserId;
    private String largestCreditorName;
    private BigDecimal largestCreditorAmount;
}
