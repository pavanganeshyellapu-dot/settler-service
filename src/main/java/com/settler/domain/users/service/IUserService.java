package com.settler.domain.users.service;

import com.settler.domain.users.dto.UserResponse;
import com.settler.domain.users.entity.User;
import java.util.List;
import java.util.UUID;

/**
 * Contract for user-related operations.
 */
public interface IUserService {

    /**
     * Fetch user details by ID.
     * @param id user UUID
     * @return user if found, otherwise throws exception
     */
    UserResponse getUserById(UUID id);

    /**
     * Fetch list of all users (admin/internal usage)
     * @return list of users
     */
    List<UserResponse> getAllUsers();

    /**
     * Fetch user details by ID.
     * @param id user UUID
     * @return user if found, otherwise throws exception
     */
    UserResponse getUserByEmail(String email);

}
