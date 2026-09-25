package com.sr.smart_civic_platform.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * Purpose:
 * Register API এর incoming request body।
 *
 * Why no "role" field:
 * Client কে role select করতে দিলে কেউ নিজেকে ADMIN বানিয়ে ফেলতে পারবে।
 * তাই role field এখানে ইচ্ছাকৃতভাবে বাদ দেওয়া হয়েছে —
 * Service layer এ hardcoded CITIZEN বসানো হবে।
 *
 * Validation:
 * fullName, email, password required।
 * phone optional (validation নেই)।
 */
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    private String phone; // optional

    public RegisterRequest() {
    }

    // ---- Getters & Setters ----

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}