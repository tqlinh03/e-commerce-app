package com.linh.ecommerce.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linh.ecommerce.account.*;
import com.linh.ecommerce.customer.CustomerClient;
import com.linh.ecommerce.customer.CustomerInfoRequest;
import com.linh.ecommerce.customer.StoreInfoRequest;
import com.linh.ecommerce.security.JwtService;
import com.linh.ecommerce.token.Token;
import com.linh.ecommerce.token.TokenRepository;
import com.linh.ecommerce.token.TokenService;
import com.linh.ecommerce.token.TokenType;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var account = ((Account) auth.getPrincipal());
        if (!account.isVerified()) {
            throw new IllegalStateException("Account is not verified. Please verify your email before logging in.");
        }

        var claims = new HashMap<String, Object>();
        claims.put("email", account.getEmail());

        var jwtToken = jwtService.generateToken(claims, (Account) auth.getPrincipal());
        var refreshToken = jwtService.generateRefreshToken(account);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByVerificationCode(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            tokenService.sendValidationEmail(savedToken.getAccount());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var account = accountRepository.findById(savedToken.getAccount().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        account.setVerified(true);
        accountRepository.save(account);

        savedToken.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = accountRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);

                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void resetPassword(ResetPasswordRequest request) throws MessagingException{
        // Find user
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Find token
        Token savedToken = tokenRepository.findByVerificationCode(request.token())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        // Check token
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            tokenService.sendForgotPasswordEmail(savedToken.getAccount());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        // Check if the two new password are the same
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new BadCredentialsException("Password are not the same");
        }

        // Update verified_at token
        savedToken.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

        // Update the password
        account.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        accountRepository.save(account);
    }

    public void sendTokenForgotPasswordEmail(ForgotPasswordRequest request) throws MessagingException {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Not found account with email " + request.email()));

        // Check exists token Expires
        boolean exists = tokenRepository.existsByAccountAndTypeAndExpiresAtAfter(
                account,
                TokenType.FORGOT_PASSWORD,
                LocalDateTime.now()
        );
        if (exists) return;

        // Send email
        tokenService.sendForgotPasswordEmail(account);
    }

    @Transactional
    public String registerCustomer(CustomerRegistrationRequest request) throws MessagingException {
        // Save account
        AccountRequest accountRequest = AccountRequest.builder()
                .email(request.email())
                .password(request.password())
                .build();
        Account account = accountMapper.toAccount(accountRequest, Role.CUSTOMER);
        var saveAccount = accountRepository.save(account);

        // Save info customer
        CustomerInfoRequest customerInfoRequest = CustomerInfoRequest.builder()
                .email(request.email())
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .build();
        String customerId = customerClient.saveInfoCustomer(customerInfoRequest);

        //send email
        tokenService.sendValidationEmail(saveAccount);

        return customerId;
    }

    @Transactional
    public String registerStoreAccount(StoreRegistrationRequest request) throws MessagingException {
        // Save account
        AccountRequest accountRequest = AccountRequest.builder()
                .email(request.email())
                .password(request.password())
                .build();
        Account account = accountMapper.toAccount(accountRequest, Role.CUSTOMER);
        var saveAccount = accountRepository.save(account);

        // Save info store
        StoreInfoRequest customerInfoRequest = StoreInfoRequest.builder()
                .email(request.email())
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .storeName(request.storeName())
                .subdomain(request.subdomain())
                .build();
        String customerId = customerClient.saveInfoStoreRegistration(customerInfoRequest);

        //send email
        tokenService.sendValidationEmail(saveAccount);

        return customerId;
    }
}
