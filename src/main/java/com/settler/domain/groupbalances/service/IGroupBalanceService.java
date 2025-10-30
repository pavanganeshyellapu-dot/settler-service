package com.settler.domain.groupbalances.service;

import com.settler.domain.groupbalances.dto.response.SettlementResponse;
import com.settler.domain.groupbalances.entity.GroupBalance;

import java.util.List;
import java.util.UUID;

public interface IGroupBalanceService {
    void recalculateBalances(UUID groupId, String correlationId);
    List<GroupBalance> getGroupBalances(UUID groupId, String correlationId);
    List<SettlementResponse> calculateSettlements(UUID groupId, String correlationId);


}
