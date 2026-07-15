package com.yourdomain.ecommerce.service.impl;

import com.yourdomain.ecommerce.dto.request.AddCartItemRequest;
import com.yourdomain.ecommerce.dto.request.UpdateCartItemRequest;
import com.yourdomain.ecommerce.dto.response.CartItemResponse;
import com.yourdomain.ecommerce.dto.response.CartResponse;
import com.yourdomain.ecommerce.entity.Cart;
import com.yourdomain.ecommerce.entity.CartItem;
import com.yourdomain.ecommerce.entity.Product;
import com.yourdomain.ecommerce.entity.User;
import com.yourdomain.ecommerce.exception.ErrorCode;
import com.yourdomain.ecommerce.exception.ResourceNotFoundException;
import com.yourdomain.ecommerce.repository.CartItemRepository;
import com.yourdomain.ecommerce.repository.CartRepository;
import com.yourdomain.ecommerce.repository.ProductRepository;
import com.yourdomain.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserResolver userResolver;

    @Override
    @Transactional
    public CartResponse getMyCart() {
        return toResponse(findOrCreateCart());
    }

    @Override
    @Transactional
    public CartResponse addItem(AddCartItemRequest request) {
        Cart cart = findOrCreateCart();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Product", request.getProductId()));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem created = CartItem.builder().cart(cart).product(product).quantity(0).build();
                    cart.getItems().add(created);
                    return created;
                });
        item.setQuantity(item.getQuantity() + request.getQuantity());
        cartItemRepository.save(item);

        log.info("Added product={} qty={} to cart={}", product.getId(), request.getQuantity(), cart.getId());
        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long itemId, UpdateCartItemRequest request) {
        Cart cart = findOrCreateCart();
        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND, "CartItem", itemId));
        item.setQuantity(request.getQuantity());
        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long itemId) {
        Cart cart = findOrCreateCart();
        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND, "CartItem", itemId));
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return toResponse(cart);
    }

    @Override
    @Transactional
    public void clear() {
        Cart cart = findOrCreateCart();
        cart.getItems().clear();
        log.info("Cleared cart={}", cart.getId());
    }

    private Cart findOrCreateCart() {
        User user = userResolver.getCurrentUserOrThrow();
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();
        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalItems = items.stream().mapToInt(CartItemResponse::getQuantity).sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .subtotal(subtotal)
                .totalItems(totalItems)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        Product product = item.getProduct();
        BigDecimal unitPrice = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
        if (product.getStock() != null && product.getStock() <= 0) {
            log.debug("Product {} is out of stock but remains in cart {}", product.getId(), item.getCart().getId());
        }
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .productImage(product.getImages().isEmpty() ? null : product.getImages().get(0))
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .subtotal(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}
