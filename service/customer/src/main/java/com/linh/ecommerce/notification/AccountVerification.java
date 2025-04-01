package com.linh.ecommerce.notification;

public record AccountVerification(
        String to,
        String username,
        String confirmationUrl,
        String activationCode
) {
}
