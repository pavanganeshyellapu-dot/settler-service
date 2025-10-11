package com.settler.domain.users.service;

import com.settler.domain.users.entity.User;
import java.util.List;
import java.util.UUID;

/**
 * Contract for user-related operations.
 */
public interface    IUserService {

    /**
     * Create a new user with given email and display name.
     * @param email unique email ID
     * @param displayName user’s display name
     * @return created User entity
     */
    User createUser(String email, String displayName);

    /**
     * Fetch user details by ID.
     * @param id user UUID
     * @return user if found, otherwise throws exception
     */
    User getUserById(UUID id);

    /**
     * Fetch list of all users (admin/internal usage)
     * @return list of users
     */
    List<User> getAllUsers();
}
