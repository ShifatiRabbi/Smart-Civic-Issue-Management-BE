package com.sr.smart_civic_platform.auth.service.impl;

import com.sr.smart_civic_platform.auth.dto.request.LoginRequest;
import com.sr.smart_civic_platform.auth.dto.request.RefreshTokenRequest;
import com.sr.smart_civic_platform.auth.dto.request.RegisterRequest;
import com.sr.smart_civic_platform.auth.dto.response.LoginResponse;
import com.sr.smart_civic_platform.auth.dto.response.RegisterResponse;
import com.sr.smart_civic_platform.auth.dto.response.TokenResponse;
import com.sr.smart_civic_platform.auth.dto.response.UserSummaryResponse;
import com.sr.smart_civic_platform.auth.service.AuthService;
import com.sr.smart_civic_platform.common.exception.BusinessException;
import com.sr.smart_civic_platform.security.jwt.JwtUtil;
import com.sr.smart_civic_platform.user.entity.User;
import com.sr.smart_civic_platform.user.entity.UserRole;
import com.sr.smart_civic_platform.user.entity.UserStatus;
import com.sr.smart_civic_platform.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/*
 * Purpose:
 * Register business logic এর actual implementation।
 *
 * Flow:
 * 1. Email duplicate কিনা check করা।
 * 2. Password hash করা।
 * 3. Role = CITIZEN, Status = ACTIVE hardcode করে User তৈরি করা।
 * 4. Save করে DTO তে map করে return করা।
 *
 * Security:
 * Client কখনো role/status নিজে সেট করতে পারবে না — এখানে hardcoded।
 */

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        // Step 1: Duplicate email check
        // Why check before insert: MongoDB unique index would also catch this,
        // but a raw duplicate-key exception is not a clean client-facing error.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered", HttpStatus.CONFLICT);
        }

        // Step 2: Hash password (never store plain text)
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Step 3: Build entity with server-controlled role & status
        User user = new User(
                request.getFullName(),
                request.getEmail(),
                hashedPassword,
                UserRole.CITIZEN,       // hardcoded - client cannot choose this
                UserStatus.ACTIVE,
                request.getPhone()
        );

        User savedUser = userRepository.save(user);

        // Step 4: Map to response DTO (no password field exposed)
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }


    /*
     * Purpose:
     * Login: email+password verify করে JWT token pair ইস্যু করা।
     *
     * Security:
     * - "user not found" আর "wrong password" এর জন্য একই generic exception
     *   ব্যবহার করা হচ্ছে (email enumeration prevent করতে)।
     * - Suspended account হলে token issue হবে না।
     */
    @Override
    public LoginResponse login(LoginRequest request) {

        // Step 1: Find user by email (generic error if not found)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        // Step 2: Verify password using bcrypt matches() (never raw compare)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        // Step 3: Block suspended accounts even with correct password
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new BusinessException("Account is suspended", HttpStatus.FORBIDDEN);
        }

        // Step 4: Issue tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtUtil.getAccessExpirationMs(),
                UserSummaryResponse.fromEntity(user)
        );
    }


    /*
     * Purpose:
     * Expired access token কে password ছাড়াই renew করা।
     *
     * Flow:
     * 1. Refresh token parse+validate করা (signature, expiry, type=refresh)।
     * 2. Token এর ভিতরের user id দিয়ে বর্তমান database state আনা
     *    (token এর ভিতরের পুরনো role/status না, current data)।
     * 3. User এখনো ACTIVE কিনা check করা।
     * 4. নতুন access + refresh token pair ইস্যু করা (rotation)।
     *
     * Security:
     * JwtException (malformed/expired/tampered/wrong-type) কে
     * generic 401 BusinessException এ map করা হচ্ছে — raw JWT
     * library error client কে দেখানো হচ্ছে না।
     */
    @Override
    public TokenResponse refresh(RefreshTokenRequest request) {

        String userId;
        try {
            userId = jwtUtil.validateAndExtractUserIdFromRefreshToken(request.getRefreshToken());
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new BusinessException("Account is suspended", HttpStatus.FORBIDDEN);
        }

        // Token rotation: issue a brand new pair, old refresh token is not
        // explicitly blacklisted (no token store yet), but should no longer
        // be relied upon by well-behaved clients since a newer one now exists.
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        return new TokenResponse(newAccessToken, newRefreshToken, jwtUtil.getAccessExpirationMs());
    }
}