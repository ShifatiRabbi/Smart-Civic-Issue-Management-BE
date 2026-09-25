package com.sr.smart_civic_platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // TODO (PHASE-2 শেষে): নিচের লাইন remove করে
                        // .requestMatchers("/api/v1/**").authenticated() বসাতে হবে
                        .anyRequest().permitAll()
                );

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