package com.sr.smart_civic_platform.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/*
 * Purpose:
 * সব API response কে একই structure এ wrap করা।
 *
 * Why:
 * Frontend developer কে guess করতে হবে না response shape কেমন হবে।
 * success/error দুই ক্ষেত্রেই একই top-level fields থাকবে।
 *
 * Future:
 * Pagination হলে "meta" field যোগ করা হবে।
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private Instant timestamp;

    private ApiResponse(boolean success, String message, T data, List<String> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = Instant.now();
    }

    // ---- Success factory methods ----

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    // ---- Error factory methods ----

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }

    // ---- Getters (Jackson serialization এর জন্য প্রয়োজন) ----

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}