package com.linh.ecommerce.notification;

public record AccountVerification(
        String to,
        String confirmationUrl,
        String activationCode
) {
}
