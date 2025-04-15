package com.linh.ecommerce.orderline;

import com.linh.ecommerce.order.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderLineMapper {
    public OrderLine toOrderLine(OrderLineRequest request) {
        return OrderLine.builder()
                .productId(request.productId())
                .order(request.order())
                .quantity(request.quantity())
                .sizeId(request.sizeId())
                .build();
    }

//    public OrderLineResponse toOrderLineResponse(OrderLine orderLine) {
//        return new OrderLineResponse(
//                orderLine.getId(),
//                orderLine.getSizeName(),
//                orderLine.getQuantity()
//        );
//    }
}
