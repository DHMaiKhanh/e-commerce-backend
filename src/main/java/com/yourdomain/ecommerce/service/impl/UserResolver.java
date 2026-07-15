package com.yourdomain.ecommerce.service.impl;

import com.yourdomain.ecommerce.entity.User;
import com.yourdomain.ecommerce.exception.BusinessException;
import com.yourdomain.ecommerce.exception.ErrorCode;
import com.yourdomain.ecommerce.repository.UserRepository;
import com.yourdomain.ecommerce.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserResolver {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getCurrentUserOrThrow() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
