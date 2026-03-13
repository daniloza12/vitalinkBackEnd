package com.vitalink.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// CORS is now configured in SecurityConfig via CorsConfigurationSource bean.
@Configuration
public class CorsConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
