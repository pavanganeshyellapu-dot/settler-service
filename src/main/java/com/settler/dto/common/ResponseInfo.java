package com.settler.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

/**
 * Contains metadata about API call — timestamp, response code, and message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInfo {
    private OffsetDateTime timestamp;
    private String responseCode;
    private String responseMessage;
}
