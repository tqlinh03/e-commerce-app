package com.linh.ecommerce.product;

import com.linh.ecommerce.size.SizeType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    UUID storeId,
    String name,
    String description,
    String imageUrl,
    List<CategoryDTO> categories,
    List<SizeWithInventoryDTO> sizes
) {}

record CategoryDTO(
    UUID id,
    String name,
    String description
) {}

record SizeWithInventoryDTO(
    UUID id,
    SizeType type,
    String value,
    Integer quantity
) {} 