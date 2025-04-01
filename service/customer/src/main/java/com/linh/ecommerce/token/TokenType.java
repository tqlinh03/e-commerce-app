package com.linh.ecommerce.token;

import lombok.Getter;

@Getter
public enum TokenType {
    EMAIL("EMAIL"),
    FORGOT_PASSWORD("FORGOT_PASSWORD"),
    ACCOUNT_VERIFICATION("ACCOUNT_VERIFICATION")
    ;

    private final String description;

    TokenType(String description) {
        this.description = description;
    }
}
