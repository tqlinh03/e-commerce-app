package com.linh.ecommerce.customer;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    UUID id;
    String email;
    String fullName;
    String phoneNumber;
}
