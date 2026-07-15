package com.yourdomain.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCartItemRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
