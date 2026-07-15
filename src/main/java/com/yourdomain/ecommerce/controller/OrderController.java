package com.yourdomain.ecommerce.controller;

import com.yourdomain.ecommerce.common.ApiResponse;
import com.yourdomain.ecommerce.common.PageResponse;
import com.yourdomain.ecommerce.constants.AppConstants;
import com.yourdomain.ecommerce.dto.request.CheckoutRequest;
import com.yourdomain.ecommerce.dto.response.OrderResponse;
import com.yourdomain.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders")
@RestController
@RequestMapping(AppConstants.API_V1 + "/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "List current user's orders")
    @GetMapping
    public ApiResponse<PageResponse<OrderResponse>> getMyOrders(@ParameterObject Pageable pageable) {
        return ApiResponse.success(PageResponse.of(orderService.getMyOrders(pageable)));
    }

    @Operation(summary = "Get current user's order by id")
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(orderService.getMyOrderById(id));
    }

    @Operation(summary = "Checkout current cart into an order")
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@Valid @RequestBody CheckoutRequest request) {
        OrderResponse created = orderService.checkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Order placed", created));
    }
}
