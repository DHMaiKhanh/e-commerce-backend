package com.yourdomain.ecommerce.dto.request;

import com.yourdomain.ecommerce.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CreateUserRequest {

    @NotBlank
    @Size(min = 3, max = 64)
    private String username;

    @NotBlank
    @Email
    @Size(max = 128)
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @Size(max = 128)
    private String fullName;

    @NotNull
    private Set<Role> roles;
}
