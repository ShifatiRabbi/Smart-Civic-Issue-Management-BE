package com.sr.smart_civic_platform.user.dto.response;

import com.sr.smart_civic_platform.user.entity.User;
import com.sr.smart_civic_platform.user.entity.UserRole;
import com.sr.smart_civic_platform.user.entity.UserStatus;

import java.time.Instant;

/*
 * Purpose:
 * User entity কে client-facing response এ map করা — password field
 * বাদ দিয়ে, এবং future এ entity তে নতুন internal field যোগ হলেও
 * সেইটা accidentally leak না হওয়া নিশ্চিত করার জন্য explicit mapping।
 */
public class UserResponse {

    private String id;
    private String fullName;
    private String email;
    private UserRole role;
    private UserStatus status;
    private String phone;
    private Instant createdAt;

    public UserResponse(String id, String fullName, String email, UserRole role,
                         UserStatus status, String phone, Instant createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getPhone(),
                user.getCreatedAt()
        );
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public String getPhone() { return phone; }
    public Instant getCreatedAt() { return createdAt; }
}