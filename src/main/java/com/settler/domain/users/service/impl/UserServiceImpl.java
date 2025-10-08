package com.settler.domain.users.service.impl;

import com.settler.domain.users.entity.User;
import com.settler.domain.users.repo.UserRepository;
import com.settler.domain.users.service.IUserService;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String email, String displayName) {
        log.info("🧩 [Service] Creating user with email: {}", email);

        // Validation
        if (email == null || email.trim().isEmpty())
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Email cannot be empty");
        if (displayName == null || displayName.trim().isEmpty())
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Display name cannot be empty");

        // Duplicate check
        if (userRepository.existsByEmail(email)) {
            log.warn("⚠️ Duplicate user creation attempt with email: {}", email);
            throw new BusinessException(ErrorCode.CONFLICT, "Email already registered");
        }

        // Create entity
        User user = User.builder()
                .email(email)
                .displayName(displayName)
                .build();

        User saved = userRepository.save(user);
        log.info("✅ User created successfully with ID: {}", saved.getId());

        return saved;
    }

    @Override
    public User getUserById(UUID id) {
        log.info("🔍 [Service] Fetching user by ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ User not found for ID: {}", id);
                    return new BusinessException(ErrorCode.NOT_FOUND, "User not found");
                });
    }

    @Override
    public List<User> getAllUsers() {
        log.info("📜 [Service] Fetching all users");

        List<User> users = userRepository.findAll();
        log.debug("Total users found: {}", users.size());

        return users;
    }
}
