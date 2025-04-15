package com.linh.ecommerce.token;

import com.linh.ecommerce.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    Optional<Token> findByVerificationCode(String code);
    boolean existsByAccountAndTypeAndExpiresAtAfter(
            Account account,
            TokenType type,
            LocalDateTime currentTime
    );
}
