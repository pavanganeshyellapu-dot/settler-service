package com.settler.exceptions;

import lombok.Getter;

/**
 * Represents controlled, expected business-level exceptions.
 * These are not programming bugs, but rule violations or known issues.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String detailMessage;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = errorCode.getMessage();
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    /** 
     * Returns the short code (e.g. "11") from ErrorCode.
     */
    public String getCode() {
        return errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return detailMessage;
    }
}
