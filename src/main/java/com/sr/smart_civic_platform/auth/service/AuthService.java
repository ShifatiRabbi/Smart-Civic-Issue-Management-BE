package com.sr.smart_civic_platform.auth.service;

import com.sr.smart_civic_platform.auth.dto.request.LoginRequest;
import com.sr.smart_civic_platform.auth.dto.request.RefreshTokenRequest;
import com.sr.smart_civic_platform.auth.dto.request.RegisterRequest;
import com.sr.smart_civic_platform.auth.dto.response.LoginResponse;
import com.sr.smart_civic_platform.auth.dto.response.RegisterResponse;
import com.sr.smart_civic_platform.auth.dto.response.TokenResponse;

/*
 * Purpose:
 * Authentication সম্পর্কিত সব business logic এর contract।
 *
 * Why interface + impl (না শুধু class):
 * - Testability: future এ unit test করার সময় mock করা সহজ হবে।
 * - PHASE-2 এ login/refresh/logout method গুলোও এখানে যোগ হবে ধীরে ধীরে।
 */
public interface AuthService {

    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    TokenResponse refresh(RefreshTokenRequest request);

}