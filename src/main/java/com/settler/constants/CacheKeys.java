package com.settler.constants;

/**
 * Central definition of all cache key prefixes used across the system.
 */
public final class CacheKeys {

    private CacheKeys() {}

    public static final String USER_PREFIX = "user:";
    public static final String GROUP_PREFIX = "group:";
    public static final String EXPENSE_PREFIX = "expense:";
    public static final String GROUP_BALANCE_PREFIX = "groupBalance:";

    public static String userKey(String userId) {
        return USER_PREFIX + userId;
    }

    public static String groupKey(String groupId) {
        return GROUP_PREFIX + groupId;
    }

    public static String expenseKey(String expenseId) {
        return EXPENSE_PREFIX + expenseId;
    }

    public static String groupBalanceKey(String groupId) {
        return GROUP_BALANCE_PREFIX + groupId;
    }
}
