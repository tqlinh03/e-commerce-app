package com.linh.ecommerce.user;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("User is active"),
    INACTIVE("User is inactive"),
    BANNED("User is banned");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }
}
