package com.settler.domain.settlements.service;

import com.settler.domain.settlements.dto.SettlementRequest;
import com.settler.domain.settlements.dto.SettlementResponse;

import java.util.List;
import java.util.UUID;

public interface ISettlementService {

    SettlementResponse confirmSettlement(SettlementRequest request, String correlationId);

    List<SettlementResponse> getSettlementsByGroup(UUID groupId);

    List<SettlementResponse> getUserSettlements(UUID userId);
}
