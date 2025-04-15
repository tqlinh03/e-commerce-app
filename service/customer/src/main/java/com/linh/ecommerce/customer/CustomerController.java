package com.linh.ecommerce.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PatchMapping()
    public ResponseEntity<Void> updateCustomer(
            @RequestBody @Valid CustomerUpdateRequest request
    ) {
        customerService.updateCustomer(request);
        return ResponseEntity.accepted().build();
    }


    @GetMapping("/{customer-id}")
    public ResponseEntity<CustomerResponse> findById(
            @PathVariable("customer-id") UUID customerId
    ) {
        return ResponseEntity.ok(customerService.findById(customerId));
    }

    @PostMapping("/save-store")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> registerStore(
            @RequestBody @Valid StoreRegistrationRequest request
    ) throws IOException {
        return ResponseEntity.ok(customerService.registerStoreOwner(request));
    }

    @PostMapping("/save-customer")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> registerCustomer(
            @RequestBody @Valid CustomerRequest request
    ) {
        return ResponseEntity.ok(customerService.saveInfoCustomer(request));

    }
}
