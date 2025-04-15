package com.linh.ecommerce.store;

import com.linh.ecommerce.customer.Customer;
import org.springframework.stereotype.Service;

@Service
public class StoreMapper {
    public Store toStore(StoreRequest request, Customer owner) {
        if (request == null) {
            return null;
        }
        return Store.builder()
                .storeName(request.storeName())
                .subdomain(request.subdomain())
                .owner(owner)
                .build();
    }

    public StoreResponse toStoreResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .owner_id(store.getId())
                .storeName(store.getStoreName())
                .storeUrl(store.getStoreUrl())
                .subdomain(store.getSubdomain())
                .build();
    }
}
