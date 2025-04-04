package com.linh.ecommerce.category;

import com.linh.ecommerce.product.Product;
import com.linh.ecommerce.product.ProductResponse;

import java.util.List;
import java.util.UUID;

public record CategoryResponse(
    UUID id,
    String name,
    String description,
    List<ProductResponse> products
) {} 