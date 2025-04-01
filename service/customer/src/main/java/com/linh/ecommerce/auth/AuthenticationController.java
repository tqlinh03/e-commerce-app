package com.linh.ecommerce.auth;

import com.linh.ecommerce.user.UserRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register-store")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid StoreRegistrationRequest request
    ) throws MessagingException {
        authenticationService.registerStoreOwner(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/register-customer")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> registerCustomer(
            @RequestBody @Valid UserRequest request
    ) throws MessagingException {
        authenticationService.registerCustomer(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        authenticationService.activateAccount(token);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @GetMapping("/forgot-password")
    public void forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request
    ) throws MessagingException {
        authenticationService.sendTokenForgotPasswordEmail(request);
    }

    @PatchMapping("/reset-password")
    public void resetPassword(
            @RequestBody @Valid ResetPasswordRequest request
    ) throws MessagingException {
        authenticationService.resetPassword(request);
    }
}
