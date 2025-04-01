package com.linh.ecommerce.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StoreRegistrationRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
        String phoneNumber,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Store name is required")
        String storeName,

        @NotBlank(message = "Subdomain URL is required")
        String subdomain
) {


}
