package com.yourdomain.ecommerce.controller;

import com.yourdomain.ecommerce.common.ApiResponse;
import com.yourdomain.ecommerce.constants.AppConstants;
import com.yourdomain.ecommerce.dto.request.CreateUserRequest;
import com.yourdomain.ecommerce.dto.request.ForgotPasswordRequest;
import com.yourdomain.ecommerce.dto.request.LoginRequest;
import com.yourdomain.ecommerce.dto.request.ResetPasswordRequest;
import com.yourdomain.ecommerce.dto.response.TokenResponse;
import com.yourdomain.ecommerce.dto.response.UserResponse;
import com.yourdomain.ecommerce.service.AuthService;
import com.yourdomain.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@Validated
@RestController
@RequestMapping(AppConstants.API_V1 + "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Login with username/password")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestParam @NotBlank String refreshToken) {
        return ApiResponse.success(authService.refresh(refreshToken));
    }

    @Operation(summary = "Self-register customer account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Registered", created));
    }

    @Operation(summary = "Get current authenticated user")
    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        return ApiResponse.success(authService.getCurrentUser());
    }

    @Operation(summary = "Logout current session (stateless: client discards tokens)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Request a password reset token by email")
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.success("If the email exists, a reset link has been sent", null);
    }

    @Operation(summary = "Reset password using a reset token")
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password reset successful", null);
    }
}
