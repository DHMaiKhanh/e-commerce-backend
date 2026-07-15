package com.yourdomain.ecommerce.event;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class UserRegisteredEvent {
    private final Long userId;
    private final String username;
    private final String email;
    @Builder.Default
    private final Instant occurredAt = Instant.now();
}
