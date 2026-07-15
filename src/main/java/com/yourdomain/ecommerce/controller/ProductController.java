package com.yourdomain.ecommerce.controller;

import com.yourdomain.ecommerce.common.ApiResponse;
import com.yourdomain.ecommerce.common.PageResponse;
import com.yourdomain.ecommerce.constants.AppConstants;
import com.yourdomain.ecommerce.dto.request.CreateProductRequest;
import com.yourdomain.ecommerce.dto.request.UpdateProductRequest;
import com.yourdomain.ecommerce.dto.response.CategoryResponse;
import com.yourdomain.ecommerce.dto.response.ProductResponse;
import com.yourdomain.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Products")
@RestController
@RequestMapping(AppConstants.API_V1 + "/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Search products")
    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @ParameterObject Pageable pageable) {
        Pageable sorted = applySort(pageable, sort);
        return ApiResponse.success(PageResponse.of(
                productService.search(search, category, minPrice, maxPrice, sorted)));
    }

    @Operation(summary = "Get featured products")
    @GetMapping("/featured")
    public ApiResponse<List<ProductResponse>> getFeatured() {
        return ApiResponse.success(productService.getFeatured());
    }

    @Operation(summary = "Get product categories")
    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> getCategories() {
        return ApiResponse.success(productService.getCategories());
    }

    @Operation(summary = "Get product by slug")
    @GetMapping("/slug/{slug}")
    public ApiResponse<ProductResponse> getBySlug(@PathVariable String slug) {
        return ApiResponse.success(productService.getBySlug(slug));
    }

    @Operation(summary = "Get product by id")
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(productService.getById(id));
    }

    @Operation(summary = "Create product")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product created", created));
    }

    @Operation(summary = "Update product")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return ApiResponse.success("Product updated", productService.update(id, request));
    }

    @Operation(summary = "Soft delete product")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Pageable applySort(Pageable pageable, String sort) {
        if (sort == null) {
            return pageable;
        }
        Sort resolved = switch (sort) {
            case "price-asc" -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "newest" -> Sort.by("createdAt").descending();
            case "popular" -> Sort.by("sold").descending();
            default -> pageable.getSort();
        };
        return pageable.getSort().isSorted() ? pageable : org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), resolved);
    }
}
