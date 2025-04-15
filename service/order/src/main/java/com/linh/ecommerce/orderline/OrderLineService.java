package com.linh.ecommerce.orderline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderLineService {

    private final OrderLineRepository repository;
    private final OrderLineMapper mapper;

    public UUID saveOrderLine(OrderLineRequest request) {
        var order = mapper.toOrderLine(request);
        return repository.save(order).getId();
    }

//    public List<OrderLineResponse> findAllByOrderId(UUID orderId) {
//        return repository.findAllByOrderId(orderId)
//                .stream()
//                .map(mapper::toOrderLineResponse)
//                .collect(Collectors.toList());
//    }
}
