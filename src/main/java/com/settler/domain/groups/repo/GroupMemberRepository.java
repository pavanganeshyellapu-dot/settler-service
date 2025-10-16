package com.settler.domain.groups.repo;

import com.settler.domain.groups.entity.GroupMember;
import com.settler.domain.groups.entity.Group;
import com.settler.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    List<GroupMember> findByGroup(Group group);
    List<GroupMember> findByUser(User user);
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    void deleteByGroupAndUser(Group group, User user);
}
