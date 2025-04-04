package com.linh.ecommerce.product;

import com.linh.ecommerce.inventory.InventoryRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record ProductRequest(
    UUID storeId,
    String name,
    String description,
    List<UUID> categoryIds,
    List<UUID> sizeIds,
    List<InventoryRequest> inventories
) {
}
