package com.settler.domain.users.service;

import com.settler.domain.users.entity.User;
import com.settler.domain.users.repo.UserRepository;
import com.settler.exceptions.BusinessException;
import com.settler.exceptions.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User createUser(String email, String displayName) {
        repo.findByEmail(email).ifPresent(u -> {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Email already exists: " + email);
        });

        User user = User.builder()
                .email(email)
                .displayName(displayName)
                .build();
        return repo.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }
}
