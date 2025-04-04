package com.linh.ecommerce.inventory;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryRequest(
        UUID sizeId,
        BigDecimal price,
        Integer quantity
) {
}
