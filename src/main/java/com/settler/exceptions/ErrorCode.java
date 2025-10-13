package com.settler.exceptions;

public enum ErrorCode {

    SUCCESS("00", "Success"),
    VALIDATION_FAILED("01", "Validation failed"),
    UNAUTHORIZED("02", "Unauthorized access"),
    NOT_FOUND("03", "Resource not found"),
    CONFLICT("04", "Conflict - Duplicate or Invalid state"),
    GROUP_NOT_FOUND("05", "Group not found"),
    INTERNAL_ERROR("99", "Internal server error"), 
    SETTLEMENT_NOT_FOUND("13", "Settlement not found"),
    USER_NOT_FOUND("10", "User not found"),
    ALREADY_EXISTS("11", "Already Exists");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
