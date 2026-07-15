package com.yourdomain.ecommerce.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreateProductRequest {

    @NotBlank
    @Size(max = 256)
    private String name;

    @NotBlank
    @Size(max = 256)
    private String slug;

    private String description;

    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal price;

    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal salePrice;

    @NotNull
    @Min(0)
    private Integer stock;

    private List<String> images;

    @NotNull
    private Long categoryId;

    private String location;

    private boolean official;

    private boolean freeShipping;

    private boolean featured;
}
