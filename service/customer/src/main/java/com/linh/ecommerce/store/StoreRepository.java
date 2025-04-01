package com.linh.ecommerce.store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    boolean existsBySubdomain(String subdomain);
    boolean existsByStoreName(String name);
    Optional<Store> findByOwnerId(UUID ownerId);
}
