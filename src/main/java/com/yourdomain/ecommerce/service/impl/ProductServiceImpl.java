package com.yourdomain.ecommerce.service.impl;

import com.yourdomain.ecommerce.dto.request.CreateProductRequest;
import com.yourdomain.ecommerce.dto.request.UpdateProductRequest;
import com.yourdomain.ecommerce.dto.response.CategoryResponse;
import com.yourdomain.ecommerce.dto.response.ProductResponse;
import com.yourdomain.ecommerce.entity.Category;
import com.yourdomain.ecommerce.entity.Product;
import com.yourdomain.ecommerce.enums.ProductStatus;
import com.yourdomain.ecommerce.exception.ErrorCode;
import com.yourdomain.ecommerce.exception.ResourceNotFoundException;
import com.yourdomain.ecommerce.mapper.CategoryMapper;
import com.yourdomain.ecommerce.mapper.ProductMapper;
import com.yourdomain.ecommerce.repository.CategoryRepository;
import com.yourdomain.ecommerce.repository.ProductRepository;
import com.yourdomain.ecommerce.service.ProductService;
import com.yourdomain.ecommerce.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product entity = productMapper.toEntity(request);
        entity.setCategory(findCategoryOrThrow(request.getCategoryId()));
        entity.setStatus(ProductStatus.ACTIVE);

        Product saved = productRepository.save(entity);
        log.info("Created product id={} slug={}", saved.getId(), saved.getSlug());
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = findByIdOrThrow(id);
        productMapper.updateEntity(request, product);
        if (request.getCategoryId() != null) {
            product.setCategory(findCategoryOrThrow(request.getCategoryId()));
        }
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {
        Product product = productRepository.findBySlugAndStatusNot(slug, ProductStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Product", slug));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice,
                                         Pageable pageable) {
        return productRepository.findAll(ProductSpecification.build(keyword, category, minPrice, maxPrice), pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getFeatured() {
        return productRepository.findByFeaturedTrueAndStatus(ProductStatus.ACTIVE, Pageable.ofSize(20))
                .map(productMapper::toResponse)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findByIdOrThrow(id);
        product.setStatus(ProductStatus.DELETED);
        log.info("Soft-deleted product id={}", id);
    }

    private Product findByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Product", id));
    }

    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Category", categoryId));
    }
}
