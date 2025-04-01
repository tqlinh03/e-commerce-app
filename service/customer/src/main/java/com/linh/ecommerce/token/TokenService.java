package com.linh.ecommerce.token;

import com.linh.ecommerce.notification.AccountVerification;
import com.linh.ecommerce.notification.AccountVerificationProducer;
import com.linh.ecommerce.notification.ForgotPassword;
import com.linh.ecommerce.notification.ForgotPasswordProducer;
import com.linh.ecommerce.user.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final AccountVerificationProducer accountVerificationProducer;
    private final ForgotPasswordProducer forgotPasswordProducer;

    @Value("${spring.application.mailing.frontend}")
    private String activationUrl;

    private String generateAndSaveAccountVerification(User user, TokenType tokenType) throws MessagingException {
        // Generate a code
        String generatedCode = generateActivationCode(6);
        var accountVerification = Token.builder()
                .verificationCode(generatedCode)
                .user(user)
                .type(tokenType)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(accountVerification);

        return generatedCode;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public void sendValidationEmail(User user) throws MessagingException {
        var newCode = generateAndSaveAccountVerification(user, TokenType.ACCOUNT_VERIFICATION);

        accountVerificationProducer.sendNotification(
                new AccountVerification(
                        user.getEmail(),
                        user.getFullName(),
                        activationUrl,
                        newCode
                )
        );
    }

    public void sendForgotPasswordEmail(User user) throws MessagingException {
        var newCode = generateAndSaveAccountVerification(user, TokenType.FORGOT_PASSWORD);

        forgotPasswordProducer.sendNotification(
                new ForgotPassword(
                        user.getEmail(),
                        user.getFullName(),
                        newCode
                )
        );
    }
}
