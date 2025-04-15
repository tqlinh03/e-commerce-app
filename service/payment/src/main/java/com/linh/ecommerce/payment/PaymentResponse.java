package com.linh.ecommerce.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Phản hồi thanh toán
 */
public record PaymentResponse(
    UUID id,
    Long orderReference,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String description,
    LocalDateTime createdDate
) {} 