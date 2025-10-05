package com.settler.web.base;

import com.settler.dto.common.ApiResponse;

public abstract class BaseController {

    protected <T> ApiResponse<T> ok(T data) {
        return ApiResponse.success(data);
    }

    protected <T> ApiResponse<T> fail(String message) {
        return ApiResponse.failure(message, 400);
    }
}
