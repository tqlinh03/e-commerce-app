package com.linh.ecommerce.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.linh.ecommerce.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(Include.NON_EMPTY)
public record OrderResponse(
    Long reference,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    UUID customerId
) {

}
