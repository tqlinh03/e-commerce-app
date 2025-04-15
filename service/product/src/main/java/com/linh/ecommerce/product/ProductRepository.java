package com.linh.ecommerce.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStoreId(UUID storeId);

    List<Product> findAllByIdInOrderById(List<UUID> productIds);

    // Sử dụng JPQL để kiểm tra sự tồn tại của cặp productId và sizeId
    @Query("SELECT COUNT(ps) > 0 FROM Product p JOIN p.sizes ps WHERE p.id = :productId AND ps.id = :sizeId")
    boolean existsByProductIdAndSizeId(UUID productId, UUID sizeId);
}