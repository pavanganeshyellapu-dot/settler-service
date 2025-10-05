package com.settler.domain.users.service;

import com.settler.domain.users.entity.User;
import java.util.List;
import java.util.UUID;

public interface IUserService {
    User createUser(String email, String displayName);
    User getUserById(UUID id);
    List<User> getAllUsers();
}
