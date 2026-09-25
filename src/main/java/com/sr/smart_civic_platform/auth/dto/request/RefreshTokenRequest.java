package com.sr.smart_civic_platform.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/*
 * Purpose:
 * /refresh endpoint এর incoming request body।
 */
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    public RefreshTokenRequest() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}