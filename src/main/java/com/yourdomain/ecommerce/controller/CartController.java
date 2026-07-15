package com.yourdomain.ecommerce.controller;

import com.yourdomain.ecommerce.common.ApiResponse;
import com.yourdomain.ecommerce.constants.AppConstants;
import com.yourdomain.ecommerce.dto.request.AddCartItemRequest;
import com.yourdomain.ecommerce.dto.request.UpdateCartItemRequest;
import com.yourdomain.ecommerce.dto.response.CartResponse;
import com.yourdomain.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cart")
@RestController
@RequestMapping(AppConstants.API_V1 + "/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get current user's cart")
    @GetMapping
    public ApiResponse<CartResponse> getMyCart() {
        return ApiResponse.success(cartService.getMyCart());
    }

    @Operation(summary = "Add item to cart")
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody AddCartItemRequest request) {
        CartResponse updated = cartService.addItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Item added", updated));
    }

    @Operation(summary = "Update cart item quantity")
    @PutMapping("/items/{id}")
    public ApiResponse<CartResponse> updateItem(@PathVariable Long id, @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success("Item updated", cartService.updateItem(id, request));
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/items/{id}")
    public ApiResponse<CartResponse> removeItem(@PathVariable Long id) {
        return ApiResponse.success("Item removed", cartService.removeItem(id));
    }

    @Operation(summary = "Clear cart")
    @DeleteMapping
    public ResponseEntity<Void> clear() {
        cartService.clear();
        return ResponseEntity.noContent().build();
    }
}
