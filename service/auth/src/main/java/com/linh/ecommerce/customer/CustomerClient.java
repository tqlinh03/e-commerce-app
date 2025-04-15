package com.linh.ecommerce.customer;

import com.linh.ecommerce.handler.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class CustomerClient {
    @Value("${application.config.customer-url}")
    private String customerUrl;
    private final RestTemplate restTemplate;

    public String saveInfoCustomer(CustomerInfoRequest requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<CustomerInfoRequest> requestEntity = new HttpEntity<>(requestBody, headers);
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                customerUrl + "/save-customer",
                POST,
                requestEntity,
                responseType
        );

        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException("An error occurred while processing the create account: " + responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

    public String saveInfoStoreRegistration(StoreInfoRequest requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<StoreInfoRequest> requestEntity = new HttpEntity<>(requestBody, headers);
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                customerUrl + "/save-store",
                POST,
                requestEntity,
                responseType
        );

        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException("An error occurred while processing the create store account: " + responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }
}
