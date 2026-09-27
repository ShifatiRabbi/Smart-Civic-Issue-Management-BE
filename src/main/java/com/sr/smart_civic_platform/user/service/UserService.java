package com.sr.smart_civic_platform.user.service;

import com.sr.smart_civic_platform.user.dto.request.CreateUserRequest;
import com.sr.smart_civic_platform.user.dto.request.UpdateProfileRequest;
import com.sr.smart_civic_platform.user.dto.request.UpdateUserStatusRequest;
import com.sr.smart_civic_platform.user.dto.response.PagedResponse;
import com.sr.smart_civic_platform.user.dto.response.UserResponse;
import com.sr.smart_civic_platform.user.entity.UserRole;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse getMyProfile(String userId);

    UserResponse updateMyProfile(String userId, UpdateProfileRequest request);

    UserResponse createUser(CreateUserRequest request);

    PagedResponse<UserResponse> listUsers(UserRole roleFilter, Pageable pageable);

    UserResponse updateUserStatus(String currentAdminId, String targetUserId, UpdateUserStatusRequest request);

}