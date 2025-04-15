package com.linh.ecommerce.orderline;

import java.util.UUID;

public record OrderLineResponse(
        UUID id,
        String size,
        double quantity
) { }
