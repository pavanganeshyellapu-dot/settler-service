package com.settler.exceptions;

import java.util.List;
import com.settler.dto.common.ErrorDetail;

public class ValidationException extends RuntimeException {

    private final List<ErrorDetail> details;

    public ValidationException(List<ErrorDetail> details) {
        super("Validation failed");
        this.details = details;
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }
}
