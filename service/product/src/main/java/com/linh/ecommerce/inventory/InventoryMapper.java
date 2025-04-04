package com.linh.ecommerce.inventory;

import com.linh.ecommerce.product.Product;
import com.linh.ecommerce.size.Size;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryMapper {
    public List<Inventory> toInventories(Product product, List<InventoryRequest> inventoryRequests) {
        return inventoryRequests.stream()
                .map(req -> Inventory.builder()
                        .product(product)
                        .size(Size.builder().id(req.sizeId()).build())
                        .storeId(product.getStoreId())
                        .quantity(req.quantity())
                        .price(req.price())
                        .build())
                .toList();
    }
}
