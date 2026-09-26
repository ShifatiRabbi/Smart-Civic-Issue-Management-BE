package com.sr.smart_civic_platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.sr.smart_civic_platform.security.jwt.JwtAuthenticationFilter;

/*
 * Purpose:
 * Security filter chain configure করা।
 *
 * Change in this step:
 * - JwtAuthenticationFilter কে UsernamePasswordAuthenticationFilter এর
 *   আগে বসানো হয়েছে (addFilterBefore) — যাতে Spring এর নিজস্ব auth
 *   filter এর আগে আমাদের JWT check হয়ে যায়।
 * - "/api/v1/**" এখন সত্যিকারের authenticated() (আগে ছিল permitAll,
 *   TEMPORARY কমেন্ট দিয়ে মার্ক করা ছিল)।
 * - exceptionHandling এ CustomAuthenticationEntryPoint যোগ হয়েছে,
 *   যাতে unauthenticated request এ clean JSON error যায়।
 */
/*
 * Purpose:
 * Spring Security এর base filter chain configure করা।
 *
 * Why:
 * Spring Security dependency থাকলে by default সব endpoint লক থাকে।
 * এখানে explicitly বলে দিচ্ছি কোনটা public, কোনটা future এ protected হবে।
 *
 * Current State (TEMPORARY):
 * /api/v1/** আপাতত permitAll() — কারণ Auth/JWT module (PHASE-2) এখনো নেই।
 *
 * ⚠️ IMPORTANT - Before PHASE-3 শুরু হওয়ার আগে:
 * এইটা change করে "/api/v1/auth/**" বাদে বাকি সব .authenticated() করতে হবে,
 * আর JWT filter chain এ addFilterBefore() দিয়ে বসাতে হবে।
 *
 * Session Policy:
 * STATELESS রাখা হয়েছে already — কারণ আমরা JWT ব্যবহার করবো,
 * session-based login না। এইটা future এর জন্য প্রস্তুত রাখা।
 *
 * CSRF:
 * Disabled — কারণ stateless REST API তে CSRF token দরকার নেই
 * (CSRF মূলত browser session/cookie based attack এর জন্য প্রাসঙ্গিক)।
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                           CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/health",
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * Purpose:
     * Password hash করার জন্য encoder bean — PHASE-2 (Auth/Register)
     * এ user password save করার সময় এইটা ব্যবহার হবে।
     *
     * এখনই বসিয়ে রাখছি কারণ পরে Auth module এ শুধু @Autowired করে
     * ব্যবহার করলেই চলবে, নতুন bean বানাতে হবে না।
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}