package com.settler.domain.groupbalances.service;

import com.settler.domain.groupbalances.dto.response.GroupBalanceResponse;
import com.settler.domain.groupbalances.dto.response.GroupBalanceSummaryResponse;
import com.settler.domain.groupbalances.dto.response.GroupSettlementSuggestionResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IGroupBalanceService {

    void recalculateBalances(UUID groupId, String correlationId);

    List<GroupBalanceResponse> getGroupBalances(UUID groupId, String correlationId);

    List<GroupSettlementSuggestionResponse> calculateSettlements(UUID groupId, String correlationId);

    BigDecimal getRemainingAmountBetweenUsers(UUID groupId, UUID fromUserId, UUID toUserId);

    GroupBalanceSummaryResponse getGroupSummary(UUID groupId, String correlationId);

}
