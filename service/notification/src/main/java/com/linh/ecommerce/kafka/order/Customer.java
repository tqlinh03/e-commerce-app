package com.linh.ecommerce.kafka.order;

import java.util.UUID;

public record Customer(
    UUID id,
    String fullName,
    String email
) {

}
