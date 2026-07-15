package com.yourdomain.ecommerce.entity;

import com.yourdomain.ecommerce.common.BaseEntity;
import com.yourdomain.ecommerce.enums.ProductStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name = "uk_products_slug", columnNames = "slug")
})
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "slug", nullable = false, length = 256)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn(name = "position")
    @Column(name = "url", length = 512)
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "rating", nullable = false)
    @Builder.Default
    private Double rating = 0.0;

    @Column(name = "review_count", nullable = false)
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "sold", nullable = false)
    @Builder.Default
    private Integer sold = 0;

    @Column(name = "location", length = 128)
    private String location;

    @Column(name = "is_official", nullable = false)
    @Builder.Default
    private boolean official = false;

    @Column(name = "free_shipping", nullable = false)
    @Builder.Default
    private boolean freeShipping = false;

    @Column(name = "featured", nullable = false)
    @Builder.Default
    private boolean featured = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;
}
