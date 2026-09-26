package com.sr.smart_civic_platform.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*
 * Purpose:
 * প্রতিটা incoming request এর Authorization header থেকে JWT বের করে
 * verify করা, এবং valid হলে SecurityContext এ authenticated user বসানো।
 *
 * Why OncePerRequestFilter:
 * এক request এ যেন এই filter ঠিক একবারই চলে (forward/include হলেও)।
 *
 * Important design choice:
 * Token invalid/missing হলে এখানে exception throw করছি না বা response
 * লিখছি না — শুধু SecurityContext খালি রেখে চেইন কে এগিয়ে যেতে দিচ্ছি।
 * এর ফলে পরে SecurityConfig এর authorizeHttpRequests rule অনুযায়ী
 * Spring Security নিজেই ঠিক করবে block করবে কিনা (public path হলে
 * pass, protected path হলে CustomAuthenticationEntryPoint ট্রিগার হবে)।
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HEADER_NAME);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length());

        try {
            // Only ACCESS tokens may authenticate a request.
            // A refresh token has the same signature validity but must
            // never be usable to call protected business endpoints.
            String type = jwtUtil.extractType(token);
            if (!JwtUtil.TYPE_ACCESS.equals(type)) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException ex) {
            // Invalid/expired/tampered token: leave context empty,
            // downstream authorization rule will reject if route is protected.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}