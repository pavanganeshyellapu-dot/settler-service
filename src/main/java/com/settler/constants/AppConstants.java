package com.settler.constants;

/**
 * Global constants used across the Settler application.
 */
public final class AppConstants {

    private AppConstants() {}

    // === TIME ZONE / DATE ===
    public static final String DEFAULT_TIME_ZONE = "Asia/Kolkata";
    public static final String UTC_ZONE = "UTC";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    // === RESPONSE CODES ===
    public static final String SUCCESS_CODE = "00";
    public static final String ERROR_CODE = "99";

    // === HEADERS ===
    public static final String HEADER_E2E_ID = "X-E2E-ID";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    // === COMMON FLAGS ===
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

    // === CURRENCIES ===
    public static final String DEFAULT_CURRENCY = "INR";

    // === CACHE DEFAULTS ===
    public static final long DEFAULT_CACHE_TTL_MS = 300000; // 5 minutes

    // === PAGINATION ===
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}
