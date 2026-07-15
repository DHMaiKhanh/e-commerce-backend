package com.yourdomain.ecommerce.service.impl;

import com.yourdomain.ecommerce.dto.request.CreateUserRequest;
import com.yourdomain.ecommerce.dto.request.UpdateProfileRequest;
import com.yourdomain.ecommerce.dto.request.UpdateUserRequest;
import com.yourdomain.ecommerce.dto.response.UserResponse;
import com.yourdomain.ecommerce.entity.User;
import com.yourdomain.ecommerce.enums.UserStatus;
import com.yourdomain.ecommerce.exception.ErrorCode;
import com.yourdomain.ecommerce.exception.ResourceNotFoundException;
import com.yourdomain.ecommerce.mapper.UserMapper;
import com.yourdomain.ecommerce.repository.UserRepository;
import com.yourdomain.ecommerce.service.UserService;
import com.yourdomain.ecommerce.service.helper.UserValidationHelper;
import com.yourdomain.ecommerce.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidationHelper validationHelper;
    private final PasswordEncoder passwordEncoder;
    private final UserResolver userResolver;

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        validationHelper.ensureUsernameAvailable(request.getUsername());
        validationHelper.ensureEmailAvailable(request.getEmail());

        User entity = userMapper.toEntity(request);
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        entity.setStatus(UserStatus.ACTIVE);
        entity.setRoles(request.getRoles());

        User saved = userRepository.save(entity);
        log.info("Created user id={} username={}", saved.getId(), saved.getUsername());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findByIdOrThrow(id);
        userMapper.updateEntity(request, user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> search(String keyword, Pageable pageable) {
        return userRepository.findAll(UserSpecification.build(keyword, null), pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = findByIdOrThrow(id);
        user.setStatus(UserStatus.DELETED);
        log.info("Soft-deleted user id={}", id);
    }

    private User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "User", id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile() {
        return userMapper.toResponse(userResolver.getCurrentUserOrThrow());
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(UpdateProfileRequest request) {
        User user = userResolver.getCurrentUserOrThrow();
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            validationHelper.ensureEmailAvailable(request.getEmail());
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName());
        }
        return userMapper.toResponse(user);
    }
}
