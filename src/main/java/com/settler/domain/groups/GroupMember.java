
package com.settler.domain.groups;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "group_members")
public class GroupMember {

	@Id
	@Column(name = "group_id", columnDefinition = "uuid")
	private UUID groupId;

	@Id
	@Column(name = "user_id", columnDefinition = "uuid")
	private UUID userId;

	@Builder.Default
	@Column(nullable = false)
	private String role = "MEMBER";

	@Builder.Default
	@Column(name = "joined_at", nullable = false)
	private OffsetDateTime joinedAt = OffsetDateTime.now();
}
