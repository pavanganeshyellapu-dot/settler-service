package com.settler.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import org.flywaydb.core.experimental.MetaData;

/**
 * Contains data payload and additional details like status and errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBodyWrapper<T> {
    private String statusCode;
    private String statusMessage;
    private T data;
    private List<ErrorDetail> errors;
    private MetaData meta;
}
