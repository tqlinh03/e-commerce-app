package com.linh.ecommerce.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.linh.ecommerce.payment.PaymentMethod;
import com.linh.ecommerce.product.ProductPurchaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@JsonInclude(Include.NON_EMPTY)
public record OrderRequest(
    Long reference,
    @Positive(message = "Order amount should be positive")
    BigDecimal amount,
    @NotNull(message = "Payment method should be precised")
    PaymentMethod paymentMethod,
    @NotNull(message = "Customer should be present")
    UUID customerId,
    @NotEmpty(message = "You should at least purchase one product")
    List<ProductPurchaseRequest> products
) {

}
