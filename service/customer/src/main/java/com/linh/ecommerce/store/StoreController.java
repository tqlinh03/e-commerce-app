package com.linh.ecommerce.store;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/{customer-id}")
    public ResponseEntity<StoreResponse> findStoreByCustomerId(
            @PathVariable("customer-id") UUID customerId
    ) {
        return ResponseEntity.ok(storeService.findStoreByCustomerId(customerId));
    }
}
