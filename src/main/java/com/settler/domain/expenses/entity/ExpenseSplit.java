package com.settler.domain.expenses.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expense_splits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplit {

	@Id
	@Column(nullable = false, updatable = false)
	private UUID id;

	@Column(name = "expense_id", nullable = false)
	private UUID expenseId;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "share_amount", nullable = false)
	private BigDecimal shareAmount;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@PrePersist
	public void prePersist() {
		if (id == null)
			id = UUID.randomUUID();
		if (createdAt == null)
			createdAt = OffsetDateTime.now();
	}
}
