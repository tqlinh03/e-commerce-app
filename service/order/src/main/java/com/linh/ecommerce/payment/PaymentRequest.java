package com.linh.ecommerce.payment;

import com.linh.ecommerce.customer.CustomerResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Yêu cầu thanh toán
 */
public record PaymentRequest(
    BigDecimal amount,
    PaymentMethod paymentMethod,
    UUID orderId,
    Long orderReference,
    String description,
    String returnUrl,
    String cancelUrl,
    CustomerResponse customer
) {
}
