package com.settler.infra.cache;

public final class CacheKeyUtil {

    private CacheKeyUtil() {}

    public static String userKey(String userId) {
        return "user:" + userId;
    }

    public static String groupKey(String groupId) {
        return "group:" + groupId;
    }

    public static String expenseKey(String expenseId) {
        return "expense:" + expenseId;
    }

    public static String groupBalanceKey(String groupId) {
        return "groupBalance:" + groupId;
    }
}
