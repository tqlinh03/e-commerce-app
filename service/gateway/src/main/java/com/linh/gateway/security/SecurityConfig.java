package com.linh.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                // Public endpoints
                                "/api/v1/payments/payments/webhook",
                                "/api/v1/auth/**",
                                "/api/v1/customers/save-customer",
                                "/api/v1/customers/save-store",
                                "/api/v1/products/{product-id}",
                                "/api/v1/products/store/{store-Id}",
                                "/eureka/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Admin endpoints
                        .pathMatchers("/api/v1/admin/**")
                        .hasAuthority("ADMIN")

                        // Store owner endpoints
                        .pathMatchers("/api/v1/stores/**")
                        .hasAnyAuthority("STORE_OWNER", "ADMIN")

                        // Staff endpoints
                        .pathMatchers("/api/v1/staff/**")
                        .hasAnyAuthority("STAFF", "ADMIN")

                        // Customer endpoints
                        .pathMatchers(
                                "/api/v1/customers/**",
                                "/api/v1/payments/**",
                                "/api/v1/orders/**"
                        )
                        .hasAnyAuthority("CUSTOMER", "ADMIN")

                        // All other endpoints require authentication
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return serverHttpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Configure allowed origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Username", "X-Auth-Authorities"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Auth-Username", "X-Auth-Authorities"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
