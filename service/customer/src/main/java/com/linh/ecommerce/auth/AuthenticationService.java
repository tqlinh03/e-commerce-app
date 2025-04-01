package com.linh.ecommerce.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linh.ecommerce.token.Token;
import com.linh.ecommerce.token.TokenRepository;
import com.linh.ecommerce.token.TokenService;
import com.linh.ecommerce.cloudflare.CloudflareService;
import com.linh.ecommerce.role.Role;
import com.linh.ecommerce.role.RoleName;
import com.linh.ecommerce.role.RoleRepository;
import com.linh.ecommerce.security.JwtService;
import com.linh.ecommerce.store.Store;
import com.linh.ecommerce.store.StoreRepository;
import com.linh.ecommerce.store.StoreRequest;
import com.linh.ecommerce.store.StoreService;
import com.linh.ecommerce.token.TokenType;
import com.linh.ecommerce.user.User;
import com.linh.ecommerce.user.UserRepository;
import com.linh.ecommerce.user.UserRequest;
import com.linh.ecommerce.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final StoreService storeService;
    private final StoreRepository storeRepository;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CloudflareService cloudflareService;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.cloudflare.domain}")
    private String rootDomain;

    public String registerCustomer(UserRequest request) {
        // Find Role
        Role role = roleRepository.findByName(RoleName.CUSTOMER);

        return userService.createUser(request, role).getId().toString();
    }

    @Transactional
    public String registerStoreOwner(StoreRegistrationRequest request) {
        // Create request
        UserRequest userRequest = UserRequest.builder()
                .email(request.email())
                .password(request.password())
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .build();

        StoreRequest storeRequest = StoreRequest.builder()
                .storeName(request.storeName())
                .subdomain(request.subdomain())
                .build();
        // Find Role
        Role role = roleRepository.findByName(RoleName.STORE_OWNER);

        // Create user
        User user = userService.createUser(userRequest, role);

        // Create store
        Store store = storeService.createStore(storeRequest, user);

        try {
            // Create subdomain
            cloudflareService.createDnsRecord(store.getSubdomain());

            // Update store url
            String storeUrl = String.format("https://%s.%s", store.getSubdomain(), rootDomain);
            store.setStoreUrl(storeUrl);
            storeRepository.save(store);

            // Send email
            tokenService.sendValidationEmail(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create DNS record for subdomain: " + store.getStoreUrl(), e);
        } catch (MessagingException e) {
            log.error("Failed to send validation email to {}: {}", user.getEmail(), e.getMessage());
        }
        return user.getId().toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        var refreshToken = jwtService.generateRefreshToken(user);
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
            tokenService.sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setVerified(true);
        userRepository.save(user);

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
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow();
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
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Not found user with email " + request.email()));

        // Find token
        Token savedToken = tokenRepository.findByVerificationCode(request.token())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        // Check token
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            tokenService.sendForgotPasswordEmail(savedToken.getUser());
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
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void sendTokenForgotPasswordEmail(ForgotPasswordRequest request) throws MessagingException {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Not found user with email " + request.email()));

        // Check exists token Expires
        boolean exists = tokenRepository.existsByUserAndTypeAndExpiresAtAfter(
                user,
                TokenType.FORGOT_PASSWORD,
                LocalDateTime.now()
        );
        if (exists) return;

        // Send email
        tokenService.sendForgotPasswordEmail(user);
    }
}
