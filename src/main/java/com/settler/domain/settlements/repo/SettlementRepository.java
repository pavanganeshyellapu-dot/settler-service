package com.settler.domain.settlements.repo;

import com.settler.domain.settlements.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    List<Settlement> findByGroupId(UUID groupId);
    List<Settlement> findByFromUserId(UUID fromUserId);
    List<Settlement> findByToUserId(UUID toUserId);
}
