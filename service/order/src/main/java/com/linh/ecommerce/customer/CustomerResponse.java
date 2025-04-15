package com.linh.ecommerce.customer;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String email,
        String fullName,
        String phoneNumber
) {

}

