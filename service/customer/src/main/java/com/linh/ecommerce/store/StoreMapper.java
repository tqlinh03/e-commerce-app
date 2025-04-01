package com.linh.ecommerce.store;

import com.linh.ecommerce.user.User;
import org.springframework.stereotype.Service;

@Service
public class StoreMapper {
    public Store toStore(StoreRequest request, User owner) {
        if (request == null) {
            return null;
        }
        return Store.builder()
                .storeName(request.storeName())
                .subdomain(request.subdomain())
                .owner(owner)
                .build();
    }
}
