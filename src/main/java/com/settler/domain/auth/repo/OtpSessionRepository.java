package com.settler.domain.auth.repo;

import com.settler.domain.auth.entity.OtpSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtpSessionRepository extends JpaRepository<OtpSession, UUID> {
}
