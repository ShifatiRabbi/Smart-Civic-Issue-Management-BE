package com.sr.smart_civic_platform.auth.dto.response;

import com.sr.smart_civic_platform.user.entity.User;
import com.sr.smart_civic_platform.user.entity.UserRole;

/*
 * Purpose:
 * Login response এর ভিতরে "কে login করলো" সংক্ষিপ্ত তথ্য।
 *
 * Why separate from RegisterResponse (even though structure is similar):
 * এখন duplicate মনে হলেও, ভবিষ্যতে দুইটা ভিন্ন দিকে বাড়তে পারে
 * (যেমন LoginResponse এ পরে "lastLoginAt" যোগ হতে পারে যেটা
 * RegisterResponse এ দরকার নেই)। এখনই merge করলে পরে coupling সমস্যা হবে।
 */
public class UserSummaryResponse {

    private String id;
    private String fullName;
    private String email;
    private UserRole role;

    public UserSummaryResponse(String id, String fullName, String email, UserRole role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public static UserSummaryResponse fromEntity(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }

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