package com.yourdomain.ecommerce.service.impl;

import com.yourdomain.ecommerce.config.properties.JwtProperties;
import com.yourdomain.ecommerce.constants.SecurityConstants;
import com.yourdomain.ecommerce.dto.request.ForgotPasswordRequest;
import com.yourdomain.ecommerce.dto.request.LoginRequest;
import com.yourdomain.ecommerce.dto.request.ResetPasswordRequest;
import com.yourdomain.ecommerce.dto.response.TokenResponse;
import com.yourdomain.ecommerce.dto.response.UserResponse;
import com.yourdomain.ecommerce.entity.PasswordResetToken;
import com.yourdomain.ecommerce.entity.User;
import com.yourdomain.ecommerce.exception.BusinessException;
import com.yourdomain.ecommerce.exception.ErrorCode;
import com.yourdomain.ecommerce.mapper.UserMapper;
import com.yourdomain.ecommerce.repository.PasswordResetTokenRepository;
import com.yourdomain.ecommerce.repository.UserRepository;
import com.yourdomain.ecommerce.security.CustomUserDetails;
import com.yourdomain.ecommerce.security.JwtTokenProvider;
import com.yourdomain.ecommerce.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.yourdomain.ecommerce.service.impl.UserResolver userResolver;

    @Override
    public TokenResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            String access = tokenProvider.generateAccessToken(
                    principal.getId(), principal.getUsername(), principal.getRoles());
            String refresh = tokenProvider.generateRefreshToken(principal.getId(), principal.getUsername());

            log.info("Login success for user={}", principal.getUsername());
            return buildResponse(access, refresh);
        } catch (AuthenticationException ex) {
            log.warn("Login failed for user={}: {}", request.getUsername(), ex.getMessage());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        if (!tokenProvider.isValid(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        Claims claims = tokenProvider.parse(refreshToken);
        Number userIdNum = claims.get(SecurityConstants.CLAIM_USER_ID, Number.class);
        Long userId = userIdNum == null ? null : userIdNum.longValue();
        String username = claims.getSubject();
        var roles = tokenProvider.extractRoles(claims);

        String newAccess = tokenProvider.generateAccessToken(userId, username, roles);
        String newRefresh = tokenProvider.generateRefreshToken(userId, username);
        return buildResponse(newAccess, newRefresh);
    }

    private TokenResponse buildResponse(String access, String refresh) {
        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresInSeconds(jwtProperties.getAccessTokenExpirationMs() / 1000)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        return userMapper.toResponse(userResolver.getCurrentUserOrThrow());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresentOrElse(user -> {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .user(user)
                    .token(token)
                    .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                    .build();
            passwordResetTokenRepository.save(resetToken);
            // No email infrastructure yet: log the token so it can be delivered manually in dev/staging.
            log.info("Password reset requested for user={}. Reset token={}", user.getUsername(), token);
        }, () -> log.info("Password reset requested for unknown email={}", request.getEmail()));
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_RESET_TOKEN));
        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.INVALID_RESET_TOKEN);
        }
        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        resetToken.setUsed(true);
        log.info("Password reset for user={}", user.getUsername());
    }
}
