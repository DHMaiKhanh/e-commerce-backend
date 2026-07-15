package com.yourdomain.ecommerce.specification;

import com.yourdomain.ecommerce.entity.Product;
import com.yourdomain.ecommerce.enums.ProductStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> withKeyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Product> withCategorySlug(String categorySlug) {
        return (root, query, cb) -> !StringUtils.hasText(categorySlug)
                ? cb.conjunction()
                : cb.equal(root.get("category").get("slug"), categorySlug);
    }

    public static Specification<Product> withMinPrice(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? cb.conjunction() : cb.ge(root.get("price"), minPrice);
    }

    public static Specification<Product> withMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? cb.conjunction() : cb.le(root.get("price"), maxPrice);
    }

    public static Specification<Product> notDeleted() {
        return (root, query, cb) -> cb.notEqual(root.get("status"), ProductStatus.DELETED);
    }

    public static Specification<Product> build(String keyword, String categorySlug, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Specification<Product>> specs = new ArrayList<>();
        specs.add(notDeleted());
        specs.add(withKeyword(keyword));
        specs.add(withCategorySlug(categorySlug));
        specs.add(withMinPrice(minPrice));
        specs.add(withMaxPrice(maxPrice));
        return specs.stream().reduce(Specification.where(null), Specification::and);
    }
}
