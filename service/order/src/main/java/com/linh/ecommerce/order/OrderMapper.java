package com.linh.ecommerce.order;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderMapper {


  public Order toOrder(OrderRequest request) {
    if (request == null) {
      return null;
    }
    return Order.builder()
        .reference(request.reference())
        .paymentMethod(request.paymentMethod())
        .customerId(request.customerId())
            .totalAmount(request.amount())
            .createdDate(LocalDateTime.now())
        .build();
  }

  public OrderResponse fromOrder(Order order) {
    return new OrderResponse(
        order.getReference(),
        order.getTotalAmount(),
        order.getPaymentMethod(),
        order.getCustomerId()
    );
  }
}
