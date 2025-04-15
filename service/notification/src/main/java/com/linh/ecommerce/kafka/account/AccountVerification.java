package com.linh.ecommerce.kafka.account;

public record AccountVerification(
        String to,
        String confirmationUrl,
        String activationCode
) {
}
