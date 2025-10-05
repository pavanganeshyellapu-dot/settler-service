package com.settler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LoggerUtil {

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void info(Logger log, String message) {
        log.info("[traceId={}]: {}", MDC.get("traceId"), message);
    }

    public static void error(Logger log, String message, Throwable e) {
        log.error("[traceId={}]: {}", MDC.get("traceId"), message, e);
    }

    public static void warn(Logger log, String message) {
        log.warn("[traceId={}]: {}", MDC.get("traceId"), message);
    }
}
