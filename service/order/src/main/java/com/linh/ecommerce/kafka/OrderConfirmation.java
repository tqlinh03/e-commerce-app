package com.linh.ecommerce.kafka;


import com.linh.ecommerce.customer.CustomerResponse;
import com.linh.ecommerce.payment.PaymentMethod;
import com.linh.ecommerce.product.ProductPurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        Long orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<ProductPurchaseResponse> products

) {
}
