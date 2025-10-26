package com.settler.domain.groupbalances.repo;

import com.settler.domain.groupbalances.entity.GroupBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface GroupBalanceRepository extends JpaRepository<GroupBalance, UUID> {
    List<GroupBalance> findByGroupId(UUID groupId);
    Optional<GroupBalance> findByGroupIdAndUserId(UUID groupId, UUID userId);
}
