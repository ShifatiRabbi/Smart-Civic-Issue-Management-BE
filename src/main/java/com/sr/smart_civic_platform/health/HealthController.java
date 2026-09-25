package com.sr.smart_civic_platform.health;

import com.sr.smart_civic_platform.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/*
 * Purpose:
 * Server up আছে কিনা এই API দিয়ে check করা যাবে।
 *
 * Why:
 * Deployment/monitoring tool (future এ Kubernetes liveness probe)
 * এই endpoint hit করে বুঝবে server alive আছে কিনা।
 *
 * Security:
 * Public — authentication লাগবে না।
 * (SecurityConfig এ পরের step এ এই path whitelist করা হবে)
 */
@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public ApiResponse<Map<String, Object>> checkHealth() {
        return ApiResponse.success(
                "Service is up",
                Map.of(
                        "status", "UP",
                        "timestamp", Instant.now().toString()
                )
        );
    }
}