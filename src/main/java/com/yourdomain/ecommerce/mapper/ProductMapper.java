package com.yourdomain.ecommerce.mapper;

import com.yourdomain.ecommerce.dto.request.CreateProductRequest;
import com.yourdomain.ecommerce.dto.request.UpdateProductRequest;
import com.yourdomain.ecommerce.dto.response.ProductResponse;
import com.yourdomain.ecommerce.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class, uses = CategoryMapper.class)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    Product toEntity(CreateProductRequest request);

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateProductRequest request, @MappingTarget Product product);
}
