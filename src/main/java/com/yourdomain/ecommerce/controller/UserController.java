package com.yourdomain.ecommerce.controller;

import com.yourdomain.ecommerce.common.ApiResponse;
import com.yourdomain.ecommerce.common.PageResponse;
import com.yourdomain.ecommerce.constants.AppConstants;
import com.yourdomain.ecommerce.dto.request.CreateUserRequest;
import com.yourdomain.ecommerce.dto.request.UpdateProfileRequest;
import com.yourdomain.ecommerce.dto.request.UpdateUserRequest;
import com.yourdomain.ecommerce.dto.response.UserResponse;
import com.yourdomain.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users")
@RestController
@RequestMapping(AppConstants.API_V1 + "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get current authenticated user's profile")
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile() {
        return ApiResponse.success(userService.getMyProfile());
    }

    @Operation(summary = "Update current authenticated user's profile")
    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success("Profile updated", userService.updateMyProfile(request));
    }

    @Operation(summary = "Create user")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User created", created));
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(userService.getById(id));
    }

    @Operation(summary = "Search users")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<PageResponse<UserResponse>> search(
            @RequestParam(required = false) String keyword,
            @ParameterObject Pageable pageable) {
        return ApiResponse.success(PageResponse.of(userService.search(keyword, pageable)));
    }

    @Operation(summary = "Update user")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success("User updated", userService.update(id, request));
    }

    @Operation(summary = "Soft delete user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
