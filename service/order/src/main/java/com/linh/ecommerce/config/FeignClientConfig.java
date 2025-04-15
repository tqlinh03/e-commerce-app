package com.linh.ecommerce.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // Get the Authorization header from the incoming request
                String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
                
                if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                    // Add the Authorization header to the outgoing request
                    requestTemplate.header(AUTHORIZATION_HEADER, authorizationHeader);
                }
                
                // Always add content type
                requestTemplate.header("Content-Type", "application/json");
            }
        };
    }
}
