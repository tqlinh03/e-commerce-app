package com.linh.ecommerce.store;

import lombok.Builder;

import java.util.UUID;

@Builder
public class StoreResponse {
    private UUID id;
    private UUID owner_id;

    private String storeName;

    private String subdomain;

    private String storeUrl;

}
