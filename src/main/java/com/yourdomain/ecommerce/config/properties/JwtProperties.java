package com.yourdomain.ecommerce.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {
    private String secret;
    private long accessTokenExpirationMs;
    private long refreshTokenExpirationMs;
    private String issuer;
}
