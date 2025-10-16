package com.settler.domain.expenses.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Value;

/**
 * Data Transfer Object for creating an equal split expense.
 *
 * NOTE: The amount is received in cents/paise (long) to prevent floating-point
 * arithmetic issues during transfer and initial processing.
 */
@Value
public class AddEqualExpenseRequest {
    String description;
    long amountPaise;
    UUID paidBy;
    List<UUID> participantUserIds;
    OffsetDateTime occurredAt;
}
