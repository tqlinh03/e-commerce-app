package com.linh.ecommerce.user;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse{
        UUID id;
        String email;
        String fullName;
        String phoneNumber;
}
