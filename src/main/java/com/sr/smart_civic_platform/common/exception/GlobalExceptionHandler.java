package com.sr.smart_civic_platform.common.exception;

import com.sr.smart_civic_platform.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Purpose:
 * প্রতিটা controller এ আলাদা try-catch না লিখে, এখান থেকে
 * সব exception centralized ভাবে handle করা হবে।
 *
 * Why:
 * - Consistent error response shape (ApiResponse) সবসময় নিশ্চিত করা।
 * - প্রতিটা exception type কে সঠিক HTTP status এ map করা।
 *
 * Order matters:
 * Specific exception গুলো আগে, সবচেয়ে generic Exception সবার শেষে।
 * নাহলে generic handler আগে ধরে ফেলবে specific case গুলো।
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- 404: Resource not found ----
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ---- Business rule violation (status configurable per exception) ----
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.error(ex.getMessage()));
    }

    /*
     * Validation:
     * @Valid দিয়ে annotated DTO তে validation fail হলে Spring
     * এই exception throw করে। প্রতিটা field error কে readable
     * string এ convert করে errors list এ পাঠানো হচ্ছে।
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errors));
    }

    /*
     * Security:
     * এইটা সবচেয়ে শেষ fallback — কোনো uncaught exception যেন
     * কখনো raw stack trace বা internal detail client কে না দেখায়।
     * শুধু generic message + 500 status যাবে।
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong. Please try again later."));
    }
}