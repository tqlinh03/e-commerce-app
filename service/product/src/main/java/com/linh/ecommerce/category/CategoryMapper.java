package com.linh.ecommerce.category;

import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {
    
    public Category toCategory(CategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getProducts()
        );
    }
} 