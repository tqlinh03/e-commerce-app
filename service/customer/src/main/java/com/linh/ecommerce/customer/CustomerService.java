package com.linh.ecommerce.customer;

import com.linh.ecommerce.cloudflare.CloudflareService;
import com.linh.ecommerce.store.Store;
import com.linh.ecommerce.store.StoreRepository;
import com.linh.ecommerce.store.StoreRequest;
import com.linh.ecommerce.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final StoreService storeService;
    private final StoreRepository storeRepository;
    private final CloudflareService cloudflareService;

    @Value("${spring.cloudflare.domain}")
    private String rootDomain;

    public Customer createCustomer(CustomerRequest request) {
        validateCustomer(request);
        var user = mapper.toUser(request);
        return repository.save(user);
    }

    @Transactional
    public String saveInfoCustomer(CustomerRequest request) {
        // Save user 
        var customer = this.createCustomer(request);

        return customer.getId().toString();
    }

    @Transactional
    public String registerStoreOwner(StoreRegistrationRequest request) throws IOException {
        // Create request
        CustomerRequest customerRequest = CustomerRequest.builder()
                .email(request.email())
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .build();

        StoreRequest storeRequest = StoreRequest.builder()
                .storeName(request.storeName())
                .subdomain(request.subdomain())
                .build();
        // Create user
        Customer customer = this.createCustomer(customerRequest);

        // Create store
        Store store = storeService.createStore(storeRequest, customer);

        // Create subdomain
        cloudflareService.createDnsRecord(store.getSubdomain());

        // Update store url
        String storeUrl = String.format("https://%s.%s", store.getSubdomain(), rootDomain);
        store.setStoreUrl(storeUrl);
        storeRepository.save(store);

        return customer.getId().toString();
    }

    private void validateCustomer(CustomerRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }
        if (repository.existsByPhoneNumber(request.phoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
    }

    public void updateCustomer(CustomerUpdateRequest request) {
        Customer customer = repository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        customer.setPhoneNumber(request.phoneNumber());
        customer.setFullName(request.fullName());

        repository.save(customer);
    }

    public CustomerResponse findById(UUID customerId) {
        return repository.findById(customerId)
                .map(mapper::toUserResponse)
                .orElseThrow(() -> new RuntimeException("User not found"))  ;
    }
}
