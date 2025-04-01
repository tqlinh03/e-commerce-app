package com.linh.ecommerce.store;

import com.linh.ecommerce.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository repository;
    private final StoreMapper mapper;

    public Store createStore(StoreRequest request, User owner) {
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
}
