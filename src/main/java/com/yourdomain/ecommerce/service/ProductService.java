package com.yourdomain.ecommerce.service;

import com.yourdomain.ecommerce.dto.request.CreateProductRequest;
import com.yourdomain.ecommerce.dto.request.UpdateProductRequest;
import com.yourdomain.ecommerce.dto.response.CategoryResponse;
import com.yourdomain.ecommerce.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    ProductResponse getById(Long id);

    ProductResponse getBySlug(String slug);

    Page<ProductResponse> search(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                                  Pageable pageable);

    List<ProductResponse> getFeatured();

    List<CategoryResponse> getCategories();

    void delete(Long id);
}
