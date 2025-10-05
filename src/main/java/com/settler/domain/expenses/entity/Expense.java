package com.settler.domain.expenses.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "paid_by", nullable = false)
    private UUID paidBy; // ✅ renamed for clarity

    @Column(nullable = false)
    private BigDecimal amount; // ✅ changed from Double to BigDecimal

    @Column(nullable = false)
    private String description;

    @Column(name = "split_type", nullable = false)
    private String splitType = "EQUAL";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "expense_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ExpenseSplit> participants; // ✅ mapped participants

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
