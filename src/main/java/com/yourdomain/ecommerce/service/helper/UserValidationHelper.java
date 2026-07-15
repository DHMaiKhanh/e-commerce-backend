package com.yourdomain.ecommerce.service.helper;

import com.yourdomain.ecommerce.exception.BusinessException;
import com.yourdomain.ecommerce.exception.ErrorCode;
import com.yourdomain.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidationHelper {

    private final UserRepository userRepository;

    public void ensureUsernameAvailable(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS,
                    "Username already taken: " + username);
        }
    }

    public void ensureEmailAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS,
                    "Email already registered: " + email);
        }
    }
}
