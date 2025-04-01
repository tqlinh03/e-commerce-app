package com.linh.ecommerce.category;

import com.linh.ecommerce.product.Product;

import java.util.List;
import java.util.UUID;

public record CategoryResponse(
    UUID id,
    String name,
    String description,
    List<Product> products
) {} 