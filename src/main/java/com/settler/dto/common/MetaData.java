package com.settler.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * For pagination or diagnostics (execution time, total items, etc.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaData {
    private Integer page;
    private Integer size;
    private Long total;
    private Long executionTimeMs;
}
