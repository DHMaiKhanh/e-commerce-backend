package com.yourdomain.ecommerce.dto.response;

import com.yourdomain.ecommerce.enums.Role;
import com.yourdomain.ecommerce.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserStatus status;
    private Set<Role> roles;
    private Instant createdAt;
    private Instant updatedAt;
}
