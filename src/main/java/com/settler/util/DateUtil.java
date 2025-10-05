package com.settler.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DateUtil {
    private DateUtil() {}

    public static OffsetDateTime nowUtc() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
