package com.sr.smart_civic_platform.user.dto.request;

import com.sr.smart_civic_platform.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 * Purpose:
 * Admin কর্তৃক নতুন STAFF/ADMIN user তৈরি করার request body।
 *
 * Why "role" is allowed here (unlike public RegisterRequest):
 * এই endpoint নিজেই @PreAuthorize("hasRole('ADMIN')") দিয়ে protected -
 * শুধু authenticated admin এই request পাঠাতে পারবে, তাই client কে
 * role বেছে নিতে দেওয়া এখানে নিরাপদ।
 */
public class CreateUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotNull(message = "Role is required")
    private UserRole role;

    private String phone;

    public CreateUserRequest() {
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}