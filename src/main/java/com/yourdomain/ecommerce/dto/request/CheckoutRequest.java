package com.yourdomain.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutRequest {

    @NotBlank
    @Size(max = 128)
    private String recipientName;

    @NotBlank
    @Size(max = 32)
    private String recipientPhone;

    @NotBlank
    @Size(max = 512)
    private String shippingAddress;

    @Size(max = 512)
    private String note;
}
