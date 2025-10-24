package com.settler.domain.expenses.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddExpenseRequestDTO {
    private Long groupId;
    private String description;
    private Double amount;
    private String category; // FOOD, RENT, GAMES, etc.
    private String splitType; // EQUAL, PERCENTAGE, SHARES
    private List<SplitDetailDTO> splits;
}

