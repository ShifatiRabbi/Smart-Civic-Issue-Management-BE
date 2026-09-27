package com.sr.smart_civic_platform.user.controller;

import com.sr.smart_civic_platform.common.response.ApiResponse;
import com.sr.smart_civic_platform.user.dto.request.CreateUserRequest;
import com.sr.smart_civic_platform.user.dto.request.UpdateProfileRequest;
import com.sr.smart_civic_platform.user.dto.request.UpdateUserStatusRequest;
import com.sr.smart_civic_platform.user.dto.response.PagedResponse;
import com.sr.smart_civic_platform.user.dto.response.UserResponse;
import com.sr.smart_civic_platform.user.entity.UserRole;
import com.sr.smart_civic_platform.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/*
 * Purpose:
 * Self-service profile endpoints - যেকোনো logged-in user নিজের জন্য।
 *
 * How current user is identified:
 * JwtAuthenticationFilter (STEP-02e) token এর "sub" claim (user id) কে
 * Authentication.getPrincipal() হিসেবে SecurityContext এ বসিয়েছিল।
 * তাই এখানে Authentication injection করে principal থেকে userId পাচ্ছি -
 * client কে own user id পাঠাতে হচ্ছে না, token থেকেই determine হচ্ছে
 * (এইটা নিশ্চিত করে যে কেউ অন্যের id দিয়ে নিজের profile চাইতে পারবে না)।
 */

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ---- Self-service (any authenticated user) ----

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return ApiResponse.success("Profile fetched successfully", userService.getMyProfile(userId));
    }

    @PatchMapping("/me")
    public ApiResponse<UserResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        String userId = (String) authentication.getPrincipal();
        return ApiResponse.success("Profile updated successfully",
                userService.updateMyProfile(userId, request));
    }

    // ---- Admin-only ----

    /*
     * @PreAuthorize checks the ROLE_ADMIN authority set by
     * JwtAuthenticationFilter in the SecurityContext, BEFORE this
     * method body ever runs.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public org.springframework.http.ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<PagedResponse<UserResponse>> listUsers(
            @RequestParam(required = false) UserRole role,
            Pageable pageable) {

        return ApiResponse.success("Users fetched successfully",
                userService.listUsers(role, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ApiResponse<UserResponse> updateUserStatus(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        String currentAdminId = (String) authentication.getPrincipal();
        return ApiResponse.success("User status updated successfully",
                userService.updateUserStatus(currentAdminId, id, request));
    }
}