package com.linh.ecommerce.orderline;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/order-lines")
@RequiredArgsConstructor
public class OrderLineController {

    private final OrderLineService service;

//    @GetMapping("/order/{order-id}")
//    public ResponseEntity<List<OrderLineResponse>> findByOrderId(
//            @PathVariable("order-id") UUID orderId
//    ) {
//        return ResponseEntity.ok(service.findAllByOrderId(orderId));
//    }
}
