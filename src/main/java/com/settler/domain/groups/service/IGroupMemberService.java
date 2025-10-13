package com.settler.domain.groups.service;

import java.util.List;
import java.util.UUID;

public interface IGroupMemberService {
    void addMember(UUID groupId, UUID userId, String addedByEmail);
    void removeMember(UUID groupId, UUID memberId, String removedByEmail);
    List<Object> getMembers(UUID groupId);
}
