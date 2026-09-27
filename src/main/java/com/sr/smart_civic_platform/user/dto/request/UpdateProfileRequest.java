package com.sr.smart_civic_platform.user.dto.request;

import jakarta.validation.constraints.Size;

/*
 * Purpose:
 * Self profile update এর request body।
 *
 * Why no email/role/status field:
 * - email change identity/ownership জটিলতা তৈরি করে (verification flow দরকার,
 *   এখনো scope এ নেই)।
 * - role/status client নিজে বদলাতে পারলে security hole তৈরি হবে
 *   (নিজেকে ADMIN বানানো, নিজেকে re-activate করা ইত্যাদি)।
 * তাই এই DTO তে ইচ্ছাকৃতভাবে শুধু harmless field গুলো রাখা হয়েছে।
 */
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    private String phone;

    public UpdateProfileRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}