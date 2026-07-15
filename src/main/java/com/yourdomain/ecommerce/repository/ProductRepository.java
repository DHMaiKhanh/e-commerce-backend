package com.yourdomain.ecommerce.repository;

import com.yourdomain.ecommerce.entity.Product;
import com.yourdomain.ecommerce.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlugAndStatusNot(String slug, ProductStatus status);

    Page<Product> findByFeaturedTrueAndStatus(ProductStatus status, Pageable pageable);
}
