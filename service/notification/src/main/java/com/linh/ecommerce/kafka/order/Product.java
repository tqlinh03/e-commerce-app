package com.linh.ecommerce.kafka.order;

import java.math.BigDecimal;
import java.util.UUID;

public record Product(
        UUID productId,
        String productName,
        String sizeName,
        BigDecimal price,
        Integer quantity
) {
}
