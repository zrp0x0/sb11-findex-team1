package com.codeit.findex.global.error;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp;

    private final int status;;

    private final String message;

    private final String details;

    @Builder
    public ErrorResponse(int status, String message, String details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.details = details;
    }
}
