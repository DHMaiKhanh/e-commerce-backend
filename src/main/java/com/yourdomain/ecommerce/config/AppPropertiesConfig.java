package com.yourdomain.ecommerce.config;

import com.yourdomain.ecommerce.config.properties.CorsProperties;
import com.yourdomain.ecommerce.config.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class AppPropertiesConfig {
}
