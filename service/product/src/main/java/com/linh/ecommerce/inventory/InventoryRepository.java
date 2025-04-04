package com.linh.ecommerce.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    List<Inventory> findByProductId(UUID productId);
    void deleteByProductId(UUID productId);
}
