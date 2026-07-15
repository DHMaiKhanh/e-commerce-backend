package com.yourdomain.ecommerce.dto.response;

import com.yourdomain.ecommerce.enums.ProductStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String slug;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer stock;
    private List<String> images;
    private CategoryResponse category;
    private Double rating;
    private Integer reviewCount;
    private Integer sold;
    private String location;
    private boolean official;
    private boolean freeShipping;
    private boolean featured;
    private ProductStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
