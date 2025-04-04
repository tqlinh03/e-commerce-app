package com.linh.ecommerce.size;

import java.util.UUID;

public record SizeResponse(
        UUID id,
        SizeType type,
        String value
) {
}
