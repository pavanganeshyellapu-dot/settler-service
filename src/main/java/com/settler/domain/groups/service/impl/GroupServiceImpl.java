package com.settler.domain.groups.service.impl;

import com.settler.domain.groups.entity.Group;
import com.settler.domain.groups.repo.GroupRepository;
import com.settler.domain.groups.service.IGroupService;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.repo.UserRepository;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements IGroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    public Group createGroup(String name, String currencyCode, UUID ownerId) {
        log.info("🆕 Creating group: {}, owner: {}", name, ownerId);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Owner not found"));

        Group group = Group.builder()
                .name(name)
                .currencyCode(currencyCode)
                .owner(owner)
                .createdAt(OffsetDateTime.now())
                .build();

        groupRepository.save(group);
        log.info("✅ Group created successfully: {}", group.getName());

        return group;
    }

    @Override
    public List<Group> getGroupsByUser(UUID userId) {
        log.info("📋 Fetching groups for user: {}", userId);
        return groupRepository.findByOwnerId(userId);
    }

    @Override
    public Group getGroupById(UUID groupId) {
        log.info("🔍 Fetching group by ID: {}", groupId);
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Group not found"));
    }

    @Override
    public void deleteGroup(UUID groupId) {
        log.info("🗑️ Deleting group ID: {}", groupId);
        groupRepository.deleteById(groupId);
    }
}
