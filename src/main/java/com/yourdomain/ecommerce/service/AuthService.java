package com.yourdomain.ecommerce.service;

import com.yourdomain.ecommerce.dto.request.ForgotPasswordRequest;
import com.yourdomain.ecommerce.dto.request.LoginRequest;
import com.yourdomain.ecommerce.dto.request.ResetPasswordRequest;
import com.yourdomain.ecommerce.dto.response.TokenResponse;
import com.yourdomain.ecommerce.dto.response.UserResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request);

    TokenResponse refresh(String refreshToken);

    UserResponse getCurrentUser();

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
