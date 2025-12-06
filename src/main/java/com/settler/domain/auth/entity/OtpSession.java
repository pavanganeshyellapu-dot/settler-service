package com.settler.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpSession {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id; // sessionId

    @Column(nullable = false)
    private String identifier; // phone or email

    @Column(nullable = false)
    private String channel; // PHONE or EMAIL

    @Column(nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private Boolean verified;

    @Column(nullable = false)
    private Integer attempts;

    @Column(nullable = false)
    private String purpose; // LOGIN, etc

    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
