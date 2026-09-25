package com.sr.smart_civic_platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Purpose:
 * Swagger UI তে API document/test করার জন্য OpenAPI bean configure করা।
 *
 * Why:
 * Frontend developer বা QA কে Postman collection শেয়ার করতে হবে না —
 * browser এ /swagger-ui.html গেলেই সব API দেখা ও test করা যাবে।
 *
 * Future:
 * JWT বসানোর পর এখানে "Authorize" button দিয়ে Bearer token
 * দিয়ে protected API test করা যাবে (SecurityScheme already added below).
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI smartCivicOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Smart Civic Platform API")
                        .description("Smart Complaint & Civic Issue Management Platform - Backend API")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Shifati Rabbi")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}