package com.sr.smart_civic_platform.auth.dto.response;

/*
 * Purpose:
 * /refresh endpoint এর success response — শুধু নতুন token pair।
 *
 * Why not reuse LoginResponse:
 * LoginResponse এ "user" field mandatory অনুভূত হয় (login এ user info
 * দরকার), কিন্তু refresh এ পুনরায় user object পাঠানোর দরকার নেই —
 * frontend এর কাছে already আছে login থেকে। আলাদা DTO রাখলে
 * প্রতিটা endpoint এর response তার নিজের প্রয়োজন অনুযায়ী থাকে।
 */
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

    public TokenResponse(String accessToken, String refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
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
}