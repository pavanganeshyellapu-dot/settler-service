package com.settler.domain.groups.repo;

import com.settler.domain.groups.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    List<Group> findByOwnerId(UUID ownerId);
}
