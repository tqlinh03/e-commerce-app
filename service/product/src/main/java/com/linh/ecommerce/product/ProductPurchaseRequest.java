package com.linh.ecommerce.product;

import java.util.UUID;

public record ProductPurchaseRequest(
        UUID productId,
        UUID sizeId, // This can be null for products without sizes like TV, refrigerator
        Integer quantity
) {}