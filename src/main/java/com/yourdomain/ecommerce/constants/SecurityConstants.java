package com.yourdomain.ecommerce.constants;

public final class SecurityConstants {

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_USER_ID = "uid";

    public static final String[] PUBLIC_ENDPOINTS = {
            "/v1/auth/**",
            "/v1/products/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/health",
            "/actuator/info",
            "/actuator/prometheus"
    };

    private SecurityConstants() {
    }
}
