package com.linh.ecommerce.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductPurchaseResponse(
        UUID productId,
        String productName,
        String sizeName,
        BigDecimal price,
        Integer quantity
) {}