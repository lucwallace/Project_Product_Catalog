package org.product.catalog.config;

import org.product.catalog.exception.CustomAccessDeniedHandler;
import org.product.catalog.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/category/findAll",
            "/category/list",
            "/category/findById/**",
            "/category/findByName",
            "/product/findAll",
            "/product/list",
            "/produc/findById/**",
            "/product/findByName",
            "/product/findByCategory",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    private static final String[] PRIVATE_ENDPOINTS = {
            "/category/create",
            "/category/update/**",
            "/category/delete/**",
            "/product/create",
            "/product/update/**",
            "/product/delete/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(PRIVATE_ENDPOINTS).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new JwtAuthConverter())
                        )
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        return http.build();
    }
}