package com.linh.ecommerce.role;

import lombok.Getter;

@Getter
public enum RoleName {
    ADMIN("ADMIN"),
    CUSTOMER("CUSTOMER"),
    STORE_OWNER("STORE_OWNER"),
    STAFF("STAFF");

    private final String description;

    RoleName(String description) {
        this.description = description;
    }
}
