package com.yourdomain.ecommerce.service;

import com.yourdomain.ecommerce.dto.request.CreateUserRequest;
import com.yourdomain.ecommerce.dto.request.UpdateProfileRequest;
import com.yourdomain.ecommerce.dto.request.UpdateUserRequest;
import com.yourdomain.ecommerce.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse update(Long id, UpdateUserRequest request);

    UserResponse getById(Long id);

    Page<UserResponse> search(String keyword, Pageable pageable);

    void delete(Long id);

    UserResponse getMyProfile();

    UserResponse updateMyProfile(UpdateProfileRequest request);
}
