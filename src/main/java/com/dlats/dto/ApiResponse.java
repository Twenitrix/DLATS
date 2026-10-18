package com.dlats.dto;

import java.time.Instant;

public record ApiResponse<T>(
    boolean success,
    T data,
    String message,
    String timestamp
) {
    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now().toString());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, Instant.now().toString());
    }
}
