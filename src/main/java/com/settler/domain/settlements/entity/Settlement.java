package com.settler.domain.settlements.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a payment or settlement transaction between users.
 */
@Entity
@Table(name = "settlements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "from_user_id", nullable = false)
    private UUID fromUserId;  // who paid

    @Column(name = "to_user_id", nullable = false)
    private UUID toUserId;    // who received

    @Column(nullable = false)
    private Double amount;

    @Column(name = "mode_of_payment", nullable = false)
    private String modeOfPayment; // CASH, UPI, PHONEPE, GPAY, PAYTM

    // FIX: Added @Builder.Default to ensure 'false' is used as the default
    // when using the Lombok builder.
    @Builder.Default
    @Column(nullable = false)
    private boolean confirmed = false; // true after both users verify

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
