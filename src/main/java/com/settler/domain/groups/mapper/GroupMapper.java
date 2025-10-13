package com.settler.domain.groups.mapper;

import com.settler.domain.groups.mapper.GroupMapper;
import com.settler.domain.groups.dto.GroupResponse;
import com.settler.domain.groups.entity.Group;
import com.settler.domain.groups.dto.MemberResponse;
import com.settler.domain.users.entity.User;
import java.util.Collections;
import java.util.List;

public class GroupMapper {

    // ✅ Convert single Group entity → GroupResponse DTO
    public static GroupResponse toDto(Group group) {
        if (group == null) return null;

        User owner = group.getOwner();

        // Owner mapped as MemberResponse (could reuse later for group members)
        MemberResponse ownerDto = null;
        if (owner != null) {
            ownerDto = MemberResponse.builder()
                    .id(owner.getId())
                    .email(owner.getEmail())
                    .displayName(owner.getDisplayName())
                    .role(owner.getRole().name()) // ADMIN / USER
                    .build();
        }

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .createdBy(owner != null ? owner.getDisplayName() : null)
                .members(ownerDto != null ? List.of(ownerDto) : Collections.emptyList())
                .createdAt(group.getCreatedAt())
                .build();
    }

    // ✅ Convert List<Group> → List<GroupResponse>
    public static List<GroupResponse> toDtoList(List<Group> groups) {
        if (groups == null) return Collections.emptyList();
        return groups.stream().map(GroupMapper::toDto).toList();
    }
}
