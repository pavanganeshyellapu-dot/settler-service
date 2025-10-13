package com.settler.domain.groups.service.impl;

import com.settler.domain.groups.entity.Group;
import com.settler.domain.groups.entity.GroupMember;
import com.settler.domain.groups.repo.GroupMemberRepository;
import com.settler.domain.groups.repo.GroupRepository;
import com.settler.domain.groups.service.IGroupMemberService;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.repo.UserRepository;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements IGroupMemberService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    /**
     * ➕ Add a new member to a group
     */
    @Override
    @Transactional
    public void addMember(UUID groupId, UUID userId, String addedByEmail) {
        log.info("➕ Adding user {} to group {}", userId, groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not found"));
        User addedByUser = userRepository.findByEmail(addedByEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Added-by user not found"));

        // 🧠 Validate: only member or admin can add
        GroupMember addedByMember = groupMemberRepository.findByGroupAndUser(group, addedByUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "You are not a member of this group"));

        if (!(addedByMember.getRole() == GroupMember.Role.ADMIN || addedByMember.getRole() == GroupMember.Role.MEMBER)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "You don't have permission to add members");
        }

        // 🧩 Prevent duplicates
        boolean exists = groupMemberRepository.findByGroupAndUser(group, user).isPresent();
        if (exists) throw new BusinessException(ErrorCode.ALREADY_EXISTS, "User already a member");

        // ✅ Add member as MEMBER
        GroupMember newMember = GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupMember.Role.MEMBER)
                .build();

        groupMemberRepository.save(newMember);
        log.info("✅ Added member {} to group {}", user.getEmail(), group.getName());
    }

    /**
     * ❌ Remove a member from a group
     */
    @Override
    @Transactional
    public void removeMember(UUID groupId, UUID memberId, String removedByEmail) {
        log.info("❌ Removing user {} from group {}", memberId, groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Group not found"));
        User memberToRemove = userRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not found"));
        User removedByUser = userRepository.findByEmail(removedByEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Removed-by user not found"));

        // 🧠 Validate: only admin can remove
        GroupMember remover = groupMemberRepository.findByGroupAndUser(group, removedByUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "You are not a member of this group"));

        if (remover.getRole() != GroupMember.Role.ADMIN) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Only admins can remove members");
        }

        // ✅ Perform removal
        groupMemberRepository.deleteByGroupAndUser(group, memberToRemove);
        log.info("✅ Removed member {} from group {}", memberToRemove.getEmail(), group.getName());
    }

    /**
     * 📋 List all members in a group
     */
    @Override
    @Transactional(readOnly = true)
    public List<Object> getMembers(UUID groupId) {
        log.info("📋 Fetching members for group {}", groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Group not found"));

        return Collections.singletonList(groupMemberRepository.findByGroup(group)
                .stream()
                .map(member -> {
                    var dto = new HashMap<String, Object>();
                    dto.put("id", member.getUser().getId());
                    dto.put("email", member.getUser().getEmail());
                    dto.put("displayName", member.getUser().getDisplayName());
                    dto.put("role", member.getRole().name());
                    dto.put("joinedAt", member.getJoinedAt());
                    return dto;
                })
                .toList());
    }
}
