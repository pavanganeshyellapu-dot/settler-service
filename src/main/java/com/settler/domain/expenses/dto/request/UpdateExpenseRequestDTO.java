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
public class UpdateExpenseRequestDTO {
    private String description;
    private Double amount;
    private String category;
    private String splitType;
    private List<SplitDetailDTO> splits;
}

