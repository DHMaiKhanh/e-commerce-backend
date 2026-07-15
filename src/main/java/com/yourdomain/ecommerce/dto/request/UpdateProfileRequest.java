package com.yourdomain.ecommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileRequest {

    @Email
    @Size(max = 128)
    private String email;

    @Size(max = 128)
    private String fullName;
}
