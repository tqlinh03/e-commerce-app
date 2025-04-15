package com.linh.ecommerce.customer;

import com.linh.ecommerce.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(
    name = "customer-service",
    url = "${application.config.customer-url}",
    configuration = FeignClientConfig.class
)
public interface CustomerClient {

  @GetMapping("/{customer-id}")
  Optional<CustomerResponse> findCustomerById(@PathVariable("customer-id") UUID customerId);
}
