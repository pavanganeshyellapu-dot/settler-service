package com.settler.util;

import java.util.UUID;

public final class UUIDUtil {
    private UUIDUtil() {}

    public static String randomId() {
        return UUID.randomUUID().toString();
    }
}
