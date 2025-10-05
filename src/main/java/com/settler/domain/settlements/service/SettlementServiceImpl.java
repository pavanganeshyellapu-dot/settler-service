package com.settler.domain.settlements.service;

import com.settler.domain.settlements.entity.Settlement;
import com.settler.domain.settlements.repo.SettlementRepository;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SettlementServiceImpl implements ISettlementService {

    private final SettlementRepository repo;

    public SettlementServiceImpl(SettlementRepository repo) {
        this.repo = repo;
    }

    @Override
    public Settlement createSettlement(Settlement settlement) {
        if (settlement.getAmount() == null || settlement.getAmount() <= 0)
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Amount must be greater than zero");

        return repo.save(settlement);
    }

    @Override
    public Settlement confirmSettlement(UUID id) {
        Settlement s = repo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTLEMENT_NOT_FOUND));

        s.setConfirmed(true);
        return repo.save(s);
    }

    @Override
    public List<Settlement> getSettlementsByGroup(UUID groupId) {
        return repo.findByGroupId(groupId);
    }

    @Override
    public List<Settlement> getUserSettlements(UUID userId) {
        List<Settlement> from = repo.findByFromUserId(userId);
        List<Settlement> to = repo.findByToUserId(userId);
        from.addAll(to);
        return from;
    }
}
