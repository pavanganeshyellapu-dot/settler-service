package com.settler.domain.users.service.impl;

import com.settler.domain.users.entity.User;
import com.settler.domain.users.enums.UserRole;
import com.settler.domain.users.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@settler.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@settler.com")
                    .displayName("Admin")
                    .password(passwordEncoder.encode("Rahulsaiy18"))
                    .role(UserRole.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("✅ Default admin user created: admin@settler.com / Rahulsaiy18");
        } else {
            log.info("🟢 Admin user already exists.");
        }
    }
}

