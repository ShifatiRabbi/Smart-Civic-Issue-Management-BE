package com.sr.smart_civic_platform.auth.dto.response;

import com.sr.smart_civic_platform.user.entity.UserRole;

/*
 * Purpose:
 * Register success হলে client কে যা ফেরত দেওয়া হবে।
 *
 * Why separate from User entity:
 * password field কখনো response এ যাওয়া উচিত না।
 * এই DTO তে password field-ই নেই, তাই leak হওয়ার সুযোগ নেই।
 */
public class RegisterResponse {

    private String id;
    private String fullName;
    private String email;
    private UserRole role;

    public RegisterResponse(String id, String fullName, String email, UserRole role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // ---- Getters ----

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }
}