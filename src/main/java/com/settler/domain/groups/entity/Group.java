package com.settler.domain.groups.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    // FIX: Using @Builder.Default ensures the default value (= false)
    // is respected when using Group.builder().build()
    @Builder.Default
    @Column(name = "simplify_balances", nullable = false)
    private boolean simplifyBalances = false;

    // FIX: Apply @Builder.Default here as well
    @Builder.Default
    @Column(name = "allow_member_edits", nullable = false)
    private boolean allowMemberEdits = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}
