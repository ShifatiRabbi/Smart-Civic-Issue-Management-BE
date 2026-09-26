package com.sr.smart_civic_platform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sr.smart_civic_platform.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * Purpose:
 * Protected endpoint এ token ছাড়া/invalid token নিয়ে request এলে
 * Spring Security এর default (plain text / HTML) error page না দেখিয়ে
 * আমাদের project-wide ApiResponse JSON format এ error পাঠানো।
 *
 * Why needed:
 * Spring Security exception, GlobalExceptionHandler (Controller layer)
 * পর্যন্ত পৌঁছায় না — কারণ এইটা Controller এর আগেই, filter layer এ ঘটে।
 * তাই আলাদা EntryPoint দিয়ে handle করতে হয়।
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.error("Authentication required or token is invalid/expired");

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}