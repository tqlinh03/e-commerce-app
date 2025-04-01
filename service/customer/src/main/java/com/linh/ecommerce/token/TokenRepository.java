package com.linh.ecommerce.token;

import com.linh.ecommerce.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    Optional<Token> findByVerificationCode(String code);
    boolean existsByUserAndTypeAndExpiresAtAfter(
            User user,
            TokenType type,
            LocalDateTime currentTime
    );
}
