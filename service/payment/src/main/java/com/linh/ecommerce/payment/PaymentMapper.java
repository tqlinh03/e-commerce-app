package com.linh.ecommerce.payment;

import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {

  public Payment toPayment(PaymentRequest request) {
    if (request == null) {
      return null;
    }
    
    return Payment.builder()
        .paymentMethod(request.paymentMethod())
        .amount(request.amount())
        .orderId(request.orderId())
        .orderReference(request.orderReference())
        .description(request.description())
        .status(PaymentStatus.PENDING)
        .build();
  }
  
  /**
   * Chuyển đổi Payment sang PaymentResponse
   */
  public PaymentResponse toPaymentResponse(Payment payment) {
    return new PaymentResponse(
        payment.getId(),
        payment.getOrderReference(),
        payment.getAmount(),
        payment.getPaymentMethod(),
        payment.getStatus(),
        payment.getDescription(),
        payment.getCreatedDate()
    );
  }
}
