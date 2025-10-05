package com.settler.domain.settlements.service;

import com.settler.domain.settlements.entity.Settlement;
import java.util.List;
import java.util.UUID;

public interface ISettlementService {
    Settlement createSettlement(Settlement settlement);
    Settlement confirmSettlement(UUID id);
    List<Settlement> getSettlementsByGroup(UUID groupId);
    List<Settlement> getUserSettlements(UUID userId);
}
