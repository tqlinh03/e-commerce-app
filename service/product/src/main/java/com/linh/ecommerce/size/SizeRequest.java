package com.linh.ecommerce.size;

public record SizeRequest(
        SizeType type,
        String value
) {}