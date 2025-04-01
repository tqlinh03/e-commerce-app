package com.linh.ecommerce.cloudflare;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudflareService {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper;

    @Value("${spring.cloudflare.api.token}")
    private String cloudflareApiToken;

    @Value("${spring.cloudflare.zone.id}")
    private String zoneId;

    @Value("${spring.cloudflare.domain}")
    private String rootDomain;

    public void createDnsRecord(String subdomain) throws IOException {
        String fullDomain = subdomain + "." + rootDomain;

        if (dnsRecordExists(fullDomain)) {
            throw new RuntimeException("Subdomain already exists: " + fullDomain);
        }

        // Tạo route cho Worker
        Map<String, Object> routePayload = new HashMap<>();
        routePayload.put("pattern", fullDomain + "/*");
        routePayload.put("script", "e-commerce-app-ui");

        String routeJson = objectMapper.writeValueAsString(routePayload);

        RequestBody routeBody = RequestBody.create(routeJson, MediaType.get("application/json"));
        Request routeRequest = new Request.Builder()
                .url("https://api.cloudflare.com/client/v4/zones/" + zoneId + "/workers/routes")
                .post(routeBody)
                .addHeader("Authorization", "Bearer " + cloudflareApiToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response routeResponse = client.newCall(routeRequest).execute()) {
            if (!routeResponse.isSuccessful()) {
                throw new IOException("Failed to create Worker route: " + routeResponse.body().string());
            }
        }
    }

    private boolean dnsRecordExists(String fullDomain) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.cloudflare.com/client/v4/zones/" + zoneId + "/dns_records?type=CNAME&name=" + fullDomain)
                .get()
                .addHeader("Authorization", "Bearer " + cloudflareApiToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to check DNS record: " + response.body().string());
            }
            String responseBody = response.body().string();
            Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
            return !((List<?>) result.get("result")).isEmpty();
        }
    }
}
