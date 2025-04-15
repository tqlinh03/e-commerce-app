package com.linh.ecommerce.size;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SizeRepository extends JpaRepository<Size, UUID> {
    List<Size> findAllByIdInOrderById(List<UUID> sizeIds);
}
