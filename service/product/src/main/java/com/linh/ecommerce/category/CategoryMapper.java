package com.linh.ecommerce.category;

import com.linh.ecommerce.inventory.Inventory;
import com.linh.ecommerce.inventory.InventoryRepository;
import com.linh.ecommerce.product.ProductMapper;
import com.linh.ecommerce.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryMapper {
    private final InventoryRepository inventoryRepository;
    private final ProductMapper productMapper;
    
    public Category toCategory(CategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public CategoryResponse toCategoryResponse(Category category) {
        List<ProductResponse> productDTO = category.getProducts().stream()
                .map(product -> {
                    List<Inventory> inventories = inventoryRepository.findByProductId(product.getId());
                    return productMapper.toProductResponse(product, inventories);
                })
                .toList();

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                productDTO
        );
    }
} 