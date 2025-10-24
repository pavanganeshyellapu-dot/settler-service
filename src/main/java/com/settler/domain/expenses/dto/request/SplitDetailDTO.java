package com.settler.domain.expenses.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitDetailDTO {
    private Long userId;
    private Double amount;      // optional for equal split
    private Double percentage;  // for percentage split
}

