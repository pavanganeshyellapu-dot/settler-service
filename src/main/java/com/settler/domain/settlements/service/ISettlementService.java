package com.settler.domain.settlements.service;

import com.settler.domain.settlements.dto.SettlementRequest;
import com.settler.domain.settlements.dto.SettlementResponse;

import java.util.List;
import java.util.UUID;

public interface ISettlementService {

    /**
     * Records a payment between two users inside a group.
     * The settlement is treated as completed immediately,
     * and balances are recalculated to compute remaining amount.
     */
    SettlementResponse createSettlement(SettlementRequest request, String correlationId);

    List<SettlementResponse> getSettlementsByGroup(UUID groupId);

    List<SettlementResponse> getUserSettlements(UUID userId);
}
