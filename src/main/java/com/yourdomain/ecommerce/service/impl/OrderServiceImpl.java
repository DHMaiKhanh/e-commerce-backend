package com.yourdomain.ecommerce.service.impl;

import com.yourdomain.ecommerce.dto.request.CheckoutRequest;
import com.yourdomain.ecommerce.dto.response.OrderItemResponse;
import com.yourdomain.ecommerce.dto.response.OrderResponse;
import com.yourdomain.ecommerce.entity.Cart;
import com.yourdomain.ecommerce.entity.CartItem;
import com.yourdomain.ecommerce.entity.Order;
import com.yourdomain.ecommerce.entity.OrderItem;
import com.yourdomain.ecommerce.entity.Product;
import com.yourdomain.ecommerce.entity.User;
import com.yourdomain.ecommerce.exception.BusinessException;
import com.yourdomain.ecommerce.exception.ErrorCode;
import com.yourdomain.ecommerce.exception.ResourceNotFoundException;
import com.yourdomain.ecommerce.repository.CartRepository;
import com.yourdomain.ecommerce.repository.OrderRepository;
import com.yourdomain.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserResolver userResolver;

    @Override
    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        User user = userResolver.getCurrentUserOrThrow();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPTY_CART));
        if (cart.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_CART);
        }

        Order order = Order.builder()
                .user(user)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() == null || product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK,
                        "Insufficient stock for product: " + product.getName());
            }
            BigDecimal unitPrice = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(unitPrice)
                    .quantity(cartItem.getQuantity())
                    .build();
            order.getItems().add(orderItem);
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            product.setStock(product.getStock() - cartItem.getQuantity());
            product.setSold(product.getSold() + cartItem.getQuantity());
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        cart.getItems().clear();

        log.info("Checkout completed: order={} user={} total={}", saved.getId(), user.getId(), total);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Pageable pageable) {
        User user = userResolver.getCurrentUserOrThrow();
        return orderRepository.findByUserId(user.getId(), pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrderById(Long id) {
        User user = userResolver.getCurrentUserOrThrow();
        Order order = orderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "Order", id));
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .items(items)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
