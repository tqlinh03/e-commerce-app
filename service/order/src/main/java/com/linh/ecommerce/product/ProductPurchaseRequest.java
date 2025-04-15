package com.linh.ecommerce.product;

import java.util.UUID;

public record ProductPurchaseRequest(
        UUID productId,
        UUID sizeId,
        Integer quantity
) {}