package com.yourdomain.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresInSeconds;
}
