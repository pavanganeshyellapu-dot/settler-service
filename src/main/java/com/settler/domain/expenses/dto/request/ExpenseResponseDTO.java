package com.settler.domain.expenses.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponseDTO {
    private Long id;
    private Long groupId;
    private String description;
    private String category;
    private Double amount;
    private String splitType;
    private String createdBy;
    private LocalDateTime createdAt;
    private List<SplitDetailDTO> splits;
}

