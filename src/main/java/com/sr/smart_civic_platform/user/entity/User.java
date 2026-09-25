package com.sr.smart_civic_platform.user.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/*
 * Purpose:
 * System এর যেকোনো user (citizen/staff/admin) এর জন্য একটাই collection।
 *
 * Why single collection (না আলাদা Citizen/Staff/Admin):
 * Login logic সবার জন্য একই (email+password) — শুধু role আলাদা।
 * আলাদা collection করলে login query complex হয়ে যেত (কোন collection এ
 * খুঁজবো তা আগে থেকে জানতে হতো)। role field দিয়ে differentiate করাই simpler।
 *
 * Security:
 * password field এখানে থাকলেও, এইটা কখনো সরাসরি response এ যাবে না —
 * DTO layer (RegisterResponse/UserResponse) দিয়ে filter হয়ে যাবে,
 * এইটা পরের sub-step এ দেখানো হবে।
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String fullName;

    @Indexed(unique = true)
    private String email;

    private String password; // bcrypt hashed, never plain text

    private UserRole role;

    private UserStatus status;

    private String phone; // optional

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public User() {
    }

    public User(String fullName, String email, String password, UserRole role, UserStatus status, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.phone = phone;
    }

    // ---- Getters & Setters ----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}