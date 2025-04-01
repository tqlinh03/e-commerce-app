package com.linh.ecommerce.user;

import com.linh.ecommerce.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public User createUser(UserRequest request, Role role) {
        validateCustomer(request);

        var user = mapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setVerified(false);
        user.setRole(role);

        return repository.save(user);
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // Check if current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Wrong password");
        }

        // Check if the two new password are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new BadCredentialsException("Password are not the same");
        }

        // Update the password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        // Save the new password
        repository.save(user);
    }

    private void validateCustomer(UserRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }
        if (repository.existsByPhoneNumber(request.phoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
    }

    public void updateCustomer(UserUpdateRequest request) {
        User user = repository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPhoneNumber(request.phoneNumber());
        user.setFullName(request.fullName());

        repository.save(user);
    }

    public UserResponse findById(UUID customerId) {
        return repository.findById(customerId)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new RuntimeException("User not found"))  ;
    }

    public void deleteCustomer(UUID customerId) {
        repository.deleteById(customerId);
    }
}
