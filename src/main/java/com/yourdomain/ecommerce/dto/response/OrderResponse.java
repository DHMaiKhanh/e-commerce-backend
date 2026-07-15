package com.yourdomain.ecommerce.dto.response;

import com.yourdomain.ecommerce.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String note;
    private List<OrderItemResponse> items;
    private Instant createdAt;
}
