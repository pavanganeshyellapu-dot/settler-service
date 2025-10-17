package com.settler.domain.groups.service.impl;

import com.settler.domain.groups.entity.Group;
import com.settler.domain.groups.entity.GroupMember;
import com.settler.domain.groups.mapper.GroupMapper;
import com.settler.domain.groups.repo.GroupMemberRepository;
import com.settler.domain.groups.repo.GroupRepository;
import com.settler.domain.groups.service.IGroupService;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.repo.UserRepository;
import com.settler.dto.common.ApiResponse;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements IGroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    /**
     * 🆕 Create new group and add owner as ADMIN
     */
    @Override
    @Transactional
    public Group createGroup(String name, String currencyCode, String email) {
        log.info("🆕 Creating group: {}, owner: {}", name, email);

        User ownerEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "email not found"));

        // ✅ Create new group
        Group group = Group.builder()
                .name(name)
                .currencyCode(currencyCode)
                .owner(ownerEmail)
                .createdAt(OffsetDateTime.now())
                .build();

        groupRepository.save(group);

        // ✅ Add owner as ADMIN in group_members
        GroupMember adminMember = GroupMember.builder()
                .group(group)
                .user(ownerEmail)
                .role(GroupMember.Role.ADMIN)
                .joinedAt(OffsetDateTime.now())
                .build();

        groupMemberRepository.save(adminMember);

        log.info("✅ Group '{}' created and {} set as ADMIN", name, ownerEmail.getEmail());
        return group;
    }

    /**
     * 📋 Get all groups by user ID (where they are ADMIN or MEMBER)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Group> getGroupsByUser(UUID userId) {
        log.info("📋 Fetching groups for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not found"));

        // Fetch all GroupMember records for this user
        List<GroupMember> memberships = groupMemberRepository.findByUser(user);

        // Extract groups
        List<Group> groups = memberships.stream()
                .map(GroupMember::getGroup)
                .toList();

        // Force load owner for each group (avoid lazy proxy)
        groups.forEach(g -> {
            if (g.getOwner() != null) g.getOwner().getEmail();
        });

        return groups;
    }

    /**
     * 📋 Get all groups by user email (logged-in user)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Group> getGroupsByUserEmail(String email) {
        log.info("📋 Fetching groups for user email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not found"));

        List<GroupMember> memberships = groupMemberRepository.findByUser(user);

        List<Group> groups = memberships.stream()
                .map(GroupMember::getGroup)
                .toList();

        groups.forEach(g -> {
            if (g.getOwner() != null) g.getOwner().getEmail();
        });

        return groups;
    }

    /**
     * 🔍 Get group by ID
     */
    @Override
    @Transactional(readOnly = true)
    public Group getGroupById(UUID groupId) {
        log.info("🔍 Fetching group by ID: {}", groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Group not found"));

        if (group.getOwner() != null) group.getOwner().getEmail();
        return group;
    }

    /**
     * 🗑️ Delete group (Admin-only)
     */
    @Override
    @Transactional
    public void deleteGroup(UUID groupId) {
        log.info("🗑️ Deleting group ID: {}", groupId);

        if (!groupRepository.existsById(groupId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Group not found for deletion");
        }

        // Also delete members first (cascade not used)
        groupMemberRepository.findAll()
                .stream()
                .filter(member -> member.getGroup().getId().equals(groupId))
                .forEach(groupMemberRepository::delete);

        groupRepository.deleteById(groupId);
        log.info("✅ Group {} deleted successfully", groupId);
    }
}
