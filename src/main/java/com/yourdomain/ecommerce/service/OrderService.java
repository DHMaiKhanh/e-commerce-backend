package com.yourdomain.ecommerce.service;

import com.yourdomain.ecommerce.dto.request.CheckoutRequest;
import com.yourdomain.ecommerce.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse checkout(CheckoutRequest request);

    Page<OrderResponse> getMyOrders(Pageable pageable);

    OrderResponse getMyOrderById(Long id);
}
