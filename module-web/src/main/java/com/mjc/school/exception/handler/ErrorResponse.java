package com.mjc.school.exception.handler;

public class ErrorResponse {
    private final String code;
    private final String message;
    private String errorDetails;

    public ErrorResponse(final String code, final String message, String errorDetails) {
        this.code = code;
        this.message = message;
        this.errorDetails = errorDetails;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorDetails() {
        return errorDetails;
    }
}
