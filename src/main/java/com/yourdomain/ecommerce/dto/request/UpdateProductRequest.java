package com.yourdomain.ecommerce.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class UpdateProductRequest {

    @Size(max = 256)
    private String name;

    @Size(max = 256)
    private String slug;

    private String description;

    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal price;

    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal salePrice;

    @Min(0)
    private Integer stock;

    private List<String> images;

    private Long categoryId;

    private String location;

    private Boolean official;

    private Boolean freeShipping;

    private Boolean featured;
}
