package com.settler.domain.expenses.dto.request;

import lombok.*;
import java.util.List;
import java.util.UUID;

/**
 * Incoming request to create a new expense.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExpenseRequest {

    private UUID groupId;             // Group in which expense is added
    private UUID paidBy;              // User who paid
    private String description;       // Expense name (e.g. "Dinner")
    private Double amount;            // Total amount
    private String splitType;         // EQUAL, PERCENTAGE, EXACT

    private List<SplitDetailRequest> participants; // Participants’ shares
}
