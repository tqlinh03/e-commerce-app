package com.linh.ecommerce.payment;

import com.linh.ecommerce.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(
    name = "payment-service",
    url = "${application.config.payment-url}",
        configuration = FeignClientConfig.class
)
public interface PaymentClient {

  @PostMapping
  Optional<String> requestOrderPayment(@RequestBody PaymentRequest request);
}
