package com.yourdomain.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCartItemRequest {

    @NotNull
    @Min(1)
    private Integer quantity;
}
