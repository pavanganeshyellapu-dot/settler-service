package com.settler.domain.expenses.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderRequestDTO {
    private Long groupId;
    private Long fromUserId;
    private Long toUserId;
    private Double amount;
}

