package com.settler.domain.expenses.controller;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Request model for creating a new expense.
 * Supports split types: EQUAL, PERCENTAGE, SHARE
 */
@Data
public class ExpenseCreateRequest {

    private UUID groupId;
    private UUID payerId;
    private String description;
    private Double amount;
    private String splitType;  // EQUAL / PERCENTAGE / SHARE
    private List<UUID> participantIds;

    // Optional: used only if splitType == PERCENTAGE or SHARE
    private Map<UUID, BigDecimal> percentageMap;  // for PERCENTAGE splits
    private Map<UUID, Integer> shareMap;          // for SHARE splits
}
