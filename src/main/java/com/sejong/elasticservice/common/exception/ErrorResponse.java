package com.sejong.elasticservice.common.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int status,
        String error,
        String timestamp
) {
    public static ErrorResponse of(String message, int status, String error) {
        return new ErrorResponse(
                message,
                status,
                error,
                LocalDateTime.now().toString()
        );
    }

    public static ErrorResponse badRequest(String message) {
        return of(message, 400, "Bad request");
    }
}
