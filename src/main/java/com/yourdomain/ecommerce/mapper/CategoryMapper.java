package com.yourdomain.ecommerce.mapper;

import com.yourdomain.ecommerce.dto.response.CategoryResponse;
import com.yourdomain.ecommerce.entity.Category;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);
}
