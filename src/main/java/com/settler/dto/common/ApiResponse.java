package com.settler.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private ResponseInfo responseInfo;
    private ResponseBodyWrapper<T> body;

    // ---------- STATIC FACTORY HELPERS ---------- //

    /** Success response builder */
    public static <T> ApiResponse<T> success(String code, String message, T data) {
        ResponseInfo info = ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .responseCode(code)
                .responseMessage(message)
                .build();

        ResponseBodyWrapper<T> body = ResponseBodyWrapper.<T>builder()
                .statusCode(code)
                .statusMessage(message)
                .data(data)
                .build();

        return ApiResponse.<T>builder()
                .responseInfo(info)
                .body(body)
                .build();
    }

    /** Failure response builder */
    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> failure(String code, String message, Object errors) {
        ResponseInfo info = ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .responseCode(code)
                .responseMessage(message)
                .build();

        ResponseBodyWrapper<T> body = ResponseBodyWrapper.<T>builder()
                .statusCode(code)
                .statusMessage(message)
                .data((T)errors)
                .build();

        return ApiResponse.<T>builder()
                .responseInfo(info)
                .body(body)
                .build();
    }
}
