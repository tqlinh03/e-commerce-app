package com.linh.ecommerce.kafka.account;

public record ForgotPassword(
        String to,
        String username,
        String activationCode
) {
}
