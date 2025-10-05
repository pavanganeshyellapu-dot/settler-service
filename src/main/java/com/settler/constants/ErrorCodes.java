package com.settler.constants;

/**
 * Application-wide standard error codes and messages.
 */
public final class ErrorCodes {

    private ErrorCodes() {}

    // === VALIDATION ERRORS ===
    public static final String VALIDATION_ERROR_CODE = "VAL_001";
    public static final String VALIDATION_ERROR_MSG = "Validation failed";

    // === AUTHENTICATION / SECURITY ===
    public static final String AUTH_ERROR_CODE = "AUTH_001";
    public static final String AUTH_ERROR_MSG = "Authentication failed";

    public static final String TOKEN_EXPIRED_CODE = "AUTH_002";
    public static final String TOKEN_EXPIRED_MSG = "JWT token expired";

    // === BUSINESS ERRORS ===
    public static final String DUPLICATE_USER_CODE = "BUS_001";
    public static final String DUPLICATE_USER_MSG = "User already exists";

    public static final String INVALID_GROUP_CODE = "BUS_002";
    public static final String INVALID_GROUP_MSG = "Group not found or invalid";

    public static final String EXPENSE_NOT_FOUND_CODE = "BUS_003";
    public static final String EXPENSE_NOT_FOUND_MSG = "Expense not found";

    // === INTERNAL ===
    public static final String INTERNAL_ERROR_CODE = "SYS_001";
    public static final String INTERNAL_ERROR_MSG = "Internal server error";
}
