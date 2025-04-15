package com.linh.ecommerce.store;

import com.linh.ecommerce.customer.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository repository;
    private final StoreMapper mapper;
    private final StoreRepository storeRepository;

    public Store createStore(StoreRequest request, Customer owner) {
        validateStore(request);
        Store store = mapper.toStore(request, owner);
        return repository.save(store);
    }

    private void validateStore(StoreRequest request) {
        if (repository.existsBySubdomain(request.subdomain())) {
            throw new RuntimeException("Subdomain URL already exists");
        }

        if (repository.existsByStoreName(request.storeName())) {
            throw new RuntimeException("Store name already exists");
        }
        if (repository.findByOwnerId(request.ownerId()).isPresent()) {
            throw new RuntimeException("User already owns a store");
        }
    }

    public StoreResponse findStoreByCustomerId(UUID customerId) {
        Store store = storeRepository.findByOwnerId(customerId)
                .orElseThrow(() -> new RuntimeException("Store not found with customer id: " + customerId));
        return mapper.toStoreResponse(store);
    }
}
