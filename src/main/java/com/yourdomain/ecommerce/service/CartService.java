package com.yourdomain.ecommerce.service;

import com.yourdomain.ecommerce.dto.request.AddCartItemRequest;
import com.yourdomain.ecommerce.dto.request.UpdateCartItemRequest;
import com.yourdomain.ecommerce.dto.response.CartResponse;

public interface CartService {

    CartResponse getMyCart();

    CartResponse addItem(AddCartItemRequest request);

    CartResponse updateItem(Long itemId, UpdateCartItemRequest request);

    CartResponse removeItem(Long itemId);

    void clear();
}
