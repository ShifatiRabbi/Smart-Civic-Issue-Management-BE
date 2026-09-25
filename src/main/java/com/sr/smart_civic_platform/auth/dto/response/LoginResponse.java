package com.sr.smart_civic_platform.auth.dto.response;

/*
 * Purpose:
 * Login success হলে client কে যা ফেরত দেওয়া হবে —
 * দুইটা token + token metadata + user summary।
 *
 * Why "expiresIn" included:
 * Frontend কে বলে দিচ্ছি access token কতক্ষণ পর expire হবে (ms এ),
 * যাতে frontend নিজে proactively refresh call করতে পারে
 * (token expire হওয়ার একটু আগেই, 401 পাওয়ার অপেক্ষা না করে)।
 */
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserSummaryResponse user;

    public LoginResponse(String accessToken, String refreshToken, long expiresIn, UserSummaryResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UserSummaryResponse getUser() {
        return user;
    }
}