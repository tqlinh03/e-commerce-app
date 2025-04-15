package com.linh.ecommerce.notification;

public record ForgotPassword(
        String to,
        String activationCode
) {
}
