package com.linh.ecommerce.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    List<Inventory> findByProductId(UUID productId);
    void deleteByProductId(UUID productId);
    
    @Query("SELECT i FROM Inventory i LEFT JOIN FETCH i.size WHERE i.product.id = :productId AND i.size.id = :sizeId")
    Optional<Inventory> findByProductIdAndSizeId(@Param("productId") UUID productId, @Param("sizeId") UUID sizeId);
    
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.size IS NULL")
    Optional<Inventory> findByProductIdAndSizeIdIsNull(@Param("productId") UUID productId);
}
