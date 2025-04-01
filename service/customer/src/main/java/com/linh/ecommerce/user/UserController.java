package com.linh.ecommerce.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping()
    public ResponseEntity<Void> updateCustomer(
            @RequestBody @Valid UserUpdateRequest request
    ) {
        userService.updateCustomer(request);
        return ResponseEntity.accepted().build();
    }


    @GetMapping("/{customer-id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable("customer-id") UUID customerId
    ) {
        return ResponseEntity.ok(userService.findById(customerId));
    }

//    @DeleteMapping("/{customer-id}")
//    public ResponseEntity<Void> delete(
//            @PathVariable("customer-id") UUID customerId
//    ) {
//        userService.deleteCustomer(customerId);
//        return ResponseEntity.accepted().build();
//    }
}
