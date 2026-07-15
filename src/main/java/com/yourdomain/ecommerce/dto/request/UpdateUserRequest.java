package com.yourdomain.ecommerce.dto.request;

import com.yourdomain.ecommerce.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {

    @Email
    @Size(max = 128)
    private String email;

    @Size(max = 128)
    private String fullName;

    private UserStatus status;
}
