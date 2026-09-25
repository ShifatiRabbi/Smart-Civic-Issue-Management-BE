package com.sr.smart_civic_platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/*
 * Purpose:
 * React frontend (আলাদা origin/port) থেকে API call allow করা।
 *
 * Why:
 * Browser default এ cross-origin request block করে।
 *
 * Change from previous version:
 * Frontend URL এখন hardcode না করে .env এর FRONTEND_URL থেকে পড়া হচ্ছে,
 * যাতে production deploy এর সময় শুধু .env change করলেই হয়, code না।
 *
 * Security:
 * "*" ব্যবহার না করে specific origin allow করা হয়েছে (credentials
 * allow করা থাকলে "*" ব্যবহার করা যায়ও না browser spec অনুযায়ী)।
 */
@Configuration
public class CorsConfig {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}