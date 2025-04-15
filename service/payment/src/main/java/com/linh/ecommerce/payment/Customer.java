package com.linh.ecommerce.payment;

import java.util.UUID;

public record Customer(
        UUID id,
        String email,
        String fullName,
        String phoneNumber
) { }
