package com.linh.ecommerce.orderline;

import com.linh.ecommerce.order.Order;

import java.util.UUID;

public record OrderLineRequest(
        UUID productId,
        UUID sizeId,
        double quantity,
        Order order
) {
}
