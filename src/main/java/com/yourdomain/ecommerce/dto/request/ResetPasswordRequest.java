package com.yourdomain.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequest {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 8, max = 128)
    private String newPassword;
}
