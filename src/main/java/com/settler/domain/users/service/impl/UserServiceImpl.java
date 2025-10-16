package com.settler.domain.users.service.impl;

import com.settler.domain.users.dto.UserResponse;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.enums.UserRole;
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
    public UserResponse getUserById(UUID id) {
        log.info("🔍 [Service] Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ User not found for ID: {}", id);
                    return new BusinessException(ErrorCode.NOT_FOUND, "User not found");
                });

        // ✅ Return only safe data
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .build();
    }



    @Override
    public List<UserResponse> getAllUsers() {
        log.info("📋 [Service] Fetching all users");

        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .displayName(user.getDisplayName())
                        .role(user.getRole())
                        .build())
                .toList();
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("🔍 [Service] Fetching user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(UserRole.valueOf(user.getRole().name()))
                .build();
    }


}
