package com.sr.smart_civic_platform.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * Purpose:
 * Login API এর incoming request body।
 *
 * Validation:
 * শুধু "খালি না" এবং "valid email format" check করা হচ্ছে।
 * Password এর length এখানে check করার দরকার নেই (register এ হয়ে গেছে) —
 * login এ শুধু blank কিনা যথেষ্ট, exact length matter করে না।
 */
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {
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
}