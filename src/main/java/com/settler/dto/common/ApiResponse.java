package com.settler.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Root API Response object wrapping metadata and actual body.
 * This provides uniform structure for all API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private ResponseInfo responseInfo;
    private ResponseBodyWrapper<T> body;

    // =====================================================
    // ✅ Core builders (used by helper methods below)
    // =====================================================
    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode(code)
                        .responseMessage(message)
                        .build())
                .body(ResponseBodyWrapper.<T>builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(data)
                        .build())
                .build();
    }

    public static <T> ApiResponse<T> failure(String code, String message, T errors) {
        return ApiResponse.<T>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode(code)
                        .responseMessage("Failure")
                        .build())
                .body(ResponseBodyWrapper.<T>builder()
                        .statusCode(code)
                        .statusMessage(message)
                        .data(errors)
                        .build())
                .build();
    }

    // =====================================================
    // ✅ Convenience overloads for controllers
    // =====================================================

    /** Simple success — assumes default code/message */
    public static <T> ApiResponse<T> success(T data) {
        return success("00", "Success", data);
    }

    /** Simple failure with just message + int code */
    public static <T> ApiResponse<T> failure(String message, int code) {
        return failure(String.valueOf(code), message, null);
    }

    /** Simple failure with message only — defaults to 400 */
    public static <T> ApiResponse<T> failure(String message) {
        return failure("400", message, null);
    }
}
