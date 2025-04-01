package com.linh.ecommerce.store;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
public record StoreRequest (
    @NotBlank(message = "Store name is required")
    String storeName,
    @NotBlank(message = "subdomain is required")
    String subdomain,
    UUID ownerId
) {}
