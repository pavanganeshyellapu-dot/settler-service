package com.settler.domain.groups.service;

import com.settler.domain.groups.entity.Group;
import com.settler.domain.groups.repo.GroupRepository;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GroupServiceImpl implements IGroupService {

    private final GroupRepository repo;

    public GroupServiceImpl(GroupRepository repo) {
        this.repo = repo;
    }

    @Override
    public Group createGroup(String name, String currencyCode, UUID ownerId) {
        if (name == null || name.trim().isEmpty())
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Group name cannot be empty");

        Group group = Group.builder()
                .name(name)
                .currencyCode(currencyCode)
                .ownerId(ownerId)
                .build();

        return repo.save(group);
    }

    @Override
    public Group getGroupById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
    }

    @Override
    public List<Group> getAllGroups() {
        return repo.findAll();
    }
}
