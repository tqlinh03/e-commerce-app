package com.linh.ecommerce.product;

import com.linh.ecommerce.category.Category;
import com.linh.ecommerce.category.CategoryRepository;
import com.linh.ecommerce.inventory.Inventory;
import com.linh.ecommerce.inventory.InventoryRepository;
import com.linh.ecommerce.inventory.InventoryRequest;
import com.linh.ecommerce.size.Size;
import com.linh.ecommerce.size.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductMapper {
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;

    public Product toProduct(ProductRequest request) {
        List<Category> categories = categoryRepository.findAllById(request.categoryIds());
        List<Size> sizes = sizeRepository.findAllById(request.sizeIds());

        return Product.builder()
                .storeId(request.storeId())
                .name(request.name())
                .description(request.description())
                .categories(categories)
                .sizes(sizes)
                .build();
    }

    public Product updateProduct(Product product, ProductRequest request) {
        List<Category> categories = categoryRepository.findAllById(request.categoryIds());
        List<Size> sizes = sizeRepository.findAllById(request.sizeIds());

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategories(categories);
        product.setSizes(sizes);

        return product;
    }


    public ProductResponse toProductResponse(Product product, List<Inventory> inventories) {
        List<CategoryDTO> categoryDTOs = product.getCategories().stream()
                .map(category -> new CategoryDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                ))
                .toList();

        Map<UUID, Integer> sizeQuantityMap = inventories.stream()
                .collect(Collectors.toMap(
                        inventory -> inventory.getSize().getId(),
                        Inventory::getQuantity
                ));

        List<SizeWithInventoryDTO> sizeDTOs = product.getSizes().stream()
                .map(size -> new SizeWithInventoryDTO(
                        size.getId(),
                        size.getType(),
                        size.getValue(),
                        sizeQuantityMap.getOrDefault(size.getId(), 0)
                ))
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getStoreId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                categoryDTOs,
                sizeDTOs
        );
    }
} 