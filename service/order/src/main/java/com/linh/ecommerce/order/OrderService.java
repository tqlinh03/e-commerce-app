package com.linh.ecommerce.order;

import com.linh.ecommerce.customer.CustomerClient;
import com.linh.ecommerce.exception.BusinessException;
import com.linh.ecommerce.kafka.OrderConfirmation;
import com.linh.ecommerce.kafka.OrderProducer;
import com.linh.ecommerce.orderline.OrderLineRequest;
import com.linh.ecommerce.orderline.OrderLineService;
import com.linh.ecommerce.payment.PaymentClient;
import com.linh.ecommerce.payment.PaymentRequest;
import com.linh.ecommerce.product.ProductClient;
import com.linh.ecommerce.product.ProductPurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    @Transactional
    public String createOrder(OrderRequest request) {
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        var purchasedProducts = productClient.purchaseProducts(request.products());

        var order = this.repository.save(mapper.toOrder(request));

        for (ProductPurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            purchaseRequest.productId(),
                            purchaseRequest.sizeId(),
                            purchaseRequest.quantity(),
                            order
                    )
            );
        }
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                "Thanh toán",
                "https://www.youtube.com/watch?v=kjS8p0kAV-I",
                "https://www.youtube.com/watch?v=kjS8p0kAV-I",
                customer
        );
        String paymentUrl = paymentClient.requestOrderPayment(paymentRequest)
                .orElseThrow(() -> new BusinessException("Failed to create payment URL"));

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return paymentUrl;
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(UUID id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
