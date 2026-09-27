package com.sr.smart_civic_platform.user.service.impl;

import com.sr.smart_civic_platform.common.exception.BusinessException;
import com.sr.smart_civic_platform.common.exception.ResourceNotFoundException;
import com.sr.smart_civic_platform.user.dto.request.CreateUserRequest;
import com.sr.smart_civic_platform.user.dto.request.UpdateProfileRequest;
import com.sr.smart_civic_platform.user.dto.request.UpdateUserStatusRequest;
import com.sr.smart_civic_platform.user.dto.response.PagedResponse;
import com.sr.smart_civic_platform.user.dto.response.UserResponse;
import com.sr.smart_civic_platform.user.entity.User;
import com.sr.smart_civic_platform.user.entity.UserRole;
import com.sr.smart_civic_platform.user.entity.UserStatus;
import com.sr.smart_civic_platform.user.repository.UserRepository;
import com.sr.smart_civic_platform.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Purpose:
     * SecurityContext থেকে পাওয়া userId (JwtAuthenticationFilter এ set হওয়া)
     * দিয়ে নিজের profile আনা।
     *
     * Why ResourceNotFoundException here is realistically rare:
     * userId টা token থেকে আসছে, token টা valid login এর সময় তৈরি হয়েছিল।
     * তবুও defensive check রাখা হলো - user delete হয়ে যেতে পারে token
     * এখনো expire না হতেই (future admin delete-user feature আসলে)।
     */
    @Override
    public UserResponse getMyProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponse.fromEntity(user);
    }

    /*
     * Purpose:
     * নিজের fullName/phone আপডেট করা - null field গুলো skip করা হয়
     * (partial update - PATCH semantics, PUT না)।
     */
    @Override
    public UserResponse updateMyProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    /*
     * Purpose:
     * Admin creates a STAFF or ADMIN account directly (no self-registration
     * for these roles, per project business rule from PHASE-2 design).
     */
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered", HttpStatus.CONFLICT);
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                hashedPassword,
                request.getRole(),       // admin explicitly chooses STAFF or ADMIN
                UserStatus.ACTIVE,
                request.getPhone()
        );

        return UserResponse.fromEntity(userRepository.save(user));
    }

    /*
     * Purpose:
     * Paginated user list, optionally filtered by role.
     *
     * Why optional filter branches into two repository calls:
     * findAll(Pageable) and findByRole(role, Pageable) have different
     * MongoDB queries under the hood; keeping them separate is clearer
     * than building a dynamic Criteria query for just one optional field.
     */
    @Override
    public PagedResponse<UserResponse> listUsers(UserRole roleFilter, Pageable pageable) {
        Page<User> userPage = (roleFilter != null)
                ? userRepository.findByRole(roleFilter, pageable)
                : userRepository.findAll(pageable);

        Page<UserResponse> responsePage = userPage.map(UserResponse::fromEntity);
        return PagedResponse.fromPage(responsePage);
    }

    /*
     * Purpose:
     * Admin suspends/activates a user account.
     *
     * Security:
     * Self-lockout prevention - an admin cannot change their own status
     * through this endpoint (avoids accidentally locking themselves out
     * with no other admin able to reverse it, in a single-admin scenario).
     */
    @Override
    public UserResponse updateUserStatus(String currentAdminId, String targetUserId, UpdateUserStatusRequest request) {
        if (currentAdminId.equals(targetUserId)) {
            throw new BusinessException("You cannot change your own account status", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setStatus(request.getStatus());
        return UserResponse.fromEntity(userRepository.save(user));
    }
}