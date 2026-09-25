package com.sr.smart_civic_platform.common.exception;

import org.springframework.http.HttpStatus;

/*
 * Purpose:
 * Business rule violation হলে এই exception throw হবে।
 * (Example: একই complaint দুইবার submit করার চেষ্টা, invalid status transition)
 *
 * Why:
 * "not found" আর "business rule broke" — দুইটা আলাদা meaning।
 * তাই আলাদা exception রাখলে HTTP status ঠিকভাবে map করা যায়
 * (এইটা সাধারণত 400/409 হবে, "not found" এর মতো 404 না)।
 *
 * Design decision:
 * HttpStatus field রাখা হয়েছে, যাতে প্রতিটা business error এর
 * নিজস্ব status code দেওয়া যায় (সব সময় 400 না)।
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST; // default
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}