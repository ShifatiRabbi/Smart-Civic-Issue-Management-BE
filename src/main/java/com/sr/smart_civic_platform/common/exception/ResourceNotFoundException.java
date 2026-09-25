package com.sr.smart_civic_platform.common.exception;

/*
 * Purpose:
 * Requested resource (id দিয়ে খুঁজলে) না পাওয়া গেলে throw হবে।
 *
 * Why:
 * "not found" case এর জন্য generic RuntimeException না লিখে
 * specific exception রাখলে GlobalExceptionHandler এ আলাদাভাবে
 * 404 status map করা যায়।
 *
 * Usage Example:
 * throw new ResourceNotFoundException("Complaint not found with id: " + id);
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}