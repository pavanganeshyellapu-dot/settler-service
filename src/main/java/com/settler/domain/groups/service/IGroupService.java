package com.settler.domain.groups.service;

import com.settler.domain.groups.entity.Group;
import java.util.List;
import java.util.UUID;

public interface IGroupService {
    Group createGroup(String name, String currencyCode, UUID ownerId);
    List<Group> getGroupsByUser(UUID userId);
    Group getGroupById(UUID groupId);
    void deleteGroup(UUID groupId);
}
