package com.sr.smart_civic_platform.auth.controller;

import com.sr.smart_civic_platform.auth.dto.request.LoginRequest;
import com.sr.smart_civic_platform.auth.dto.request.RefreshTokenRequest;
import com.sr.smart_civic_platform.auth.dto.request.RegisterRequest;
import com.sr.smart_civic_platform.auth.dto.response.LoginResponse;
import com.sr.smart_civic_platform.auth.dto.response.RegisterResponse;
import com.sr.smart_civic_platform.auth.dto.response.TokenResponse;
import com.sr.smart_civic_platform.auth.service.AuthService;
import com.sr.smart_civic_platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/*
 * Purpose:
 * Authentication এর সব endpoint এর entry point।
 *
 * Not:
 * Business logic এখানে লেখা হয়নি — শুধু request receive করে
 * AuthService কে delegate করা হচ্ছে (thin controller pattern)।
 *
 * Validation:
 * @Valid দিয়ে RegisterRequest এর annotation (NotBlank, Email, Size)
 * trigger হবে — fail করলে GlobalExceptionHandler স্বয়ংক্রিয়ভাবে
 * MethodArgumentNotValidException catch করে ঠিক response পাঠাবে।
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity
                .ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        TokenResponse response = authService.refresh(request);

        return ResponseEntity
                .ok(ApiResponse.success("Token refreshed successfully", response));
    }
}