package com.yourdomain.ecommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordRequest {

    @NotBlank
    @Email
    private String email;
}
