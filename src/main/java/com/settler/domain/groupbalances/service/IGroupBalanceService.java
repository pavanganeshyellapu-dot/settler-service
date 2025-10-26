package com.settler.domain.groupbalances.service;

import java.util.UUID;

public interface IGroupBalanceService {
    void recalculateBalances(UUID groupId, String correlationId);
}
