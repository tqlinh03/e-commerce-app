package com.linh.ecommerce.kafka.account;

public record AccountVerification(
        String to,
        String username,
        String confirmationUrl,
        String activationCode
) {
}
